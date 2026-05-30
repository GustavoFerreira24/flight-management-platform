package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.ValueObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PilotStatus implements ValueObject {

    private String pilotStatus;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Required by ORM.
     */
    protected PilotStatus() {
    }

    /**
     * Constructs a {@code PilotStatus} by evaluating whether today's date
     * falls within the certification's validity period.
     *
     * @param certification the certification object containing the issue and expiry dates as strings formatted as {@code dd-MM-yyyy}
     * @throws java.time.format.DateTimeParseException if either date string is not in the expected format
     */
    public PilotStatus(Certification certification) {

        LocalDate issuedAt = LocalDate.parse(certification.issuedAtStr(), formatter);
        LocalDate validUntil = LocalDate.parse(certification.validUntilStr(), formatter);
        LocalDate hoje = LocalDate.now();

        if (!hoje.isBefore(issuedAt) && !hoje.isAfter(validUntil)) {
            this.pilotStatus = "VÁLIDO";
        } else {
            this.pilotStatus = "INVÁLIDO";
        }
    }

    /**
     * Constructs a {@code PilotStatus} with a specified status whenever the pilot is active or not.
     * @param status the pilot status as a string, expected to be either "VÁLIDO" or "INVÁLIDO"
     */
    private PilotStatus(String status) {
        this.pilotStatus = status;
    }

    /**
     * Factory method that creates a {@code PilotStatus} from two date strings.
     *
     * @param certification the certification object containing the issue and expiry dates as strings formatted as {@code dd-MM-yyyy}
     * @return a new {@code PilotStatus} instance
     * @throws java.time.format.DateTimeParseException if either date string is not in the expected format
     */
    public static PilotStatus valueOf(Certification certification) {
        return new PilotStatus(certification);
    }

    /**
     * Returns whether the pilot's certification is currently valid.
     *
     * @return {@code true} if the status is {@code "VÁLIDO"}, {@code false} if {@code "INVÁLIDO"}
     * @throws IllegalStateException if the internal status value is neither {@code "VÁLIDO"} nor {@code "INVÁLIDO"}
     */
    public boolean isValid() {
        if (pilotStatus.equals("VÁLIDO")) {
            return true;
        } else if (pilotStatus.equals("INVÁLIDO")) {
            return false;
        } else {
            throw new IllegalStateException("Unexpected pilot status value: " + pilotStatus);
        }
    }

    /**
     *
     * @return new pilot status with "VÁLIDO" status when pilot needs to get activated by thirds or "INVÁLIDO" when
     * pilot needs to get deactivated by thirds
     */

    public PilotStatus activate() {
        return new PilotStatus("VÁLIDO");
    }

    public PilotStatus deactivate() {
        return new PilotStatus("INVÁLIDO");
    }
}


