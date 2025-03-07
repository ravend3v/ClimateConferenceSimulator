package simulation.controller;

import simulation.model.CustomerType;

public interface IControllerM {
    public void showNewCustomer(int id, CustomerType type);
    public void showCustomer(int id, int source, int destination);
    public void customerExit(int id);
    public void updateStatusLabel(String text);
}
