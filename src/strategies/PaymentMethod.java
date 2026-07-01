package strategies;

import models.Payment;

/**
 * Strategy interface defining the contract for processing payments.
 * 
 * STRATEGY PATTERN IN ACTION:
 * By making PaymentMethod an interface rather than a rigid Enum with switch statements,
 * we can introduce entirely new payment gateways (like Crypto, PayPal, Apple Pay) 
 * in the future without altering the core PaymentService logic.
 */
public interface PaymentMethod {
    void processPayment(Payment payment);
}
