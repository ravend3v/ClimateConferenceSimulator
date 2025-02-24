package simulation.framework;

import java.util.PriorityQueue;

public class EventList {
	private PriorityQueue<Event> list = new PriorityQueue<Event>();

	public EventList() {
	}

	public Event remove() {
		Trace.out(Trace.Level.INFO, "Removing from event list " + list.peek().getType() + " " + list.peek().getTime());
		return list.remove();
	}

	public void add(Event event) {
		Trace.out(Trace.Level.INFO, "Adding new event to the list " + event.getType() + " " + event.getTime());
		list.add(event);
	}

	public double getNextEventTime() {
		return list.peek().getTime();
	}

	public int getSize() {
		return list.size();
	}
}