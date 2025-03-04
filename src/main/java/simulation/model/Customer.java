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
	private static int arrivedCount = 0;
	private static int completedCount = 0;
	private static int lastID;
	private static double throughPut;

	public Customer(){
		id = i++;
		lastID = id;
		arrivalTime = Clock.getInstance().getTime();
		//Trace.out(Trace.Level.INFO, "New customer no " + i + " arrived at "+ arrivalTime);
	}

	public static void resetCounts() {
		arrivedCount = 0;
		completedCount = 0;
		i = 1;
		sum = 0;
		lastID = 0;
		throughPut = 0.0;
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
		sum += (long) (exitTime - arrivalTime);
		double average = (double) sum / id;
		throughPut = average;
		System.out.println("Average throughput time of customers so far "+ average);

	}

	public static int getCompletedCount() {
		return completedCount;
	}

	public static void addCompletedCustomer(){
		completedCount++;
	}

	public static int arrivedCount(){
		return lastID;
	}

	public static double getThroughPut(){
		return throughPut;
	}

}