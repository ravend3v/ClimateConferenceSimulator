// src/simulation/model/RenewableEnergyStand.java
package simulation.model;

import simulation.framework.*;
import eduni.distributions.ContinuousGenerator;

public class RenewableEnergyStand extends ServicePoint {

    public RenewableEnergyStand(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount) {
        super(generator, eventList, type, capacity, currentCustomerCount);
    }

    @Override
    public Customer removeFromQueue() {
        setBusy(false);
        return getQueue().poll();
    }

    @Override
    public void startService() {
        if (!isBusy() && !getQueue().isEmpty()) {
            Customer customer = getQueue().peek();
            Trace.out(Trace.Level.INFO, "Started service Energy Stand for customer " + customer.getId());
            double serviceTime = getGenerator().sample();
            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            currentCustomerCount++;
            setBusy(true);
        }
    }
}