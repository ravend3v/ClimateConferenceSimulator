package simulation.view;

import simulation.model.CustomerType;

public interface ISimulationUI {

    public ServicePointView getEventEntrance();
    public ServicePointView getRenewable();
    public ServicePointView getShowRoom();
    public ServicePointView getMainStage();
    public CustomerView getCustomer(int id, CustomerType type);
    public long getDelay();
}
