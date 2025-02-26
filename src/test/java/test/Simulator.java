package test;

import simulation.framework.Motor;
import simulation.framework.Trace;
import simulation.framework.Trace.Level;
import simulation.model.TestOwnMotor;

public class Simulator {
	public static void main(String[] args) {
		Trace.setTraceLevel(Level.INFO);
		Motor motor = new TestOwnMotor();
		motor.setSimulationTime(50);
		motor.run();
	}
}