// This file exists to test the simulator in console only
package test;

import javafx.application.Application;
import javafx.stage.Stage;
import simulation.controller.Controller;
import simulation.framework.Motor;
import simulation.framework.Trace;
import simulation.framework.Trace.Level;
import simulation.model.TestOwnMotor;
import simulation.view.SimulationGUI;

public class Simulator extends Application {

	public static void main(String[] args) {
		Trace.setTraceLevel(Level.INFO);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		SimulationGUI gui = new SimulationGUI();
		Controller controller = new Controller(gui, gui);
		Motor motor = new TestOwnMotor(controller);
		motor.setSimulationTime(50);
		new Thread(motor::run).start();
	}
}