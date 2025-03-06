package simulation.model;

import eduni.distributions.ContinuousGenerator;
import simulation.framework.*;
import simulation.framework.Queue;

import java.util.*;

public class EventEntrance extends ServicePoint{
    private final Set<Customer> processing = new HashSet<>();
    private final Map<Double,Customer> exitTimes = new HashMap<>();

    public EventEntrance(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount, Queue mainQueue){
        super(generator,eventList,type, capacity, currentCustomerCount, mainQueue);
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
            Trace.out(Trace.Level.INFO, "Starting event entrance for customer " + customer.getId());

            double serviceTime = getGenerator().sample();
            addBusyTime(serviceTime);

            getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
            exitTimes.put(Clock.getInstance().getTime() + serviceTime,customer);
            currentCustomerCount++;
            System.out.println("current count at event entrance: " + currentCustomerCount);

            if (currentCustomerCount == capacity) {
                Trace.out(Trace.Level.INFO, "EventEntrance is full.");
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
