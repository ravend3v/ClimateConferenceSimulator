package test;

import simulation.framework.Motor;
import simulation.framework.Trace;
import simulation.framework.Trace.Level;
import simulation.model.OwnMotor;

public class Simulator {
	public static void main(String[] args) {
		Trace.setTraceLevel(Level.INFO);
		Motor motor = new OwnMotor();
		motor.setSimulationTime(1000);
		motor.run();
	}
}