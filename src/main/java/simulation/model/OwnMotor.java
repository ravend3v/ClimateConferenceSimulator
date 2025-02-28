// In OwnMotor.java
package simulation.model;

import simulation.controller.IControllerM;
import simulation.framework.*;
import simulation.view.ServicePointView;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import utils.NumberUtils;

public class OwnMotor extends Motor {

	private final Queue queue;
	private final ServicePoint[] servicePoints;
	private final ServicePointView[] servicePointViews;

    public OwnMotor(IControllerM controller,int[] capacities,ServicePointView[] servicePointViews) {
		super(controller);
        this.queue = new Queue(new Negexp(5, 5), eventList, EventType.ARR1);
		this.servicePointViews = servicePointViews;
		servicePoints = new ServicePoint[4];

		servicePoints[0] = new EventEntrance(new Normal(10, 10), eventList, EventType.DEP1, capacities[0], 0, queue);
		servicePoints[1] = new RenewableEnergyStand(new Normal(10, 10), eventList, EventType.DEP2, capacities[1], 0, queue);
		servicePoints[2] = new ClimateShowcaseRoom(new Normal(5, 3), eventList, EventType.DEP3, capacities[2], 0, queue);
		servicePoints[3] = new MainStage(new Normal(5, 3), eventList, EventType.DEP4, capacities[3], 0, queue);
	}

	public ServicePointView[] getServicePointViews() {
		return servicePointViews;
	}

	@Override
	protected void initialize() {
		queue.generateNext();
	}

	@Override
	protected void executeEvent(Event event) {  // Phase B events
		Customer customer;

		switch ((EventType) event.getType()) {
			case ARR1:
				customer = new Customer();
				servicePoints[0].addToQueue(customer);
				controller.showNewCustomer(customer.getId());
				System.out.println("arr1: "+customer.getId());
				queue.generateNext();
				queue.addArrival();
				break;
			case DEP1:
				queue.addArrival();
				customer = servicePoints[0].removeFromQueue();
				controller.showCustomer(customer.getId(), 0,1);
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[1].addToQueue(customer);
				System.out.println("dep1: "+customer.getId());

				break;
			case DEP2:
				customer = servicePoints[1].removeFromQueue();
				controller.showCustomer(customer.getId(), 1,2);
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[2].addToQueue(customer);
				System.out.println("dep2: "+customer.getId());

				break;
			case DEP3:
				customer = servicePoints[2].removeFromQueue();
				controller.showCustomer(customer.getId(), 2,3);
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[3].addToQueue(customer);
				System.out.println("dep3: "+customer.getId());

				break;
			case DEP4:
				customer = servicePoints[3].removeFromQueue();
				controller.customerExit(customer.getId());
				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();
				queue.addCompleted(customer.getExitTime() - customer.getArrivalTime());
				System.out.println("exit: "+customer.getId());

				break;
			default:
				break;
		}
	}

	@Override
	protected void tryCEvents() {
		for (ServicePoint p : servicePoints) {
			if (!p.isBusy() && p.hasQueue()) {
				System.out.println("pöö");
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
		double activeServiceTimeEntrance = servicePoints[0].getCompletedServices() != 0 ? servicePoints[0].getBusyTime() / servicePoints[0].getCompletedServices() : 0.0;
		double activeServiceTimeRenewable = servicePoints[1].getCompletedServices() != 0 ? servicePoints[1].getBusyTime() / servicePoints[1].getCompletedServices() : 0.0;
		double activeServiceTimeShowroom = servicePoints[2].getCompletedServices() != 0 ? servicePoints[2].getBusyTime() / servicePoints[2].getCompletedServices() : 0.0;
		double activeServiceTimeMain = servicePoints[3].getCompletedServices() != 0 ? servicePoints[3].getBusyTime() / servicePoints[3].getCompletedServices() : 0.0;
		double allAverage = activeServiceTimeEntrance + activeServiceTimeRenewable + activeServiceTimeShowroom + activeServiceTimeMain;

		for (ServicePoint sp : servicePoints) {
			activeServiceTime += sp.getBusyTime();
		}

		double cumulativeResponseTime = queue.getCumulativeResponseTime();

		// Debug statements to print values before rounding
		System.out.println("Debug: activeServiceTime / totalTime = " + (totalTime != 0 ? activeServiceTime / totalTime : 0.0));
		System.out.println("Debug: completedClientsCount / totalTime = " + (totalTime != 0 ? completedClientsCount / totalTime : 0.0));
		System.out.println("Debug: allAverage / 4 = " + (4 != 0 ? allAverage / 4 : 0.0));
		System.out.println("Debug: Customer.getThroughPut() = " + Customer.getThroughPut());
		System.out.println("Debug: cumulativeResponseTime / totalTime = " + (totalTime != 0 ? cumulativeResponseTime / totalTime : 0.0));

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

	@Override
	protected void updateUI(double time) {
		System.out.println("Simulation time: " + time);
	}

}