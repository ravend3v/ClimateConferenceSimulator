package simulation.model;

import simulation.framework.*;

// Eduni distributions
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

// Utils
import utils.NumberUtils;

public class OwnMotor extends Motor {

	private Queue queue;
	private ServicePoint[] servicePoints;

	public OwnMotor(){
		queue = new Queue(new Negexp(5, 5), eventList, EventType.ARR1);

		servicePoints = new ServicePoint[4];

		servicePoints[0] = new EventEntrance(new Normal(10, 10), eventList, EventType.DEP1,2, 0, queue);
		servicePoints[1] = new RenewableEnergyStand(new Normal(10, 10), eventList, EventType.DEP2, 4, 0, queue);
		servicePoints[2] = new ClimateShowcaseRoom(new Normal(5, 3), eventList, EventType.DEP3, 5, 0, queue);
		servicePoints[3] = new MainStage(new Normal(5, 3), eventList, EventType.DEP4, 10, 0, queue);
	}

	@Override
	protected void initialize() {
		queue.generateNext(); // First arrival to the system
	}

	@Override
	protected void executeEvent(Event event) {  // Phase B events
		Customer customer;

		//Debug statement
		System.out.println("Processing event: " + event.getType() + " at time: " + Clock.getInstance().getTime());

		switch ((EventType) event.getType()) {

			case ARR1:
				customer = new Customer();
				System.out.println("New customer arrived: " + customer.getId() + " at time: " + Clock.getInstance().getTime());
				servicePoints[0].addToQueue(customer);
				queue.generateNext();
				break;
			case DEP1:
				customer = servicePoints[0].removeFromQueue();
				System.out.println("Customer " + customer.getId() + " moving to renewable energy stand at time: " + Clock.getInstance().getTime());
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[1].addToQueue(customer);
				break;
			case DEP2:
				customer = servicePoints[1].removeFromQueue();
				System.out.println("Customer " + customer.getId() + " moving to climate showcase room at time: " + Clock.getInstance().getTime());
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[2].addToQueue(customer);
				break;
			case DEP3:
				customer = servicePoints[2].removeFromQueue();
				System.out.println("Customer " + customer.getId() + " moving to main stage at time: " + Clock.getInstance().getTime());
				customer.setExitTime(Clock.getInstance().getTime());
				servicePoints[3].addToQueue(customer);
				break;
			case DEP4:
				customer = servicePoints[3].removeFromQueue();
				System.out.println("Customer " + customer.getId() + " completed at time: " + Clock.getInstance().getTime());
				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();
				queue.addCompleted(customer.getExitTime() - customer.getArrivalTime());
				break;

		}
	}

	@Override
	protected void tryCEvents() {
		for (ServicePoint p : servicePoints) {
			if (!p.isBusy() && p.hasQueue()) {
				//Trace.out(Trace.Level.INFO, "Calling startService() for " + p.getClass().getSimpleName());
				p.startService();
			}
		}
	}


	@Override
	protected void results() {
		double totalTime = Clock.getInstance().getTime();
		int arrivedClientsCount = queue.getArrivalCount();
		int completedClientsCount = queue.getCompletedCount();
		double activeServiceTime = servicePoints[0].getBusyTime();

		for (ServicePoint sp : servicePoints) {
			activeServiceTime += sp.getBusyTime();
		}

		double cumulativeResponseTime = queue.getCumulativeResponseTime();
		double servicePointUtilization = NumberUtils.round((activeServiceTime / totalTime), 2);
		double serviceThroughput = NumberUtils.round((completedClientsCount / totalTime), 2);
		double averageServiceTime = NumberUtils.round((completedClientsCount > 0 ? activeServiceTime / completedClientsCount : 0), 2);
		double averageResponseTime = NumberUtils.round((completedClientsCount > 0 ? cumulativeResponseTime / completedClientsCount : 0), 2);
		double averageQueueLength = NumberUtils.round((cumulativeResponseTime / totalTime), 2);

		System.out.println("Simulation ended at " + totalTime);
		System.out.println("Results:");
		System.out.println("A (Arrived Clients Count): " + arrivedClientsCount);
		System.out.println("C (Completed Clients Count): " + completedClientsCount);
		System.out.println("B (Active Service Time): " + activeServiceTime);
		System.out.println("T (Total Simulation Time): " + totalTime);
		System.out.println("U (Service Point Utilization): " + servicePointUtilization);
		System.out.println("X (Service Throughput): " + serviceThroughput);
		System.out.println("S (Average Service Time): " + averageServiceTime);
		System.out.println("R (Average Response Time): " + averageResponseTime);
		System.out.println("N (Average Queue Length): " + averageQueueLength);

	}

}