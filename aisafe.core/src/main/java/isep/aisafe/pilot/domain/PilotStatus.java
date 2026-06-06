package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import isep.aisafe.pilot.domain.PilotRepository;

@Embeddable
public class PilotStatus implements ValueObject {

    private String value;
    @Transient
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

        LocalDate issuedAt = certification.issuedAt();
        LocalDate validUntil = certification.validUntil();
        LocalDate hoje = LocalDate.now();

        if (!hoje.isBefore(issuedAt) && !hoje.isAfter(validUntil)) {
            this.value = "VÁLIDO";
        } else {
            this.value = "INVÁLIDO";
        }
    }

    /**
     * Constructs a {@code PilotStatus} with a specified status whenever the pilot is active or not.
     * @param status the pilot status as a string, expected to be either "VÁLIDO" or "INVÁLIDO"
     */
    private PilotStatus(String status) {
        this.value = status;
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
        if (value.equals("VÁLIDO")) {
            return true;
        } else if (value.equals("INVÁLIDO")) {
            return false;
        } else {
            throw new IllegalStateException("Unexpected pilot status value: " + value);
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

    @Override
    public String toString() {
        return value;
    }
}


