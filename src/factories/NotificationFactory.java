package factories;

import services.notifications.ConsoleNotification;
import services.notifications.EmailNotification;
import services.notifications.NotificationService;
import services.notifications.SMSNotification;

/**
 * FACTORY PATTERN:
 * Encapsulates the instantiation logic for Notification channels.
 * 
 * Instead of the application explicitly invoking `new ConsoleNotification()`, it simply 
 * requests a notification channel by Enum type. This delegates object creation away from the 
 * consumer, drastically reducing coupling. It makes it incredibly easy to swap or add new 
 * notification types in the future without hunting down `new` keywords across the codebase.
 */
public class NotificationFactory {
    
    public enum NotificationType {
        CONSOLE, EMAIL, SMS
    }

    public static NotificationService createNotificationService(NotificationType type) {
        switch (type) {
            case EMAIL:
                return new EmailNotification();
            case SMS:
                return new SMSNotification();
            case CONSOLE:
            default:
                return new ConsoleNotification();
        }
    }
}
