package simulation.model;

import simulation.framework.Clock;
import simulation.framework.Event;
import simulation.framework.EventList;
import simulation.framework.Trace;
import eduni.distributions.ContinuousGenerator;
import java.util.HashSet;
import java.util.Set;

public class MainStage extends ServicePoint {
    private Set<Customer> processing = new HashSet<>();

    public MainStage(ContinuousGenerator generator, EventList eventList, EventType eventType, int capacity, int currentCustomerCount) {
        super(generator, eventList, eventType, capacity, currentCustomerCount);
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
            Trace.out(Trace.Level.INFO, "Starting main event for customer " + customer.getId());

            double serviceTime = getGenerator().sample();
            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            currentCustomerCount++;
            System.out.println("current count: " + currentCustomerCount);

            if (currentCustomerCount == capacity) {
                Trace.out(Trace.Level.INFO, "MainStage is full.");
                setBusy(true);
                break;
            }
        }
        for (Customer c : processing) {
            this.addToQueue(c);
        }
    }

}
