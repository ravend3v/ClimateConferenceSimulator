package test;

import simulation.framework.Motor;
import simulation.framework.Trace;
import simulation.framework.Trace.Level;
import simulation.model.OwnMotor;

public class Simulator { //Tekstipohjainen

	public static void main(String[] args) {
		
		Trace.setTraceLevel(Level.INFO);
		Motor m = new OwnMotor();
		m.setSimulointiaika(1000);
		m.aja();
		///
	}
}
