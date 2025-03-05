package simulation.view;

public interface ISimulationUI {

    public ServicePointView getEventEntrance();
    public ServicePointView getRenewable();
    public ServicePointView getShowRoom();
    public ServicePointView getMainStage();
    public CustomerView getCustomer(int id);
    public long getDelay();
}
