package com.rideshare;

import com.rideshare.exception.InvalidTripStateTransitionException;
import com.rideshare.exception.NoDriverAvailableException;
import com.rideshare.model.Driver;
import com.rideshare.model.Location;
import com.rideshare.model.Rider;
import com.rideshare.model.Trip;
import com.rideshare.pattern.factory.VehicleFactory;
import com.rideshare.pattern.strategy.BaseFarePricing;
import com.rideshare.pattern.strategy.NightPricing;
import com.rideshare.pattern.strategy.SurgePricing;
import com.rideshare.service.RideDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RideShareEngineTest {

    @BeforeEach
    public void setup() {
        RideDispatcher.getInstance().getGridManager().clear();
    }

    @Test
    public void testPricingStrategies() {
        Rider rider = new Rider("R1", "Alice", new Location(0, 0), 5.0);
        Driver driver = new Driver("D1", "Bob", VehicleFactory.createVehicle("SEDAN"), new Location(0, 0), 5.0);
        Trip trip = new Trip("T1", rider, new Location(0, 0), new Location(0, 1));
        trip.setDriver(driver); 
        
        double distance = new Location(0,0).distanceTo(new Location(0,1));
        double expectedBase = distance * 2.0 * 1.0; 
        
        BaseFarePricing basePricing = new BaseFarePricing();
        assertEquals(expectedBase, basePricing.calculateFare(trip), 0.01);
        
        SurgePricing surgePricing = new SurgePricing(2.0);
        assertEquals(expectedBase * 2.0, surgePricing.calculateFare(trip), 0.01);
        
        NightPricing nightPricing = new NightPricing();
        assertEquals(expectedBase * 1.25, nightPricing.calculateFare(trip), 0.01);
    }

    @Test
    public void testTripStateTransitions() {
        Rider rider = new Rider("R1", "Alice", new Location(0, 0), 5.0);
        Trip trip = new Trip("T1", rider, new Location(0, 0), new Location(0, 1));
        
        assertEquals("REQUESTED", trip.getStatus());
        
        assertThrows(InvalidTripStateTransitionException.class, trip::start); 
        
        trip.accept();
        assertEquals("ACCEPTED", trip.getStatus());
        
        trip.start();
        assertEquals("ONGOING", trip.getStatus());
        
        trip.complete();
        assertEquals("COMPLETED", trip.getStatus());
        
        assertThrows(InvalidTripStateTransitionException.class, trip::cancel); 
    }

    @Test
    public void testConcurrencyNoDoubleBooking() throws InterruptedException {
        RideDispatcher dispatcher = RideDispatcher.getInstance();
        Location loc = new Location(0, 0);
        
        // Add only ONE driver
        Driver driver = new Driver("D1", "Bob", VehicleFactory.createVehicle("SEDAN"), loc, 5.0);
        dispatcher.getGridManager().addDriver(driver);
        
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    Rider rider = new Rider("R" + UUID.randomUUID(), "Rider", loc, 5.0);
                    dispatcher.requestRide(rider, new Location(0.01, 0.01));
                    successCount.incrementAndGet();
                } catch (NoDriverAvailableException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        
        // Only one rider should have successfully booked the single driver
        assertEquals(1, successCount.get());
        assertEquals(threads - 1, failureCount.get());
    }
}
