package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Username of the system user associated with a customer collaborator.
 *
 * <p>Usernames are normalized to lowercase to stay aligned with collaborator email values.</p>
 */
@Embeddable
public class SystemUsername implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    private String value;

    protected SystemUsername() {
    }

    private SystemUsername(final String value) {
        Preconditions.ensure(value != null && !value.isBlank(), "System username is required.");

        this.value = value.trim().toLowerCase();
    }

    public static SystemUsername valueOf(final String value) {
        return new SystemUsername(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SystemUsername)) {
            return false;
        }
        final SystemUsername that = (SystemUsername) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
