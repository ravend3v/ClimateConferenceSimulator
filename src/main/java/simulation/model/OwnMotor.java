// In OwnMotor.java
package simulation.model;

import javafx.application.Platform;
import simulation.controller.IControllerM;
import simulation.framework.*;
import simulation.view.CustomerView;
import simulation.view.ServicePointView;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import utils.NumberUtils;
import java.util.HashMap;
import java.util.Map;
import simulation.controller.IControllerM;

public class OwnMotor extends Motor {

	private Queue queue;
	private ServicePoint[] servicePoints;
	//protected ServicePointView[] servicePointViews;
	//private Map<Integer, CustomerView> customerViews;

	public OwnMotor(IControllerM controller,int[] capacities) {
		super(controller);
		this.queue = new Queue(new Negexp(5, 5), eventList, EventType.ARR1);
		//this.servicePointViews = servicePointViews;
		//this.customerViews = new HashMap<>();

		servicePoints = new ServicePoint[4];

		servicePoints[0] = new EventEntrance(new Normal(10, 10), eventList, EventType.DEP1, capacities[0], 0, queue);
		servicePoints[1] = new RenewableEnergyStand(new Normal(10, 10), eventList, EventType.DEP2, capacities[1], 0, queue);
		servicePoints[2] = new ClimateShowcaseRoom(new Normal(5, 3), eventList, EventType.DEP3, capacities[2], 0, queue);
		servicePoints[3] = new MainStage(new Normal(5, 3), eventList, EventType.DEP4, capacities[3], 0, queue);
	}

	@Override
	protected void initialize() {
		queue.generateNext(); // First arrival to the system
	}

	@Override
	protected void executeEvent(Event event) {  // Phase B events
		Customer customer;
		CustomerView customerView;

		switch ((EventType) event.getType()) {
			case ARR1:
				customer = new Customer();
				servicePoints[0].addToQueue(customer);
				controller.showNewCustomer(customer.getId());
				queue.generateNext();
				queue.addArrival();
				break;
			case DEP1:
				queue.addArrival();
				customer = servicePoints[0].removeFromQueue();
				controller.showCustomer(customer.getId(), 0,1);
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[1].addToQueue(customer);
				break;
			case DEP2:
				customer = servicePoints[1].removeFromQueue();
				controller.showCustomer(customer.getId(), 1,2);
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[2].addToQueue(customer);
				break;
			case DEP3:
				customer = servicePoints[2].removeFromQueue();
				controller.showCustomer(customer.getId(), 2,3);
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[3].addToQueue(customer);
				break;
			case DEP4:
				customer = servicePoints[3].removeFromQueue();
				controller.customerExit(customer.getId());
				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();
				queue.addCompleted(customer.getExitTime() - customer.getArrivalTime());
				break;
			default:
				break;
		}
	}

	@Override
	protected void tryCEvents() {
		for (ServicePoint p : servicePoints) {
			if (!p.isBusy() && p.hasQueue()) {
				p.startService();
			}
		}
	}

	@Override
	protected void results() {

		double totalTime = Clock.getInstance().getTime();

		int arrivedClientsCount = Customer.arrivedCount();
		int completedClientsCount = Customer.getCompletedCount();
		double activeServiceTime = 0.0;
		double activeServiceTimeEntrance = servicePoints[0].getBusyTime() / servicePoints[0].getCompletedServices();
		double activeServiceTimeRenewable = servicePoints[1].getBusyTime() / servicePoints[1].getCompletedServices();
		double activeServiceTimeShowroom = servicePoints[2].getBusyTime() / servicePoints[2].getCompletedServices();
		double activeServiceTimeMain = servicePoints[3].getBusyTime() / servicePoints[3].getCompletedServices();
		double allAverage = activeServiceTimeEntrance + activeServiceTimeRenewable + activeServiceTimeShowroom + activeServiceTimeMain;

		for (ServicePoint sp : servicePoints) {
			activeServiceTime += sp.getBusyTime();
		}

		double cumulativeResponseTime = queue.getCumulativeResponseTime();
		double servicePointUtilization = NumberUtils.round((activeServiceTime / totalTime), 2);
		double serviceThroughput = NumberUtils.round((completedClientsCount / totalTime), 2);
		double averageServiceTime = NumberUtils.round((allAverage / 4), 2);
		double averageResponseTime = NumberUtils.round(Customer.getThroughPut(), 2);
		double averageQueueLength = NumberUtils.round((cumulativeResponseTime / totalTime), 2);

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

	@Override
	protected void updateUI(double time) {
		System.out.println("Simulation time: " + time);
	}


}