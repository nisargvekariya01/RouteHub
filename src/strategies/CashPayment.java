package strategies;

import models.Payment;
import models.PaymentStatus;

/**
 * Strategy for handling physical Cash payments.
 */
public class CashPayment implements PaymentMethod {
    @Override
    public void processPayment(Payment payment) {
        // Simulating physical cash collection
        System.out.println("[SIMULATION] Driver collecting $" + payment.getAmount() + " in cash from passenger.");
        payment.setStatus(PaymentStatus.COMPLETED);
    }
}
