package models;

/**
 * Enum representing the availability of a driver.
 * Exists to clearly define if a driver can accept new rides.
 * We use Enums rather than Booleans to allow for more than two states 
 * (e.g. they can be online, but currently on a trip).
 */
public enum DriverStatus {
    OFFLINE,
    ONLINE,
    ON_TRIP
}
