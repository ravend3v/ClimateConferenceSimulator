package utils;

import simulation.model.ServicePoint;
import simulation.framework.Queue;
import simulation.model.Customer;
import simulation.framework.Clock;

public class SimulationResults {
    private final ServicePoint[] servicePoints;
    private Queue queue;

    public SimulationResults(ServicePoint[] servicePoints, Queue queue) {
        this.servicePoints = servicePoints;
        this.queue = queue;
    }

    public void calculateResults() {
        double totalTime = Clock.getInstance().getTime();

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
    }

    public String getResultsAsString() {
        double totalTime = Clock.getInstance().getTime();
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

        return "Simulation ended at " + totalTime + "\n" +
                "Results:\n" +
                "A (Arrived Clients Count): " + arrivedClientsCount + "\n" +
                "C (Completed Clients Count): " + completedClientsCount + "\n" +
                "B (Active Service Time total): " + activeServiceTime + "\n" +
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
}
