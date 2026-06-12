package isep.aisafe.flightplan.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persisted audit report for a flight plan validation/test execution.
 */
@Entity
public class FlightPlanTestReport implements AggregateRoot<FlightPlanTestReportIdentifier>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "REPORT_ID", nullable = false, unique = true))
    private FlightPlanTestReportIdentifier identifier;

    @ManyToOne(optional = false)
    private FlightPlan flightPlan;

    @Column(name = "TESTED_AT", nullable = false)
    private LocalDateTime testedAt;

    @Column(name = "TESTED_BY", nullable = false)
    private String testedBy;

    @Column(name = "SUCCESSFUL", nullable = false)
    private boolean successful;

    @Enumerated(EnumType.STRING)
    @Column(name = "INITIAL_STATUS", nullable = false)
    private FlightPlanStatus initialStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "VALIDATION_STATUS", nullable = false)
    private FlightPlanStatus validationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "FINAL_STATUS", nullable = false)
    private FlightPlanStatus finalStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "FLIGHT_PLAN_TEST_REPORT_MESSAGES", joinColumns = @JoinColumn(name = "REPORT_PK"))
    @Column(name = "MESSAGE", nullable = false, length = 500)
    private List<String> messages = new ArrayList<>();

    protected FlightPlanTestReport() {
    }

    public FlightPlanTestReport(
            final FlightPlanTestReportIdentifier identifier,
            final FlightPlan flightPlan,
            final LocalDateTime testedAt,
            final String testedBy,
            final boolean successful,
            final FlightPlanStatus initialStatus,
            final FlightPlanStatus validationStatus,
            final FlightPlanStatus finalStatus,
            final List<String> messages) {

        Preconditions.nonNull(identifier, "Flight plan test report identifier is required.");
        Preconditions.nonNull(flightPlan, "Flight plan is required.");
        Preconditions.nonNull(testedAt, "Test timestamp is required.");
        Preconditions.ensure(testedBy != null && !testedBy.isBlank(), "Tested by is required.");
        Preconditions.nonNull(initialStatus, "Initial status is required.");
        Preconditions.nonNull(validationStatus, "Validation status is required.");
        Preconditions.nonNull(finalStatus, "Final status is required.");
        Preconditions.nonNull(messages, "Report messages are required.");
        Preconditions.ensure(validationStatus == FlightPlanStatus.UNDER_VALIDATION,
                "Validation status must be UNDER_VALIDATION.");
        Preconditions.ensure(finalStatus == FlightPlanStatus.VALIDATED || finalStatus == FlightPlanStatus.REJECTED,
                "Final status must be VALIDATED or REJECTED.");

        this.identifier = identifier;
        this.flightPlan = flightPlan;
        this.testedAt = testedAt;
        this.testedBy = testedBy.trim();
        this.successful = successful;
        this.initialStatus = initialStatus;
        this.validationStatus = validationStatus;
        this.finalStatus = finalStatus;
        this.messages = new ArrayList<>(messages);
    }

    @Override
    public FlightPlanTestReportIdentifier identity() {
        return identifier;
    }

    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof FlightPlanTestReport)) {
            return false;
        }
        final FlightPlanTestReport that = (FlightPlanTestReport) other;
        return identity().equals(that.identity());
    }

    public FlightPlan flightPlan() {
        return flightPlan;
    }

    public LocalDateTime testedAt() {
        return testedAt;
    }

    public String testedBy() {
        return testedBy;
    }

    public boolean successful() {
        return successful;
    }

    public FlightPlanStatus initialStatus() {
        return initialStatus;
    }

    public FlightPlanStatus validationStatus() {
        return validationStatus;
    }

    public FlightPlanStatus finalStatus() {
        return finalStatus;
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }
}
