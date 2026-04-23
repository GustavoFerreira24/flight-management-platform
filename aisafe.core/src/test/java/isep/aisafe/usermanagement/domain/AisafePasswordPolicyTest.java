package isep.aisafe.usermanagement.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AisafePasswordPolicyTest {

    private final AisafePasswordPolicy policy = new AisafePasswordPolicy();

    @Test
    void ensureValidPasswordIsAccepted() {
        assertTrue(policy.isSatisfiedBy("Password1"));
    }

    @Test
    void ensureNullPasswordIsRejected() {
        assertFalse(policy.isSatisfiedBy(null));
    }

    @Test
    void ensureShortPasswordIsRejected() {
        assertFalse(policy.isSatisfiedBy("Pa1"));
    }

    @Test
    void ensurePasswordWithoutDigitIsRejected() {
        assertFalse(policy.isSatisfiedBy("Password"));
    }

    @Test
    void ensurePasswordWithoutCapitalLetterIsRejected() {
        assertFalse(policy.isSatisfiedBy("password1"));
    }
}