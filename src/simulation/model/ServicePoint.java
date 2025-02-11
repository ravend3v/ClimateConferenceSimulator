package simulation.model;

import simulation.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

// TODO:
// Service point specific functionalities, calculations (+ necessary variables) and reporting to be coded
public class ServicePoint {

	private final LinkedList<Customer> queue = new LinkedList<>(); // Data structure implementation
	private final ContinuousGenerator generator;
	private final EventList eventList;
	private final EventType scheduledEventType;

	//QueueStrategy strategy; //option: customer order

	private boolean busy = false;

	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.scheduledEventType = type;
	}

	public void addToQueue(Customer c){   // The first customer in the queue is always in service
		queue.add(c);
	}

	public Customer removeFromQueue(){  // Remove the customer who was in service
		busy = false;
		return queue.poll();
	}

	public void startService(){  // Start a new service, the customer is in the queue during the service

		Trace.out(Trace.Level.INFO, "Starting a new service for customer " + queue.peek().getId());

		busy = true;
		double serviceTime = generator.sample();
		eventList.add(new Event(scheduledEventType, Clock.getInstance().getTime() + serviceTime));
	}

	public boolean isBusy(){
		return busy;
	}

	public boolean hasQueue(){
		return queue.size() != 0;
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
}