package simulation.framework;
import eduni.distributions.*;

public class Queue {

	private ContinuousGenerator generator;
	private EventList eventList;
	private IEventType type;

	public Queue(ContinuousGenerator generator, EventList eventList, IEventType type) {
		this.generator = generator;
		this.eventList = eventList;
		this.type = type;
	}

	public void generateNext() {
		Event event = new Event(type, Clock.getInstance().getTime() + generator.sample());
		eventList.add(event);
	}
}