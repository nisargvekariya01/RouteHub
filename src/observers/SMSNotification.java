package observers;

import models.Ride;

/**
 * Future observer implementation for sending SMS notifications.
 */
public class SMSNotification implements NotificationService {
    
    @Override
    public void onRideUpdate(Ride ride, String eventMessage) {
        // Future logic to connect to SMS Gateway APIs (e.g., Twilio, Plivo)
        System.out.println("[SMS SIMULATION] Dispatching text message for Ride ID: " + ride.getId());
    }
}
