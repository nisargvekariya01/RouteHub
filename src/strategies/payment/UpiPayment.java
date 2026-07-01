package strategies.payment;

import models.Payment;
import models.enums.PaymentStatus;

/**
 * Strategy for handling digital UPI payments.
 */
public class UpiPayment implements PaymentMethod {
    @Override
    public void processPayment(Payment payment) {
        // Simulating API call to UPI Gateway
        System.out.println("[SIMULATION] Connecting to UPI Gateway...");
        System.out.println("[SIMULATION] Successfully charged $" + payment.getAmount() + " via UPI.");
        payment.setStatus(PaymentStatus.COMPLETED);
    }
}
