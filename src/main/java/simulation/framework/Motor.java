package simulation.framework;
import simulation.controller.IControllerM;

public abstract class Motor extends Thread implements IMotor{

    private double simulationTime = 0;
    private long delay = 0;
    private final Clock clock;
    protected EventList eventList;
    protected IControllerM controller;

    public Motor(IControllerM controller) {
        this.controller = controller;
        clock = Clock.getInstance(); // Take the clock variable to simplify the code
        eventList = new EventList();
        // Service points are created in the simulation.model package in the Motor subclass
    }

    @Override
    public void setSimulationTime(double time) {
        simulationTime = time;
    }

    @Override
    public void setDelay(long delay){
        this.delay = delay;
    }

    @Override
    public long getDelay(){
        return delay;
    }

    @Override
    public void run() {
        initialize(); // Create the first event, among other things
        while (isSimulating()) {
            defaultDelay();
            delay();
            Trace.out(Trace.Level.INFO, "\nPhase A: the clock is " + currentTime());
            clock.setTime(currentTime());

            Trace.out(Trace.Level.INFO, "\nPhase B:");
            executeBEvents();

            Trace.out(Trace.Level.INFO, "\nPhase C:");
            tryCEvents();

            updateUI(currentTime());
        }
        controller.updateStatusLabel("Simulation Completed!");
        results();
    }

    private void executeBEvents() {
        while (eventList.getNextEventTime() == clock.getTime()) {
            executeEvent(eventList.remove());
        }
    }

    private double currentTime() {
        return eventList.getNextEventTime();
    }

    private boolean isSimulating() {
        return clock.getTime() < simulationTime;
    }

    protected abstract void executeEvent(Event e); // Defined in the simulation.model package in the Motor subclass
    protected abstract void tryCEvents(); // Defined in the simulation.model package in the Motor subclass
    protected abstract void initialize(); // Defined in the simulation.model package in the Motor subclass
    protected abstract void results(); // Defined in the simulation.model package in the Motor subclass

    // Method to update the UI with the current simulation time
    protected void updateUI(double time) {
        // Override this method in the subclasses to update UI
    }

    private void defaultDelay(){

        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void delay(){
        Trace.out(Trace.Level.INFO, "delay " + delay);
        try {
            sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}