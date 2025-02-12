package simulation.model;

import simulation.framework.*;
import eduni.distributions.ContinuousGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ClimateShowcaseRoom extends ServicePoint {
    private final Set<Customer> processing = new HashSet<>();
    private Map<Double,Customer> exitTimes = new HashMap<>();

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
            Trace.out(Trace.Level.INFO, "Starting climate showcase room for customer " + customer.getId());

            double serviceTime = getGenerator().sample();
            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            exitTimes.put(Clock.getInstance().getTime() + serviceTime,customer);
            currentCustomerCount++;
            System.out.println("current count at climate showcase room: " + currentCustomerCount);

            if (currentCustomerCount == capacity) {
                Trace.out(Trace.Level.INFO, "climate showcase room is full.");
                setBusy(true);
                break;
            }
        }

        for (Customer c : processing) {
            this.addToQueue(c);
        }


    }
}
