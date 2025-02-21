// Refactor the clock class to focus on thread safety and lazy initialization (12.2.2025)
package simulation.framework;

public class Clock {

	private double time;
	private static Clock instance;

	private Clock(){
		time = 0;
	}

	public static Clock getInstance(){
		if (instance == null){
			synchronized (Clock.class) {
				if (instance == null) {
					instance = new Clock();
				}
			}
		}
		return instance;
	}

	public void setTime(double time){
		this.time = time;
	}

	public double getTime(){
		return time;
    }
}
