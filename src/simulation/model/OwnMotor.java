package simulation.model;

import simulation.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

public class OwnMotor extends Motor {

	private Queue queue;
	private ServicePoint[] servicePoints;
	private MainStage mainStage; // Adding MainStage separately

	public OwnMotor(){

		servicePoints = new ServicePoint[3];

		servicePoints[0] = new RenewableEnergyStand(new Normal(10, 6), eventList, EventType.DEP1,2, 0);
		servicePoints[1] = new MainStage (new Normal(10, 10), eventList, EventType.DEP2, 4, 0);
		servicePoints[2] = new ServicePoint(new Normal(5, 3), eventList, EventType.DEP3, 5, 0);

		// Adding MainStage separately, without modifying the servicePoints array
		mainStage = new MainStage(new Normal(8, 4), eventList, EventType.TEST_EVENT, 3, 0);

		queue = new Queue(new Negexp(15, 5), eventList, EventType.ARR1);
	}

	@Override
	protected void initialize() {
		queue.generateNext(); // First arrival to the system
	}

	@Override
	protected void executeEvent(Event event) {  // Phase B events

		// ✅ Add a short delay so output is easier to read
		try {
			Thread.sleep(600);  // Pause for 200 milliseconds (0.2 seconds)
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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

				// Instead of ending the customer journey, send them to MainStage
				mainStage.addToQueue(customer);
				break;

			case TEST_EVENT:
				customer = (Customer) mainStage.removeFromQueue();
				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();
				break;
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

		// Ensure MainStage also gets checked in Phase C
		if (!mainStage.isBusy() && mainStage.hasQueue()) {
			Trace.out(Trace.Level.INFO, "Calling startService() for MainStage");
			mainStage.startService();
		}
	}

	@Override
	protected void results() {
		System.out.println("Simulation ended at " + Clock.getInstance().getTime());
		System.out.println("Results ... still missing");
	}
}
