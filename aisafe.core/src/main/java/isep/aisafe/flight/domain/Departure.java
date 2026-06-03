package isep.aisafe.flight.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Value object that represents a flight departure definition.
 */
@Embeddable
public class Departure implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "DEPARTURE_DAY", nullable = false)
    private LocalDate day;

    @Column(name = "DEPARTURE_TIME", nullable = false)
    private LocalTime time;

    /**
     * Required by ORM.
     */
    protected Departure() {
    }

    private Departure(final LocalDate day, final LocalTime time) {
        Preconditions.nonNull(day, "Departure day cannot be null.");
        Preconditions.nonNull(time, "Departure time cannot be null.");

        this.day = day;
        this.time = time;
    }

    public static Departure valueOf(final LocalDate day, final LocalTime time) {
        return new Departure(day, time);
    }

    public static Departure valueOf(final LocalDateTime dateTime) {
        Preconditions.nonNull(dateTime, "Departure date/time cannot be null.");
        return new Departure(dateTime.toLocalDate(), dateTime.toLocalTime());
    }

    public LocalDate day() {
        return day;
    }

    public LocalTime time() {
        return time;
    }

    public LocalDateTime dateTime() {
        return LocalDateTime.of(day, time);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Departure)) {
            return false;
        }
        final Departure that = (Departure) other;
        return Objects.equals(day, that.day) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, time);
    }

    @Override
    public String toString() {
        return dateTime().toString();
    }
}
