package models;

import models.enums.*;

/**
 * Base abstract model representing a generic user in the system.
 * Exists to centralize shared properties like ID, name, and contact details 
 * for both Passengers and Drivers, promoting DRY (Don't Repeat Yourself) principles.
 */
public abstract class User {
    private final String id;
    private final String name;
    private String phoneNumber;
    
    // Storing location at the User level ensures both Drivers and Passengers have location tracking
    private Location currentLocation;

    public User(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
