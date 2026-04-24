package isep.aisafe.application.usermanagement;

import eapli.framework.infrastructure.authz.domain.model.Role;

import java.util.Set;
import java.util.regex.Pattern;

public class UserRegistrationValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public void validate(
            final String username,
            final String password,
            final String firstName,
            final String lastName,
            final String email,
            final Set<Role> roles) {

        ensureNotBlank(username, "Username is required.");
        ensureNotBlank(password, "Password is required.");
        ensureNotBlank(firstName, "First name is required.");
        ensureNotBlank(lastName, "Last name is required.");
        ensureNotBlank(email, "Email is required.");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email format is invalid.");
        }

        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("At least one role must be assigned.");
        }
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}