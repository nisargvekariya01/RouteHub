package com.rideshare.pattern.factory;

import com.rideshare.model.Bike;
import com.rideshare.model.SUV;
import com.rideshare.model.Sedan;
import com.rideshare.model.Vehicle;

import java.util.UUID;

/**
 * Factory Pattern implementation for creating Vehicle objects.
 * 
 * WHY FACTORY PATTERN: It encapsulates the instantiation logic of vehicles.
 * This ensures that if we add new vehicle types in the future (e.g., Luxury),
 * we only update this factory rather than scattering object creation throughout the codebase,
 * upholding the Open/Closed Principle for client code.
 */
public class VehicleFactory {
    
    public static Vehicle createVehicle(String type) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return switch (type.toUpperCase()) {
            case "SEDAN" -> new Sedan(id);
            case "SUV" -> new SUV(id);
            case "BIKE" -> new Bike(id);
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + type);
        };
    }
}
