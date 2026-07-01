package com.rideshare.model;

public class Sedan extends Vehicle {
    public Sedan(String id) {
        super(id, 4, 1.0);
    }

    @Override
    public String getType() {
        return "Sedan";
    }
}
