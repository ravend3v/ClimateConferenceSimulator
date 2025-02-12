package simulation.model;

import simulation.framework.*;
import eduni.distributions.ContinuousGenerator;


public class ClimateShowcaseRoom extends ServicePoint {

    public ClimateShowcaseRoom(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount) {
        super(generator, eventList, type, capacity, currentCustomerCount);
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void setCurrentCustomerCount(int currentCustomerCount) {
        this.currentCustomerCount = currentCustomerCount;
    }

    @Override
    public Customer removeFromQueue() {
        setBusy(false);
        currentCustomerCount--;
        return getQueue().poll();
    }

    @Override
    public void startService() {
        if (!isBusy() && getQueue().peek() != null) {
            Customer customer = getQueue().peek();
            if (customer != null) {
                Trace.out(Trace.Level.INFO, "Starting Climate Showcase Room for customer " + customer.getId());
                double serviceTime = getGenerator().sample();
                getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
                currentCustomerCount++;
                setBusy(true);
            }
        }
    }
}
