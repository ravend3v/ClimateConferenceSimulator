package simulation.model;

import eduni.distributions.ContinuousGenerator;
import simulation.framework.*;

import java.util.HashSet;
import java.util.Set;

public class EventEntrance extends ServicePoint{
    private final Set<Customer> processing = new HashSet<>();

    public EventEntrance(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount){
        super(generator,eventList,type, capacity, currentCustomerCount);
    }

    @Override
    public Customer removeFromQueue() {
        setBusy(false);
        currentCustomerCount--;
        Customer c = getQueue().poll();
        processing.remove(c);
        return c;
    }

    @Override
    public boolean isBusy() {
        return currentCustomerCount >= capacity;
    }

    @Override
    public void startService() {
        while (currentCustomerCount < capacity && !getQueue().isEmpty()) {
            Customer customer = getQueue().poll();

            // Check if the customer is null or already being processed
            if (customer == null || processing.contains(customer)) {
                if (customer != null) {
                    getQueue().add(customer);
                }
                continue;
            }
            processing.add(customer);
            Trace.out(Trace.Level.INFO, "Starting event entrance for customer " + customer.getId());

            double serviceTime = getGenerator().sample();
            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            currentCustomerCount++;
            System.out.println("current count: " + currentCustomerCount);

            if (currentCustomerCount == capacity) {
                Trace.out(Trace.Level.INFO, "EventEntrance is full.");
                setBusy(true);
                break;
            }
        }
        for (Customer c : processing) {
            this.addToQueue(c);
        }
    }
}
