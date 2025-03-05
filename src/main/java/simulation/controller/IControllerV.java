package simulation.controller;

public interface IControllerV {

    public void startSimulation(double time,int[] capacities);
    public void slowDown();
    public void speedUp();
}
