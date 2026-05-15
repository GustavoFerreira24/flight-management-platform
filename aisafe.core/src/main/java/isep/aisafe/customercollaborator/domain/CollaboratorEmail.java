package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object that identifies a customer's collaborator by email.
 */
@Embeddable
public class CollaboratorEmail implements ValueObject, Comparable<CollaboratorEmail>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private String value;

    protected CollaboratorEmail() {
    }

    private CollaboratorEmail(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "Collaborator email is required.");

        final String normalized = value.trim().toLowerCase();

        Preconditions.ensure(EMAIL_PATTERN.matcher(normalized).matches(), "Collaborator email format is invalid.");

        this.value = normalized;
    }

    /**
     * Creates a collaborator email from raw text.
     *
     * @param value raw email value
     * @return normalized collaborator email
     */
    public static CollaboratorEmail valueOf(final String value) {
        return new CollaboratorEmail(value);
    }

    @Override
    public int compareTo(final CollaboratorEmail other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollaboratorEmail)) {
            return false;
        }
        final CollaboratorEmail that = (CollaboratorEmail) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
