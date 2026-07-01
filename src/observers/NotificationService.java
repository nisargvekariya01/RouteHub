package observers;

import models.Ride;

/**
 * Interface for the Notification System.
 * This explicitly acts as the 'Observer' interface in the Observer Pattern.
 * 
 * EXPLAINING WHY NOTIFICATION IS SEPARATE:
 * Separating notifications from core business logic adheres strictly to the Single Responsibility 
 * Principle (SRP). The `RideService` (acting as the Subject) should only orchestrate core ride 
 * state transitions. It shouldn't care about formatting text strings, opening SMTP connections, 
 * or handling SMS gateway latency. 
 * 
 * By decoupling notifications via the Observer Pattern, we prevent a failure in an email provider 
 * from crashing a passenger's ride booking. It also allows us to seamlessly plug in new notification 
 * channels dynamically at runtime without ever modifying the core domain logic.
 */
public interface NotificationService {
    /**
     * Called by the Subject (RideService) whenever a significant lifecycle event occurs.
     */
    void onRideUpdate(Ride ride, String eventMessage);
}
