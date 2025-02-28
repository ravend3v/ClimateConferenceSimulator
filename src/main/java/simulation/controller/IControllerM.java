package simulation.controller;

public interface IControllerM {
    public void showNewCustomer(int id);
    public void showCustomer(int id, int source, int destination);
    public void customerExit(int id);
    public void updateStatusLabel(String text);
}
