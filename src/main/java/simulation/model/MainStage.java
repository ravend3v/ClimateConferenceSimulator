package simulation.model;

import simulation.framework.*;
import eduni.distributions.ContinuousGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainStage extends ServicePoint {
    private Set<Customer> processing = new HashSet<>();
    private Map<Double,Customer> exitTimes = new HashMap<>();

    public MainStage(ContinuousGenerator generator, EventList eventList, EventType eventType, int capacity, int currentCustomerCount, Queue mainQueue) {
        super(generator, eventList, eventType, capacity, currentCustomerCount, mainQueue);
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
            double responseTime = (c.getExitTime() - c.getArrivalTime());
            getMainQueue().addCompleted(responseTime);
            completedServices++;
        }

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
                continue;
            }
            processing.add(customer);
            Trace.out(Trace.Level.INFO, "Starting main stage for customer " + customer.getId());

            double serviceTime = getGenerator().sample();
            addBusyTime(serviceTime);

            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            exitTimes.put(Clock.getInstance().getTime() + serviceTime,customer);
            currentCustomerCount++;
            System.out.println("current count at main stage: " + currentCustomerCount);

            if (currentCustomerCount == capacity) {
                Trace.out(Trace.Level.INFO, "main stage is full.");
                setBusy(true);
                break;
            }
        }

        for (Customer c : processing) {
            if (!getQueue().contains(c)) {
                this.addToQueue(c);
            }
        }


    }

}
