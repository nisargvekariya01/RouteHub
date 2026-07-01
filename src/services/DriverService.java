package services;

import exceptions.DriverNotFoundException;
import exceptions.DuplicateUserException;
import models.Driver;
import models.Vehicle;
import repositories.DriverRepository;

import java.util.List;
import java.util.UUID;

public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver registerDriver(String name, String phoneNumber, Vehicle vehicle) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty.");
        }

        // Utilizing custom exception for business constraints
        boolean exists = driverRepository.findAll().stream()
                .anyMatch(d -> d.getPhoneNumber().equals(phoneNumber));
        if (exists) {
            throw new DuplicateUserException("Driver with phone number " + phoneNumber + " already exists.");
        }

        String id = UUID.randomUUID().toString();
        Driver driver = new Driver(id, name, phoneNumber, vehicle);
        
        driverRepository.save(driver);
        return driver;
    }

    public Driver addVehicle(String driverId, Vehicle vehicle) {
        if (driverId == null || vehicle == null) {
            throw new IllegalArgumentException("Driver ID and Vehicle are required.");
        }

        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found."));

        driver.setVehicle(vehicle);
        driverRepository.update(driver);
        return driver;
    }
    
    public Driver rateDriver(String driverId, int rating) {
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found."));
            
        driver.addRating(rating);
        driverRepository.update(driver);
        return driver;
    }

    public List<Driver> viewAllDrivers() {
        return driverRepository.findAll();
    }
}
