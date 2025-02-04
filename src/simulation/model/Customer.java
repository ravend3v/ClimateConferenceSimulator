package simulation.model;

import simulation.framework.*;

// TODO:
// Customer coded as required by the simulation model (data!)
public class Customer {
	private double arrivalTime;
	private double exitTime;
	private int id;
	private static int i = 1;
	private static long sum = 0;

	public Customer(){
		id = i++;

		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New customer no " + id + " arrived at "+ arrivalTime);
	}

	public double getExitTime() {
		return exitTime;
	}

	public void setExitTime(double exitTime) {
		this.exitTime = exitTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getId() {
		return id;
	}

	public void report(){
		Trace.out(Trace.Level.INFO, "\nCustomer "+ id + " finished! ");
		Trace.out(Trace.Level.INFO, "Customer "+ id + " arrived: " + arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "+ id + " exited: " + exitTime);
		Trace.out(Trace.Level.INFO,"Customer "+ id + " stayed: " + (exitTime - arrivalTime));
		sum += (exitTime - arrivalTime);
		double average = sum / id;
		System.out.println("Average throughput time of customers so far "+ average);
	}

}