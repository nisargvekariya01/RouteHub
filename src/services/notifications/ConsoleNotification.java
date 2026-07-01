package services.notifications;

import models.Ride;

/**
 * Concrete observer implementation that outputs notifications to the system console.
 */
public class ConsoleNotification implements NotificationService {
    
    @Override
    public void onRideUpdate(Ride ride, String eventMessage) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("[CONSOLE NOTIFICATION ALARM]");
        System.out.println("To Passenger: " + ride.getPassenger().getName() + " (" + ride.getPassenger().getPhoneNumber() + ")");
        
        if (ride.getDriver() != null) {
            System.out.println("To Driver: " + ride.getDriver().getName() + " (" + ride.getDriver().getPhoneNumber() + ")");
        }
        
        System.out.println("Event: " + eventMessage);
        System.out.println("==================================================");
        System.out.println();
    }
}
