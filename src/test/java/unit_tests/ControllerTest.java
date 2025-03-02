package unit_tests;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import simulation.view.ISimulationUI;
import simulation.view.SimulationGUI;
import simulation.controller.Controller;
import simulation.view.CustomerView;
import simulation.view.ServicePointView;

import static org.mockito.Mockito.*;

public class ControllerTest {

    private ISimulationUI ui;
    private SimulationGUI gui;
    private Controller controller;

    @Before
    public void setUp() {
        // Initialize JavaFX environment
        new JFXPanel();

        ui = mock(ISimulationUI.class);
        gui = mock(SimulationGUI.class);
        controller = new Controller(ui, gui);
    }

    @Test
    public void testStartSimulation() {
        int[] capacities = {10, 20, 30, 40};
        controller.startSimulation(100.0, capacities);

        // Verify that the motor is started and the status label is updated
        verify(gui, timeout(1000)).updateStatusLabel("Simulation Completed!");
    }

    @Test
    public void testShowNewCustomer() {
        int customerId = 1;
        CustomerView customerView = mock(CustomerView.class);
        when(ui.getCustomer(customerId)).thenReturn(customerView);

        controller.showNewCustomer(customerId);

        // Verify that the customer view is added to the event entrance
        Platform.runLater(() -> verify(ui.getEventEntrance(), times(1)).addCustomerView(customerView));
    }

    @Test
    public void testShowCustomer() {
        int customerId = 1;
        int sourceServicePoint = 0;
        int destinationServicePoint = 1;
        CustomerView customerView = mock(CustomerView.class);
        when(ui.getCustomer(customerId)).thenReturn(customerView);
        when(ui.getEventEntrance()).thenReturn(mock(ServicePointView.class));
        when(ui.getRenewable()).thenReturn(mock(ServicePointView.class));

        controller.showNewCustomer(customerId);
        controller.showCustomer(customerId, sourceServicePoint, destinationServicePoint);

        // Verify that the customer view is moved from source to destination
        Platform.runLater(() -> {
            verify(ui.getEventEntrance(), times(1)).removeCustomerView(customerView);
            verify(ui.getRenewable(), times(1)).addCustomerView(customerView);
        });
    }

    @Test
    public void testCustomerExit() {
        int customerId = 1;
        CustomerView customerView = mock(CustomerView.class);
        when(ui.getCustomer(customerId)).thenReturn(customerView);
        when(ui.getMainStage()).thenReturn(mock(ServicePointView.class));

        controller.showNewCustomer(customerId);
        controller.customerExit(customerId);

        // Verify that the customer view is removed from the main stage
        Platform.runLater(() -> verify(ui.getMainStage(), times(1)).removeCustomerView(customerView));
    }
}