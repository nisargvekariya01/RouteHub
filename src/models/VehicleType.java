package models;

/**
 * Enum representing categories of vehicles.
 * Exists to classify vehicles, which helps the matching engine and pricing strategy
 * determine appropriate fares and capacity constraints.
 */
public enum VehicleType {
    ECONOMY,
    PREMIUM,
    SUV
}
