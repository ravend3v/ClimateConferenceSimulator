package unit_tests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import simulation.model.*;
import simulation.framework.*;
import eduni.distributions.*;

public class RenewableEnergyStandTest {

    private RenewableEnergyStand renewableEnergyStand;
    private ContinuousGenerator generator;
    private EventList eventList;
    private EventType eventType;

    @Before
    public void setUp() {
        generator = new Uniform(1.0, 2.0); // Example generator
        eventList = new EventList();
        eventType = EventType.TEST_EVENT;
        renewableEnergyStand = new RenewableEnergyStand(generator, eventList, eventType, 5, 0);
    }

    @Test
    public void testAddToQueue() {
        Customer customer = new Customer();
        renewableEnergyStand.addToQueue(customer);
        assertEquals(1, renewableEnergyStand.getQueue().size());
    }

    @Test
    public void testRemoveFromQueue() {
        Customer customer = new Customer();
        renewableEnergyStand.addToQueue(customer);
        Customer removedCustomer = renewableEnergyStand.removeFromQueue();
        assertEquals(customer, removedCustomer);
        assertEquals(0, renewableEnergyStand.getQueue().size());
    }

    @Test
    public void testStartService() {
        Customer customer = new Customer();
        renewableEnergyStand.addToQueue(customer);
        renewableEnergyStand.startService();
        assertTrue(renewableEnergyStand.isBusy());
        assertEquals(1, eventList.getSize());
    }

    @Test
    public void testSetCapacity() {
        renewableEnergyStand.setCapacity(10);
        assertEquals(10, renewableEnergyStand.getCapacity());
    }

    @Test
    public void testSetCurrentCustomerCount() {
        renewableEnergyStand.setCurrentCustomerCount(3);
        assertEquals(3, renewableEnergyStand.getCurrentCustomerCount());
    }
}
