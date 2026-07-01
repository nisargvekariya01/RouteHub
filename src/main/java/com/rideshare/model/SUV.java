package com.rideshare.model;

public class SUV extends Vehicle {
    public SUV(String id) {
        super(id, 6, 1.5);
    }

    @Override
    public String getType() {
        return "SUV";
    }
}
