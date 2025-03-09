package utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import javafx.application.Platform;
import org.bson.Document;
import database.DatabaseUtils;

import simulation.model.CustomerType;
import simulation.model.ServicePoint;
import simulation.framework.Queue;
import simulation.model.Customer;
import simulation.framework.Clock;

import java.util.HashMap;
import java.util.concurrent.*;

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

            System.out.println("Active service times for each service point: ");

            System.out.println("- Event Entrance busytime: " + activeServiceTimeEntrance + ", completed services: " + servicePoints[0].getCompletedServices());
            System.out.println("- Renewable energy stand busytime: " + activeServiceTimeRenewable + ", completed services: " + servicePoints[1].getCompletedServices());
            System.out.println("- Climate showcase room busytime: " + activeServiceTimeShowroom + ", completed services: " + servicePoints[2].getCompletedServices());
            System.out.println("- Main stage busytime: " + activeServiceTimeMain + ", completed services: " + servicePoints[3].getCompletedServices());

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
                    averageQueueLength
            );

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public String getResultsAsString() {
        double totalTime = Clock.getInstance().getTime();
        HashMap<CustomerType, Integer> customerTypeCounts = Customer.getTypeCount();
        int arrivedClientsCount = Customer.arrivedCount();
        int completedClientsCount = Customer.getCompletedCount();
        double activeServiceTime = 0.0;
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

        return
                "Simulation ended at " + totalTime + "\n" +
                "Results:\n" +
                "A (Arrived Clients Count): " + arrivedClientsCount + "\n" +
                "C (Completed Clients Count): " + completedClientsCount + "\n" +
                "B (Active Service Time total): " + activeServiceTime + "\n" +
                        "Customer type counts: "+"\n" +
                        "- Students: "+customerTypeCounts.get(CustomerType.STUDENT)+"\n" +
                        "- Deciders: "+customerTypeCounts.get(CustomerType.DECIDER)+"\n" +
                        "- Experts: "+customerTypeCounts.get(CustomerType.EXPERT)+"\n" +
                "Active service times for each service point:\n" +
                "- Event Entrance busytime: " + activeServiceTimeEntrance + ", completed services: " + servicePoints[0].getCompletedServices() + "\n" +
                "- Renewable energy stand busytime: " + activeServiceTimeRenewable + ", completed services: " + servicePoints[1].getCompletedServices() + "\n" +
                "- Climate showcase room busytime: " + activeServiceTimeShowroom + ", completed services: " + servicePoints[2].getCompletedServices() + "\n" +
                "- Main stage busytime: " + activeServiceTimeMain + ", completed services: " + servicePoints[3].getCompletedServices() + "\n" +
                "T (Total Simulation Time): " + totalTime + "\n" +
                "U (Service Point Utilization): " + servicePointUtilization + "\n" +
                "X (Service Throughput): " + serviceThroughput + "\n" +
                "S (Average Service Time): " + averageServiceTime + "\n" +
                "R (Average Response Time): " + averageResponseTime + "\n" +
                "N (Average Queue Length): " + averageQueueLength;

    }


    // Method to save the results to the database
    private void saveResultsToDatabase(int arrivedClientsCount, int completedClientsCount, double activeServiceTime, double activeServiceTimeEntrance, double activeServiceTimeRenewable, double activeServiceTimeShowroom, double activeServiceTimeMain, double totalTime, double servicePointUtilization, double serviceThroughput, double averageServiceTime, double averageResponseTime, double averageQueueLength) {
        new Thread(() -> {
            try {
                MongoDatabase database = DatabaseUtils.getDatabase(EnvUtils.getEnv("DB_NAME"));
                if (database == null) {
                    System.out.println("Failed to connect to the database.");
                    return;
                }
                System.out.println("Connected to the database.");

                MongoCollection<Document> collection = database.getCollection("Results");
                if (collection == null) {
                    System.out.println("Collection 'Results' does not exist.");
                    return;
                }
                System.out.println("Collection 'Results' exists.");

                Document update = new Document("$set", new Document("arrivedClientsCount", arrivedClientsCount)
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
                        .append("averageQueueLength", averageQueueLength));

                var result = collection.insertOne(update);
                System.out.println("Update result: " + result);

            } catch (Exception e) {
                System.out.println("Error while saving results to the database");
                e.printStackTrace();
            }
        }).start();
    }
    public void fetchResultsFromDatabase() {
        new Thread(() -> {
            try {
                MongoDatabase database = DatabaseUtils.getDatabase(EnvUtils.getEnv("DB_NAME"));
                MongoCollection<Document> collection = database.getCollection("Results");

                Document result = collection.find().first();
                if (result != null) {
                    Platform.runLater(() -> {
                        System.out.println("Fetched Results:");
                        System.out.println("A (Arrived Clients Count): " + result.getInteger("arrivedClientsCount"));
                        System.out.println("C (Completed Clients Count): " + result.getInteger("completedClientsCount"));
                        System.out.println("B (Active Service Time total): " + result.getDouble("activeServiceTime"));
                        System.out.println("Active service times for each service point:");
                        System.out.println("- Event Entrance busytime: " + result.getDouble("activeServiceTimeEntrance"));
                        System.out.println("- Renewable energy stand busytime: " + result.getDouble("activeServiceTimeRenewable"));
                        System.out.println("- Climate showcase room busytime: " + result.getDouble("activeServiceTimeShowroom"));
                        System.out.println("- Main stage busytime: " + result.getDouble("activeServiceTimeMain"));
                        System.out.println("T (Total Simulation Time): " + result.getDouble("totalTime"));
                        System.out.println("U (Service Point Utilization): " + result.getDouble("servicePointUtilization"));
                        System.out.println("X (Service Throughput): " + result.getDouble("serviceThroughput"));
                        System.out.println("S (Average Service Time): " + result.getDouble("averageServiceTime"));
                        System.out.println("R (Average Response Time): " + result.getDouble("averageResponseTime"));
                        System.out.println("N (Average Queue Length): " + result.getDouble("averageQueueLength"));
                    });
                } else {
                    System.out.println("No results found in the database.");
                }
            } catch (Exception e) {
                System.out.println("Error while fetching results from the database");
                e.printStackTrace();
            }
        }).start();
    }

}
