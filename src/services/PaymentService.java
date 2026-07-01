package services;

import exceptions.RideShareException;
import models.Payment;
import models.PaymentStatus;
import strategies.PaymentMethod;

import java.util.UUID;

public class PaymentService {
    
    public Payment createPayment(String rideId, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        return new Payment(UUID.randomUUID().toString(), rideId, amount);
    }
    
    public void processPayment(Payment payment, PaymentMethod method) {
        if (payment == null || method == null) {
            throw new IllegalArgumentException("Payment and PaymentMethod cannot be null.");
        }
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RideShareException("Payment has already been processed.");
        }
        
        try {
            method.processPayment(payment);
        } catch (RideShareException e) {
            // Already a domain exception, rethrow safely
            payment.setStatus(PaymentStatus.FAILED);
            throw e;
        } catch (Exception e) {
            // Convert any generic Exception into a safely categorized domain exception
            payment.setStatus(PaymentStatus.FAILED);
            throw new RideShareException("Payment execution failed critically: " + e.getMessage());
        }
    }
}
