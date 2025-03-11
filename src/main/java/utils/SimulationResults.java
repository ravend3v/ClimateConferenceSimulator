package utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import javafx.application.Platform;
//import static com.mongodb.client.model.Sorts.descending;
import org.bson.Document;
import database.DatabaseUtils;

import simulation.model.CustomerType;
import simulation.model.ServicePoint;
import simulation.framework.Queue;
import simulation.model.Customer;
import simulation.framework.Clock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static com.mongodb.client.model.Indexes.descending;

public class SimulationResults {
    private final ServicePoint[] servicePoints;
    private Queue queue;

    public SimulationResults(ServicePoint[] servicePoints, Queue queue) {
        this.servicePoints = servicePoints;
        this.queue = queue;
        reset();
    }

    public void reset() {
        for (ServicePoint sp : servicePoints) {
            sp.reset();
        }
        queue.resetStatistics();
        Customer.resetCounts();
        Clock.getInstance().reset();
    }

    public void calculateResults() {
        ExecutorService executor = Executors.newFixedThreadPool(servicePoints.length);

        try {
            double totalTime = Clock.getInstance().getTime();
            HashMap<CustomerType, Integer> customerTypeCounts = Customer.getTypeCount();
            int arrivedClientsCount = Customer.arrivedCount();
            int completedClientsCount = Customer.getCompletedCount();
            double activeServiceTime = 0.0;
            int eventEntranceCapacity = servicePoints[0].getCapacity();
            int renewableEnergyStandCapacity = servicePoints[1].getCapacity();
            int showroomCapacity = servicePoints[2].getCapacity();
            int mainStageCapacity = servicePoints[3].getCapacity();
            int[] capacities = {eventEntranceCapacity, renewableEnergyStandCapacity, showroomCapacity,mainStageCapacity};
            double activeServiceTimeEntrance = servicePoints[0].getCompletedServices() != 0 ? servicePoints[0].getBusyTime() / servicePoints[0].getCompletedServices() : 0.0;
            double activeServiceTimeRenewable = servicePoints[1].getCompletedServices() != 0 ? servicePoints[1].getBusyTime() / servicePoints[1].getCompletedServices() : 0.0;
            double activeServiceTimeShowroom = servicePoints[2].getCompletedServices() != 0 ? servicePoints[2].getBusyTime() / servicePoints[2].getCompletedServices() : 0.0;
            double activeServiceTimeMain = servicePoints[3].getCompletedServices() != 0 ? servicePoints[3].getBusyTime() / servicePoints[3].getCompletedServices() : 0.0;
            double allAverage = activeServiceTimeEntrance + activeServiceTimeRenewable + activeServiceTimeShowroom + activeServiceTimeMain;

            for (ServicePoint sp : servicePoints) {
                activeServiceTime += sp.getBusyTime();
            }

            double cumulativeResponseTime = queue.getCumulativeResponseTime();

            double servicePointUtilization = NumberUtils.round((totalTime != 0 ? activeServiceTime / totalTime : 0.0), 2);
            double serviceThroughput = NumberUtils.round((totalTime != 0 ? completedClientsCount / totalTime : 0.0), 2);
            double averageServiceTime = NumberUtils.round((4 != 0 ? allAverage / 4 : 0.0), 2);
            double averageResponseTime = NumberUtils.round(Customer.getThroughPut(), 2);
            double averageQueueLength = NumberUtils.round((totalTime != 0 ? cumulativeResponseTime / totalTime : 0.0), 2);

            System.out.println("Simulation ended at " + totalTime);
            System.out.println("Results:");
            System.out.println("A (Arrived Clients Count): " + arrivedClientsCount);
            System.out.println("C (Completed Clients Count): " + completedClientsCount);
            System.out.println("B (Active Service Time total): " + activeServiceTime);

            System.out.println("Customer type counts: ");
            System.out.println("- Students: "+customerTypeCounts.get(CustomerType.STUDENT));
            System.out.println("- Deciders: "+customerTypeCounts.get(CustomerType.DECIDER));
            System.out.println("- Experts: "+customerTypeCounts.get(CustomerType.EXPERT));

            System.out.println("Active service times and capacities for each service point: ");

            System.out.println("- Event Entrance busytime: " + activeServiceTimeEntrance + ", completed services: " + servicePoints[0].getCompletedServices() + ", capacity: " + servicePoints[0].getCapacity());
            System.out.println("- Renewable energy stand busytime: " + activeServiceTimeRenewable + ", completed services: " + servicePoints[1].getCompletedServices()+ ", capacity: " + servicePoints[1].getCapacity());
            System.out.println("- Climate showcase room busytime: " + activeServiceTimeShowroom + ", completed services: " + servicePoints[2].getCompletedServices()+ ", capacity: " + servicePoints[2].getCapacity());
            System.out.println("- Main stage busytime: " + activeServiceTimeMain + ", completed services: " + servicePoints[3].getCompletedServices()+ ", capacity: " + servicePoints[3].getCapacity());

            System.out.println("T (Total Simulation Time): " + totalTime);
            System.out.println("U (Service Point Utilization): " + servicePointUtilization);
            System.out.println("X (Service Throughput): " + serviceThroughput);
            System.out.println("S (Average Service Time): " + averageServiceTime);
            System.out.println("R (Average Response Time): " + averageResponseTime);
            System.out.println("N (Average Queue Length): " + averageQueueLength);

            // Call the method to save the results to the database
            saveResultsToDatabase(
                    arrivedClientsCount,
                    completedClientsCount,
                    activeServiceTime,
                    activeServiceTimeEntrance,
                    activeServiceTimeRenewable,
                    activeServiceTimeShowroom,
                    activeServiceTimeMain,
                    totalTime,
                    servicePointUtilization,
                    serviceThroughput,
                    averageServiceTime,
                    averageResponseTime,
                    averageQueueLength,
                    customerTypeCounts,
                    capacities
            );

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }



    private void saveResultsToDatabase(int arrivedClientsCount, int completedClientsCount, double activeServiceTime, double activeServiceTimeEntrance, double activeServiceTimeRenewable, double activeServiceTimeShowroom, double activeServiceTimeMain, double totalTime, double servicePointUtilization, double serviceThroughput, double averageServiceTime, double averageResponseTime, double averageQueueLength,HashMap<CustomerType, Integer> customerTypeCounts,int[] capacities) {
        try {
            MongoDatabase database = DatabaseUtils.getDatabase(EnvUtils.getEnv("DB_NAME"));
            MongoCollection<Document> collection = database.getCollection("Results");

            Document update = new Document("arrivedClientsCount", arrivedClientsCount)
                    .append("completedClientsCount", completedClientsCount)
                    .append("activeServiceTime", activeServiceTime)
                    .append("activeServiceTimeEntrance", activeServiceTimeEntrance)
                    .append("activeServiceTimeRenewable", activeServiceTimeRenewable)
                    .append("activeServiceTimeShowroom", activeServiceTimeShowroom)
                    .append("activeServiceTimeMain", activeServiceTimeMain)
                    .append("totalTime", totalTime)
                    .append("servicePointUtilization", servicePointUtilization)
                    .append("serviceThroughput", serviceThroughput)
                    .append("averageServiceTime", averageServiceTime)
                    .append("averageResponseTime", averageResponseTime)
                    .append("averageQueueLength", averageQueueLength)
                    .append("studentCount", customerTypeCounts.get(CustomerType.STUDENT))
                    .append("deciderCount", customerTypeCounts.get(CustomerType.DECIDER))
                    .append("expertCount", customerTypeCounts.get(CustomerType.EXPERT))
                    .append("eventEntranceCapacity",capacities[0])
                    .append("renewableEnergystandCapacity",capacities[1])
                    .append("showroomCapacity",capacities[2])
                    .append("mainStageCapacity",capacities[3]);


            collection.insertOne(update);
            System.out.println("Data saved to database.");
        } catch (Exception e) {
            System.out.println("Error while saving results to the database");
            e.printStackTrace();
        }
    }





    public String fetchResultsFromDatabase() {
        StringBuilder resultsString = new StringBuilder();
        try {
            MongoDatabase database = DatabaseUtils.getDatabase(EnvUtils.getEnv("DB_NAME"));
            MongoCollection<Document> collection = database.getCollection("Results");

            // Hakee viimeksi lisätyn dokumentin
            Document result = collection.find()
                    .sort(descending("_id")) // Lajittelee laskevasti _id:n mukaan
                    .first();

            if (result != null) { // Tarkistus, ettei result ole null
                resultsString.append("A (Arrived Clients Count): ").append(result.getInteger("arrivedClientsCount")).append("\n");
                resultsString.append("C (Completed Clients Count): ").append(result.getInteger("completedClientsCount")).append("\n");
                resultsString.append("Customer type counts: ").append("\n");
                resultsString.append("- Students: ").append(result.getInteger("studentCount")).append("\n");
                resultsString.append("- Deciders: ").append(result.getInteger("deciderCount")).append("\n");
                resultsString.append("- Experts: ").append(result.getInteger("expertCount")).append("\n");
                resultsString.append("B (Active Service Time total): ").append(result.getDouble("activeServiceTime")).append("\n");
                resultsString.append("Active service times for each service point:\n");
                resultsString.append("- Event Entrance busytime: ").append(result.getDouble("activeServiceTimeEntrance")).append(", Capacity: ").append(result.getInteger("eventEntranceCapacity")).append("\n");
                resultsString.append("- Renewable energy stand busytime: ").append(result.getDouble("activeServiceTimeRenewable")).append(", Capacity: ").append(result.getInteger("renewableEnergystandCapacity")).append("\n");
                resultsString.append("- Climate showcase room busytime: ").append(result.getDouble("activeServiceTimeShowroom")).append(", Capacity: ").append(result.getInteger("showroomCapacity")).append("\n");
                resultsString.append("- Main stage busytime: ").append(result.getDouble("activeServiceTimeMain")).append(", Capacity: ").append(result.getInteger("mainStageCapacity")).append("\n");
                resultsString.append("T (Total Simulation Time): ").append(result.getDouble("totalTime")).append("\n");
                resultsString.append("U (Service Point Utilization): ").append(result.getDouble("servicePointUtilization")).append("\n");
                resultsString.append("X (Service Throughput): ").append(result.getDouble("serviceThroughput")).append("\n");
                resultsString.append("S (Average Service Time): ").append(result.getDouble("averageServiceTime")).append("\n");
                resultsString.append("R (Average Response Time): ").append(result.getDouble("averageResponseTime")).append("\n");
                resultsString.append("N (Average Queue Length): ").append(result.getDouble("averageQueueLength")).append("\n");
            } else {
                resultsString.append("No results found in the database.");
            }
        } catch (Exception e) {
            resultsString.append("Error while fetching results from the database");
            e.printStackTrace();
        }
        return resultsString.toString();
    }



}


