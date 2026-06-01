package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents the certification of a pilot, defined by an issue date and an expiry date.
 *
 * <p>Both dates must be provided in the {@code dd-MM-yyyy} format and must not be blank.
 * Validation of the date format is performed at construction time.</p>
 *
 * <p>This is a {@link ValueObject} and should be treated as immutable.</p>
 */

@Embeddable
public class Certification implements ValueObject {

    private LocalDate issuedAt;
    private LocalDate validUntil;

    /**
     * Required by ORM.
     */
    protected Certification() {}


    /**
     * Constructs a {@code Certification} with the given issue and expiry dates.
     *
     * @param issuedAtStr   the date the certification was issued, in {@code dd-MM-yyyy} format
     * @param validUntilStr the date the certification expires, in {@code dd-MM-yyyy} format
     * @throws IllegalArgumentException if either date is blank or not in the expected format
     */
    public Certification(String issuedAtStr, String validUntilStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (!askDate(issuedAtStr) || !askDate(validUntilStr)) {
            throw new IllegalArgumentException("Invalid date format. Use DD-MM-YYYY (e.g., 17-05-2026).");
        }

        LocalDate issuedAt = LocalDate.parse(issuedAtStr, formatter);
        LocalDate validUntil = LocalDate.parse(validUntilStr, formatter);

        if (!validUntil.isAfter(issuedAt)) {
            throw new IllegalArgumentException("Valid until must be after issued at.");
        }

        this.issuedAt = issuedAt;
        this.validUntil = validUntil;
    }

    /**
     * Validates whether the given string represents a valid date in {@code dd-MM-yyyy} format.
     *
     * @param input the date string to validate
     * @return {@code true} if the date is valid, {@code false} if blank or unparseable
     */
    private boolean askDate(String input) {

        if (input.isBlank()) {
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns the certification issue date as a date.
     *
     * @return the issue date in {@code dd-MM-yyyy} format
     */
    public LocalDate issuedAt() {
        return issuedAt;
    }

    /**
     * Returns the certification expiry date as a date.e.
     *
     * @return the expiry date in {@code dd-MM-yyyy} format
     */
    public LocalDate validUntil() {
        return validUntil;
    }

    /**
     * Returns a human-readable representation of this certification,
     * including the issue and expiry dates.
     *
     * @return a formatted string with the certification dates
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return "Certification Issued At: " + issuedAt.format(formatter) +
                "\nCertification Valid Until: " + validUntil.format(formatter);
    }
}