package isep.aisafe.application.aircraft;

/**
 * Read model for one aircraft in a company's fleet listing.
 */
public class AircraftFleetEntry {

    private final String registration;
    private final String model;
    private final String maker;
    private final int totalSeats;
    private final int modelCapacity;
    private final int manufactureYear;
    private final int ageInYears;
    private final String operationalStatus;
    private final String engineModel;

    AircraftFleetEntry(
            final String registration,
            final String model,
            final String maker,
            final int totalSeats,
            final int modelCapacity,
            final int manufactureYear,
            final int ageInYears,
            final String operationalStatus,
            final String engineModel) {

        this.registration = registration;
        this.model = model;
        this.maker = maker;
        this.totalSeats = totalSeats;
        this.modelCapacity = modelCapacity;
        this.manufactureYear = manufactureYear;
        this.ageInYears = ageInYears;
        this.operationalStatus = operationalStatus;
        this.engineModel = engineModel;
    }

    public String registration() {
        return registration;
    }

    public String model() {
        return model;
    }

    public String maker() {
        return maker;
    }

    public int totalSeats() {
        return totalSeats;
    }

    public int modelCapacity() {
        return modelCapacity;
    }

    public int manufactureYear() {
        return manufactureYear;
    }

    public int ageInYears() {
        return ageInYears;
    }

    public String operationalStatus() {
        return operationalStatus;
    }

    public String engineModel() {
        return engineModel;
    }
}
