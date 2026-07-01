package strategies;

import models.Payment;
import models.PaymentStatus;

/**
 * Strategy for handling Credit/Debit Card payments.
 */
public class CardPayment implements PaymentMethod {
    @Override
    public void processPayment(Payment payment) {
        // Simulating banking verification network
        System.out.println("[SIMULATION] Verifying Card details with Banking Network...");
        System.out.println("[SIMULATION] Successfully charged $" + payment.getAmount() + " to Card.");
        payment.setStatus(PaymentStatus.COMPLETED);
    }
}
