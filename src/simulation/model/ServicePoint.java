package simulation.model;

import simulation.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

public class ServicePoint {

	private final LinkedList<Customer> queue = new LinkedList<>(); // Data structure implementation
	private final ContinuousGenerator generator;
	private final EventList eventList;
	private final EventType scheduledEventType;
  	protected int capacity;
  	protected int currentCustomerCount;


	private boolean busy = false;

	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount){
		this.eventList = eventList;
		this.generator = generator;
		this.scheduledEventType = type;
		this.capacity = capacity;
		this.currentCustomerCount = currentCustomerCount;
	}

	public void addToQueue(Customer c){   // The first customer in the queue is always in service
		queue.add(c);
	}

	public Customer removeFromQueue(){  // Remove the customer who was in service
		busy = false;
		return queue.poll();
	}

	public void startService(){  // Start a new service, the customer is in the queue during the service

		// Check if there are customers in the queue and start service for the first customer
		if (!queue.isEmpty()) {
			Customer customer = queue.peek();
			Trace.out(Trace.Level.INFO, "Starting a new service for customer " + customer.getId());
			busy = true;
			double serviceTime = generator.sample();
			eventList.add(new Event(scheduledEventType, Clock.getInstance().getTime() + serviceTime));
		}
	}

	public boolean isBusy(){
		return busy;
	}

	public boolean hasQueue(){
		return !queue.isEmpty();
	}

	public EventList getEventList() {
		return eventList;
	}

	public ContinuousGenerator getGenerator() {
		return generator;
	}

	public EventType getScheduledEventType() {
		return scheduledEventType;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public LinkedList<Customer> getQueue() {
		return queue;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCurrentCustomerCount(int currentCustomerCount) {
		this.currentCustomerCount = currentCustomerCount;
	}

	public int getCurrentCustomerCount() {
		return currentCustomerCount;
	}
}
