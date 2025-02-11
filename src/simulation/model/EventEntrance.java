package simulation.model;

import eduni.distributions.ContinuousGenerator;
import simulation.framework.Clock;
import simulation.framework.Event;
import simulation.framework.EventList;
import simulation.framework.Trace;

public class EventEntrance extends ServicePoint{
    private int capacity;
    private int currentCustomerCount;

    public EventEntrance(ContinuousGenerator generator, EventList eventList, EventType type, int capacity){
        super(generator,eventList,type);
        this.capacity = capacity;
        currentCustomerCount = 0;
    }

    @Override
    public Customer removeFromQueue() {  // Remove the customer who was in service
        setBusy(false);
        return getQueue().poll();
    }

    @Override
    public void startService(){
        while(currentCustomerCount < capacity && getQueue().peek() != null){
            Customer customer = getQueue().peek();
            if(customer != null){
                Trace.out(Trace.Level.INFO, "Starting event entrance for customer " + customer.getId());
                double serviceTime = getGenerator().sample();
                getEventList().add(new Event(getScheduledEventType(), Clock.getInstance().getTime() + serviceTime));
                currentCustomerCount ++;
            }

        }
        if(currentCustomerCount == capacity){
            setBusy(true);
        }
    }



}
