package com.rideshare.model;

public class Bike extends Vehicle {
    public Bike(String id) {
        super(id, 1, 0.5);
    }

    @Override
    public String getType() {
        return "Bike";
    }
}
