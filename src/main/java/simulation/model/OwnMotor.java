// In OwnMotor.java
package simulation.model;

import simulation.controller.IControllerM;
import simulation.framework.*;
import simulation.view.ServicePointView;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import utils.SimulationResults;

public class OwnMotor extends Motor {

	private final Queue queue;
	private final ServicePoint[] servicePoints;
	private final ServicePointView[] servicePointViews;
	private final SimulationResults simulationResults;


	public OwnMotor(IControllerM controller,int[] capacities,ServicePointView[] servicePointViews) {
		super(controller);
        this.queue = new Queue(new Negexp(10, 5), eventList, EventType.ARR1);
		this.servicePointViews = servicePointViews;
		servicePoints = new ServicePoint[4];

		servicePoints[0] = new EventEntrance(new Normal(10, 10), eventList, EventType.DEP1, capacities[0], 0, queue);
		servicePoints[1] = new RenewableEnergyStand(new Normal(10, 10), eventList, EventType.DEP2, capacities[1], 0, queue);
		servicePoints[2] = new ClimateShowcaseRoom(new Normal(5, 3), eventList, EventType.DEP3, capacities[2], 0, queue);
		servicePoints[3] = new MainStage(new Normal(5, 3), eventList, EventType.DEP4, capacities[3], 0, queue);

		this.simulationResults = new SimulationResults(servicePoints, queue);
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
				//queue.addArrival();
				break;
			case DEP1:
				//queue.addArrival();
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
				if (customer != null) {
					controller.customerExit(customer.getId());
					customer.setExitTime(Clock.getInstance().getTime());
					customer.report();
					queue.addCompleted(customer.getExitTime() - customer.getArrivalTime());
					Customer.addCompletedCustomer();
					System.out.println("exited customers: " + Customer.getCompletedCount());
					System.out.println("Customer " + customer.getId() + " processed in DEP4.");
				} else {
					System.err.println("Error: Attempted to process a null customer in DEP4.");
				}
				/*
				customer = servicePoints[3].removeFromQueue();
				controller.customerExit(customer.getId());
				customer.setExitTime(Clock.getInstance().getTime());
				customer.report();
				queue.addCompleted(customer.getExitTime() - customer.getArrivalTime());
				Customer.addCompletedCustomer();
				System.out.println("exited customers: "+Customer.getCompletedCount());
			*/

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
		simulationResults.calculateResults();
	}

	public String getResults() {
		return simulationResults.getResultsAsString();
	}

	@Override
	protected void updateUI(double time) {
		System.out.println("Simulation time: " + time);
	}

}