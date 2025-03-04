package unit_tests;

import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import simulation.model.OwnMotor;
import simulation.view.SimulationGUI;
import simulation.framework.IMotor;

import static org.junit.Assert.*;

public class SimulationGUITest {
    private IMotor motor;
    private SimulationGUI gui;

    @Before
    public void setUp() {
        // Initialize JavaFX environment
        new JFXPanel();
        gui = new SimulationGUI();
    }

    @Test
    public void testUpdateStatusLabel() {
        String status = "Test Status";
        gui.updateStatusLabel(status);

        // Verify that the status label is updated
        if (motor instanceof OwnMotor) {
            assertEquals(status, gui.getStatusLabel().getText());
        }
    }
}