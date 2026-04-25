package isep.aisafe.application.usermanagement;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.usermanagement.domain.AisafeRoles;

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

        for (Role role : roles) {
            if (!isValidRole(role)) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
        }
    }

    private void ensureNotBlank(final String value, final String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isValidRole(final Role role) {
        return role.equals(AisafeRoles.ADMIN)
                || role.equals(AisafeRoles.BACKOFFICE_OPERATOR)
                || role.equals(AisafeRoles.PILOT)
                || role.equals(AisafeRoles.FLIGHT_CONTROL_OPERATOR)
                || role.equals(AisafeRoles.WEATHER_PERSON)
                || role.equals(AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR)
                || role.equals(AisafeRoles.POWER_USER);
    }
}