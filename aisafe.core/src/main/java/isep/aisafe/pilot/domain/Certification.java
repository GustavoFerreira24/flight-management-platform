package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.ValueObject;

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
public class Certification implements ValueObject {

    private String issuedAtStr;
    private String validUntilStr;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

        if (!askDate(issuedAtStr) || !askDate(validUntilStr)) {
            throw new IllegalArgumentException("Invalid date format. Use DD-MM-YYYY (e.g., 17-05-2026).");
        }

        LocalDate issuedAt = LocalDate.parse(issuedAtStr, formatter);
        LocalDate validUntil = LocalDate.parse(validUntilStr, formatter);

        if (!validUntil.isAfter(issuedAt)) {
            throw new IllegalArgumentException("Valid until must be after issued at.");
        }

        this.issuedAtStr = issuedAtStr;
        this.validUntilStr = validUntilStr;
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
            LocalDate.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns the certification issue date as a string.
     *
     * @return the issue date in {@code dd-MM-yyyy} format
     */
    public String issuedAtStr() {
        return issuedAtStr;
    }

    /**
     * Returns the certification expiry date as a string.
     *
     * @return the expiry date in {@code dd-MM-yyyy} format
     */
    public String validUntilStr() {
        return validUntilStr;
    }

    /**
     * Returns a human-readable representation of this certification,
     * including the issue and expiry dates.
     *
     * @return a formatted string with the certification dates
     */
    @Override
    public String toString() {
        return "Certification Issued At: " + issuedAtStr + "\nCertification Valid Until: " + validUntilStr;
    }
}