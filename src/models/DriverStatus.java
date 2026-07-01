package models;

/**
 * Enum representing the availability of a driver.
 * Exists to clearly define if a driver can accept new rides, 
 * which is essential for the dispatch/matching algorithms.
 */
public enum DriverStatus {
    OFFLINE,
    AVAILABLE,
    ON_TRIP
}
