package simulation.framework;

public interface IMotor {
    public void run();
    public void setSimulationTime(double time);
    public void setDelay(long time);
    public long getDelay();
}
