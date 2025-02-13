package simulation.framework;

import eduni.distributions.*;

public class Queue {

    private final ContinuousGenerator generator;
    private final EventList eventList;
    private final IEventType type;

    // Parameters for tracking results
    private int arrivalCount;
    private int completedCount = 0;
    private double cumulativeResponseTime;

    // Constructor to initialize the queue with a generator, event list, and event type
    public Queue(ContinuousGenerator generator, EventList eventList, IEventType type) {
        this.generator = generator;
        this.eventList = eventList;
        this.type = type;
        this.arrivalCount = 0;
        this.cumulativeResponseTime = 0.0;
    }

    // Method to generate the next event and add it to the event list
    public void generateNext() {
        Event event = new Event(type, Clock.getInstance().getTime() + generator.sample());
        eventList.add(event);
        //arrivalCount++;
    }

    // Method to add a completed event and update the cumulative response time
    public void addCompleted(double responseTime) {
        completedCount++;
        cumulativeResponseTime += responseTime;
    }

    // Getter for the arrival count
    public int getArrivalCount() {
        return arrivalCount;
    }

    public void addArrival(){
        arrivalCount++;
    }

    // Getter for the completed count
    public int getCompletedCount() {
        return completedCount;
    }

    // Getter for the cumulative response time
    public double getCumulativeResponseTime() {
        return cumulativeResponseTime;
    }

    // Method to reset the statistics
    public void resetStatistics() {
        arrivalCount = 0;
        completedCount = 0;
        cumulativeResponseTime = 0.0;
    }
}