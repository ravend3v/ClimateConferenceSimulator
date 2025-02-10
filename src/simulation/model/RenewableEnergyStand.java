package simulation.model;

public class RenewableEnergyStand extends ServicePoint {
    private int capacity;


    // Constructor for the class
    public RenewableEnergyStand(ContinuousGenerator generator, EventList eventList, EventType type, int capacity) {
        super(generator, eventList, type);
        this.capacity = capacity;
    }
}
