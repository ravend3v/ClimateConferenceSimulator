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
    public void startService() {
        while (currentCustomerCount < capacity && getQueue().peek() != null) {
            Customer customer = getQueue().peek();

            if (customer != null) {
                if (processing.contains(customer)) {
                    break;
                }
                processing.add(customer);
                Trace.out(Trace.Level.INFO, "Starting event at the Main Stage for customer " + customer.getId());
                double serviceTime = getGenerator().sample();
                getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
                currentCustomerCount++;
                if (currentCustomerCount == capacity) {
                    setBusy(true);
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public Customer removeFromQueue() {
        setBusy(false);
        currentCustomerCount--;
        return getQueue().poll();
    }

    @Override
    public boolean isBusy() {
        return currentCustomerCount >= capacity;
    }

}

