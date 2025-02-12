package simulation.model;

import eduni.distributions.ContinuousGenerator;
import simulation.framework.Clock;
import simulation.framework.Event;
import simulation.framework.EventList;
import simulation.framework.Trace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventEntrance extends ServicePoint{
    private Set<Customer> processing = new HashSet<>();

    public EventEntrance(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount){
        super(generator,eventList,type, capacity, currentCustomerCount);
    }

    @Override
    public Customer removeFromQueue() {
        if(getQueue().isEmpty()){
            System.out.println("pööpötti");
        }

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

    public int getCurrentCustomerCount() {
        return currentCustomerCount;
    }


    @Override
    public void startService() {
        List<Customer> skippedCustomers = new ArrayList<>();

        while (currentCustomerCount < capacity && !getQueue().isEmpty()) {
            Customer customer = getQueue().poll();

            if (customer == null) {
                break;
            }

            if (processing.contains(customer)) {
                skippedCustomers.add(customer);
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
