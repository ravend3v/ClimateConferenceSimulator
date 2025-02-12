// src/simulation/model/RenewableEnergyStand.java
package simulation.model;

import simulation.framework.*;
import eduni.distributions.ContinuousGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RenewableEnergyStand extends ServicePoint {
    private final Set<Customer> processing = new HashSet<>();
    private Map<Double,Customer> exitTimes = new HashMap<>();

    public RenewableEnergyStand(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount) {
        super(generator, eventList, type, capacity, currentCustomerCount);
    }

    @Override
    public Customer removeFromQueue() {
        Customer c = null;
        double smallestTime = Double.MAX_VALUE;

        for(Map.Entry<Double,Customer>entry: exitTimes.entrySet()){
            if(entry.getKey()<smallestTime){
                smallestTime = entry.getKey();
                c = entry.getValue();
            }
        }

        if(c!=null){
            exitTimes.remove(smallestTime);
            getQueue().remove(c);
            processing.remove(c);
            setBusy(false);
            currentCustomerCount--;
        }

        return c;
    }

    @Override
    public void startService() {
        while (currentCustomerCount < capacity && !getQueue().isEmpty()) {
            Customer customer = getQueue().poll();

            // Check if the customer is null or already being processed
            if (customer == null || processing.contains(customer)) {
                continue;
            }
            processing.add(customer);
            Trace.out(Trace.Level.INFO, "Starting renewable energy stand for customer " + customer.getId());

            double serviceTime = getGenerator().sample();
            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            exitTimes.put(Clock.getInstance().getTime() + serviceTime,customer);
            currentCustomerCount++;
            System.out.println("current count at renewable energy: " + currentCustomerCount);

            if (currentCustomerCount == capacity) {
                Trace.out(Trace.Level.INFO, "renewable energy stand is full.");
                setBusy(true);
                break;
            }
        }

        for (Customer c : processing) {
            this.addToQueue(c);
        }


    }
}