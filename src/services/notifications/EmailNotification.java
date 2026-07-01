package services.notifications;

import models.Ride;

/**
 * Future observer implementation for sending Email notifications.
 */
public class EmailNotification implements NotificationService {
    
    @Override
    public void onRideUpdate(Ride ride, String eventMessage) {
        // Future logic to connect to an SMTP server (e.g., SendGrid, AWS SES)
        System.out.println("[EMAIL SIMULATION] Dispatching email to passenger " + ride.getPassenger().getName() + " for Ride ID: " + ride.getId());
    }
}
