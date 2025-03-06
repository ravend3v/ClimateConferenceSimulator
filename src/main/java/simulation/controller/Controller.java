package simulation.controller;

import javafx.application.Platform;
import simulation.framework.IMotor;
import simulation.model.OwnMotor;
import simulation.view.CustomerView;
import simulation.view.ISimulationUI;
import simulation.view.ServicePointView;
import simulation.view.SimulationGUI;

import java.util.HashMap;
import java.util.Map;


public class Controller implements IControllerM,IControllerV{
    private IMotor motor;
    private ISimulationUI ui;
    private SimulationGUI gui;
    private Map<Integer, CustomerView> customerViews;

    public Controller(ISimulationUI ui, SimulationGUI gui){
        this.ui = ui;
        this.gui = gui;
        this.customerViews = new HashMap<>();
    }

    @Override
    public void startSimulation(double time,int[] capacities){
        try {
            clearVisibleClients();
            motor = new OwnMotor(this, capacities, getAllServicePointViews());
            motor.setSimulationTime(time);
            motor.setDelay(ui.getDelay());
            new Thread(() -> {
                try {
                    motor.run();
                    Platform.runLater(() -> updateStatusLabel("Simulation Completed!"));

                    // Cast the motor to OwnMotor to access the required method
                    if (motor instanceof OwnMotor) {
                        gui.updateResults("Simulation Results:\n" + ((OwnMotor) motor).getResults());
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> updateStatusLabel("Error during simulation: " + e.getMessage()));
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            updateStatusLabel("Error starting simulation");
            e.printStackTrace();
        }
    }

    @Override
    public void slowDown(){
        motor.setDelay((long)(motor.getDelay()*1.10));
    }

    @Override
    public void speedUp(){
        motor.setDelay((long)(motor.getDelay()*0.9));
    }


    // uuden asiakkaan lisäys
    @Override
    public void showNewCustomer(int customerId){
        CustomerView cView = ui.getCustomer(customerId);
        customerViews.put(customerId,cView);
        Platform.runLater(() -> ui.getEventEntrance().addCustomerView(cView));

    }

    // asiakkaiden liikkuminen visuaalisesti palvelupisteestä toiseen
    @Override
    public void showCustomer(int customerId, int sourceServicePoint, int destinationServicePoint) {
        CustomerView customerView = customerViews.get(customerId);
        Platform.runLater(() -> {
            getServicePointView(sourceServicePoint).removeCustomerView(customerView);
            getServicePointView(destinationServicePoint).addCustomerView(customerView);
        });
    }

    // asiakkaan poistuminen järjestelmästä
    @Override
    public void customerExit(int id){
        CustomerView cView = customerViews.get(id);
        Platform.runLater(() -> ui.getMainStage().removeCustomerView(cView));

    }

    private ServicePointView getServicePointView(int servicePointIndex) {
        return switch (servicePointIndex) {
            case 0 -> ui.getEventEntrance();
            case 1 -> ui.getRenewable();
            case 2 -> ui.getShowRoom();
            case 3 -> ui.getMainStage();
            default -> throw new IllegalArgumentException("Invalid service point index: " + servicePointIndex);
        };
    }

    public ServicePointView[] getAllServicePointViews(){
        ServicePointView[] servicePointViews = {
                ui.getEventEntrance(),
                ui.getRenewable(),
                ui.getShowRoom(),
                ui.getMainStage()};
        return servicePointViews;
    }

    public void clearVisibleClients() {
        Platform.runLater(() -> {
            ui.getEventEntrance().clearCustomerViews();
            ui.getRenewable().clearCustomerViews();
            ui.getShowRoom().clearCustomerViews();
            ui.getMainStage().clearCustomerViews();
        });
    }

    @Override
    public void updateStatusLabel(String message) {
        gui.updateStatusLabel(message);
    }

}
