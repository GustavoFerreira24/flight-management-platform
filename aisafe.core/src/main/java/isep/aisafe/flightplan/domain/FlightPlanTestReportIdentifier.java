package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies a persisted flight plan test report.
 */
@Embeddable
public class FlightPlanTestReportIdentifier implements ValueObject, Comparable<FlightPlanTestReportIdentifier>, Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String value;

    protected FlightPlanTestReportIdentifier() {
    }

    private FlightPlanTestReportIdentifier(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Flight plan test report identifier is required.");
        this.value = value.trim().toUpperCase();
    }

    public static FlightPlanTestReportIdentifier valueOf(final String value) {
        return new FlightPlanTestReportIdentifier(value);
    }

    @Override
    public int compareTo(final FlightPlanTestReportIdentifier other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlightPlanTestReportIdentifier)) {
            return false;
        }
        final FlightPlanTestReportIdentifier that = (FlightPlanTestReportIdentifier) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
