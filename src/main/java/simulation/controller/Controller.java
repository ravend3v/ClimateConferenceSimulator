package simulation.controller;
import javafx.application.Platform;
import simulation.framework.IMotor;
import simulation.model.OwnMotor;
import simulation.view.CustomerView;
import simulation.view.ISimulationUI;
import simulation.view.ServicePointView;
import simulation.view.SimulationGUI;

import java.util.Map;


public class Controller implements IControllerM,IControllerV{
    private IMotor motor;
    private ISimulationUI ui;
    private SimulationGUI gui;
    //protected ServicePointView[] servicePointViews;
    private Map<Integer, CustomerView> customerViews;

    public Controller(ISimulationUI ui, SimulationGUI gui){
        this.ui = ui;
        this.gui = gui;
    }

    @Override
    public void startSimulation(double time,int[] capacities){

        motor = new OwnMotor(this,capacities);
        motor.setSimulationTime(time);
        new Thread(() -> {
            motor.run();
            Platform.runLater(() -> updateStatusLabel("Simulation Completed!"));
        }).start();
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
        switch (servicePointIndex) {
            case 0:
                return ui.getEventEntrance();
            case 1:
                return ui.getRenewable();
            case 2:
                return ui.getShowRoom();
            case 3:
                return ui.getMainStage();
            default:
                throw new IllegalArgumentException("Invalid service point index: " + servicePointIndex);
        }
    }



    @Override
    public void updateStatusLabel(String message) {
        gui.updateStatusLabel(message);
    }

}
