package services;

import models.Payment;
import models.PaymentStatus;
import strategies.PaymentMethod;

import java.util.UUID;

/**
 * Service for orchestrating payment flows.
 * 
 * Demonstrates the Context class of the Strategy Pattern. The service accepts any 
 * PaymentMethod interface, isolating it completely from the underlying 
 * concrete gateway implementations (Cash, UPI, Card).
 */
public class PaymentService {
    
    /**
     * Initializes a new payment record for a completed ride.
     */
    public Payment createPayment(String rideId, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        return new Payment(UUID.randomUUID().toString(), rideId, amount);
    }
    
    /**
     * Executes the payment using the dynamically provided Strategy (Cash, UPI, or Card).
     */
    public void processPayment(Payment payment, PaymentMethod method) {
        if (payment == null || method == null) {
            throw new IllegalArgumentException("Payment and PaymentMethod cannot be null.");
        }
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment has already been processed.");
        }
        
        try {
            method.processPayment(payment);
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            throw new RuntimeException("Payment execution failed: " + e.getMessage());
        }
    }
}
