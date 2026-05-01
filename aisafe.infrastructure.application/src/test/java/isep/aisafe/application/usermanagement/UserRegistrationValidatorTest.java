package isep.aisafe.application.usermanagement;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.usermanagement.domain.AisafeRoles;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationValidatorTest {

    private final UserRegistrationValidator validator = new UserRegistrationValidator();

    @Test
    void ensureValidUserDataIsAccepted() {
        // Arrange / Act / Assert
        assertDoesNotThrow(() ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureUsernameIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureNullUsernameIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        null,
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensurePasswordIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureFirstNameIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureLastNameIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "",
                        "backoffice1@aisafe.com",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureEmailIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureInvalidEmailIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "invalid-email",
                        Set.of(AisafeRoles.BACKOFFICE_OPERATOR)
                )
        );
    }

    @Test
    void ensureNullRolesAreRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        null
                )
        );
    }

    @Test
    void ensureAtLeastOneRoleIsRequired() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of()
                )
        );
    }

    @Test
    void ensureInvalidRoleIsRejected() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "backoffice1",
                        "Password1",
                        "Backoffice",
                        "User",
                        "backoffice1@aisafe.com",
                        Set.of(Role.valueOf("INVALID_ROLE"))
                )
        );
    }

    @Test
    void ensureAllValidSystemRolesAreAccepted() {
        // Arrange / Act / Assert
        assertDoesNotThrow(() ->
                validator.validate(
                        "user1",
                        "Password1",
                        "System",
                        "User",
                        "user1@aisafe.com",
                        Set.of(
                                AisafeRoles.ADMIN,
                                AisafeRoles.BACKOFFICE_OPERATOR,
                                AisafeRoles.PILOT,
                                AisafeRoles.FLIGHT_CONTROL_OPERATOR,
                                AisafeRoles.WEATHER_PERSON,
                                AisafeRoles.AIR_TRANSPORT_COMPANY_COLLABORATOR
                        )
                )
        );
    }

    @Test
    void ensurePowerUserRoleIsRejectedBecauseItIsNotAnAisafeActor() {
        // Arrange / Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                validator.validate(
                        "poweruser",
                        "Password1",
                        "Power",
                        "User",
                        "poweruser@aisafe.com",
                        Set.of(Role.valueOf("POWER_USER"))
                )
        );
    }
}
