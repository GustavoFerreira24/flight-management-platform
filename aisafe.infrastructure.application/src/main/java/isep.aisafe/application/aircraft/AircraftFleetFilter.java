package isep.aisafe.application.aircraft;

/**
 * Optional filters for listing an air transport company's aircraft fleet.
 */
public class AircraftFleetFilter {

    private final String model;
    private final String maker;
    private final Integer minimumCapacity;
    private final Integer maximumCapacity;
    private final Integer minimumAge;
    private final Integer maximumAge;

    /**
     * Creates a fleet filter.
     *
     * @param model optional aircraft model text
     * @param maker optional aircraft maker text
     * @param minimumCapacity optional minimum seat capacity
     * @param maximumCapacity optional maximum seat capacity
     * @param minimumAge optional minimum aircraft age in years
     * @param maximumAge optional maximum aircraft age in years
     */
    public AircraftFleetFilter(
            final String model,
            final String maker,
            final Integer minimumCapacity,
            final Integer maximumCapacity,
            final Integer minimumAge,
            final Integer maximumAge) {

        validateRange(minimumCapacity, maximumCapacity, "capacity");
        validateRange(minimumAge, maximumAge, "age");

        this.model = blankToNull(model);
        this.maker = blankToNull(maker);
        this.minimumCapacity = minimumCapacity;
        this.maximumCapacity = maximumCapacity;
        this.minimumAge = minimumAge;
        this.maximumAge = maximumAge;
    }

    /**
     * Creates an empty filter.
     *
     * @return filter with no restrictions
     */
    public static AircraftFleetFilter empty() {
        return new AircraftFleetFilter(null, null, null, null, null, null);
    }

    private static String blankToNull(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private static void validateRange(final Integer minimum, final Integer maximum, final String label) {
        if (minimum != null && minimum < 0) {
            throw new IllegalArgumentException("Minimum " + label + " cannot be negative.");
        }

        if (maximum != null && maximum < 0) {
            throw new IllegalArgumentException("Maximum " + label + " cannot be negative.");
        }

        if (minimum != null && maximum != null && minimum > maximum) {
            throw new IllegalArgumentException("Minimum " + label + " cannot be greater than maximum " + label + ".");
        }
    }

    public String model() {
        return model;
    }

    public String maker() {
        return maker;
    }

    public Integer minimumCapacity() {
        return minimumCapacity;
    }

    public Integer maximumCapacity() {
        return maximumCapacity;
    }

    public Integer minimumAge() {
        return minimumAge;
    }

    public Integer maximumAge() {
        return maximumAge;
    }
}
