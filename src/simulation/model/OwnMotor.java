package simulation.model;

import simulation.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

public class OwnMotor extends Motor {

	private Queue queue;

	private ServicePoint[] servicePoints;

	public OwnMotor(){

		servicePoints = new ServicePoint[3];

		servicePoints[0] = new EventEntrance(new Normal(10, 6), eventList, EventType.DEP1,2);
		servicePoints[1] = new ServicePoint(new Normal(10, 10), eventList, EventType.DEP2);
		servicePoints[2] = new ServicePoint(new Normal(5, 3), eventList, EventType.DEP3);

		queue = new Queue(new Negexp(15, 5), eventList, EventType.ARR1);

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

				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();
		}
	}

	@Override
	protected void tryCEvents() {
		for (ServicePoint p : servicePoints) {
			if (!p.isBusy() && p.hasQueue() && !(p instanceof EventEntrance && ((EventEntrance) p).getCurrentCustomerCount() > 0)) {
				Trace.out(Trace.Level.INFO, "Calling startService() for " + p.getClass().getSimpleName());
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