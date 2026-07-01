package com.rideshare;

import com.rideshare.exception.NoDriverAvailableException;
import com.rideshare.model.Driver;
import com.rideshare.model.Location;
import com.rideshare.model.Rider;
import com.rideshare.model.Trip;
import com.rideshare.pattern.factory.VehicleFactory;
import com.rideshare.service.RideDispatcher;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Ride-Sharing Simulation Engine...");
        
        RideDispatcher dispatcher = RideDispatcher.getInstance();
        Random random = new Random();
        
        // 1. Seed 10 Drivers
        System.out.println("\n--- Seeding Drivers ---");
        String[] vehicleTypes = {"SEDAN", "SUV", "BIKE"};
        for (int i = 1; i <= 10; i++) {
            Location loc = new Location(random.nextDouble() * 0.05, random.nextDouble() * 0.05);
            Driver driver = new Driver("D" + i, "Driver-" + i, 
                    VehicleFactory.createVehicle(vehicleTypes[i % 3]), loc, 4.0 + random.nextDouble());
            dispatcher.getGridManager().addDriver(driver);
            System.out.println("Added Driver: " + driver.getName() + " with " + driver.getVehicle().getType() + " at " + loc);
        }
        
        // 2. Spawn 20 concurrent ride requests
        System.out.println("\n--- Spawning Ride Requests ---");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 1; i <= 20; i++) {
            final int riderId = i;
            executor.submit(() -> {
                Location start = new Location(random.nextDouble() * 0.05, random.nextDouble() * 0.05);
                Location end = new Location(random.nextDouble() * 0.05, random.nextDouble() * 0.05);
                Rider rider = new Rider("R" + riderId, "Rider-" + riderId, start, 4.5 + random.nextDouble() * 0.5);
                
                try {
                    System.out.println(rider.getName() + " requesting ride from " + start + " to " + end);
                    Trip trip = dispatcher.requestRide(rider, end);
                    
                    // Simulate trip progress
                    Thread.sleep(500); // Wait for driver to arrive
                    trip.start(); // Transition to ONGOING
                    
                    Thread.sleep(1000); // Simulate driving
                    trip.complete(); // Transition to COMPLETED
                    
                    dispatcher.incrementCompletedTrips();
                    dispatcher.addRevenue(trip.getFare());
                    
                    System.out.printf("[%s] Trip Completed! Fare: $%.2f%n", rider.getName(), trip.getFare());
                    
                } catch (NoDriverAvailableException e) {
                    System.err.println("[" + rider.getName() + "] Failed to get ride: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("[" + rider.getName() + "] Error: " + e.getMessage());
                }
            });
        }
        
        // Wait for all tasks to finish
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 3. Print Summary
        System.out.println("\n--- Simulation Summary ---");
        System.out.println("Total Trips Completed: " + dispatcher.getCompletedTrips());
        System.out.printf("Total Revenue: $%.2f%n", dispatcher.getTotalRevenue());
    }
}
