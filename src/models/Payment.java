package models;

import models.enums.*;

/**
 * Model representing a financial transaction for a completed ride.
 */
public class Payment {
    private final String id;
    private final String rideId;
    private final double amount;
    private PaymentStatus status;

    public Payment(String id, String rideId, double amount) {
        this.id = id;
        this.rideId = rideId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getRideId() {
        return rideId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
