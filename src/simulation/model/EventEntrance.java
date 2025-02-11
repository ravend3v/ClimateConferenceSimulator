package simulation.model;

import eduni.distributions.ContinuousGenerator;
import simulation.framework.Clock;
import simulation.framework.Event;
import simulation.framework.EventList;
import simulation.framework.Trace;

import java.util.HashSet;
import java.util.Set;

public class EventEntrance extends ServicePoint{
    private Set<Customer> processing = new HashSet<>();

    public EventEntrance(ContinuousGenerator generator, EventList eventList, EventType type, int capacity, int currentCustomerCount){
        super(generator,eventList,type, capacity, currentCustomerCount);
    }

    @Override
    public Customer removeFromQueue() {  // Remove the customer who was in service
        setBusy(false);
        currentCustomerCount--;
        return getQueue().poll();
    }

    @Override
    public boolean isBusy() {
        return currentCustomerCount >= capacity;
    }

    public int getCurrentCustomerCount() {
        return currentCustomerCount;
    }

    @Override
    public void startService(){

        while(currentCustomerCount < capacity && getQueue().peek() != null){
            Customer customer = getQueue().peek();

            if(customer != null){
                if(processing.contains(customer)){
                    break;
                }
                processing.add(customer);
                Trace.out(Trace.Level.INFO, "Starting event entrance for customer " + customer.getId());
                double serviceTime = getGenerator().sample();
                getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
                currentCustomerCount ++;
                if(currentCustomerCount == capacity){
                    setBusy(true);
                    break;
                }
            }

            else{
                break;
            }

        }

    }



}
