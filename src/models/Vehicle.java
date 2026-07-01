package models;

/**
 * Model representing a vehicle driven by a driver.
 * Exists to encapsulate vehicle specifications separate from the driver entity.
 * This ensures high cohesion, as vehicle details are independent of driver details.
 */
public class Vehicle {
    private final String licensePlate;
    private final String make;
    private final String model;
    private final VehicleType type;

    public Vehicle(String licensePlate, String make, String model, VehicleType type) {
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getType() {
        return type;
    }
}
