package simulation.model;

import simulation.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

public class OwnMotor extends Motor {

	private Queue queue;

	private ServicePoint[] servicePoints;

	public OwnMotor(){

		servicePoints = new ServicePoint[4];

		servicePoints[0] = new EventEntrance(new Normal(10, 10), eventList, EventType.DEP1,2, 0);
		servicePoints[1] = new RenewableEnergyStand(new Normal(10, 10), eventList, EventType.DEP2, 4, 0);
		servicePoints[2] = new ClimateShowcaseRoom(new Normal(5, 3), eventList, EventType.DEP3, 5, 0);
		servicePoints[3] = new MainStage(new Normal(5, 3), eventList, EventType.DEP3, 10, 0);

		queue = new Queue(new Negexp(5, 5), eventList, EventType.ARR1);

	}

	@Override
	protected void initialize() {
		queue.generateNext(); // First arrival to the system
	}

	@Override
	protected void executeEvent(Event event) {  // Phase B events

		Customer customer;
		switch ((EventType) event.getType()) {

			case ARR1:
				servicePoints[0].addToQueue(new Customer());
				queue.generateNext();
				break;
			case DEP1:
				customer = (Customer) servicePoints[0].removeFromQueue();
				servicePoints[1].addToQueue(customer);
				break;
			case DEP2:
				customer = (Customer) servicePoints[1].removeFromQueue();
				servicePoints[2].addToQueue(customer);
				break;
			case DEP3:
				customer = (Customer) servicePoints[2].removeFromQueue();
				servicePoints[3].addToQueue(customer);
				break;
			case DEP4:
				customer = (Customer) servicePoints[3].removeFromQueue();

				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();

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
		System.out.println("Simulation ended at " + Clock.getInstance().getTime());
		System.out.println("Results ... still missing");
	}

}