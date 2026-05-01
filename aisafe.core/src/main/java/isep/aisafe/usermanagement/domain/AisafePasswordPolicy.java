package isep.aisafe.usermanagement.domain;

import eapli.framework.infrastructure.authz.domain.model.PasswordPolicy;
import eapli.framework.strings.util.StringPredicates;

/**
 * Defines the password rules accepted by AISafe user accounts.
 *
 * <p>The policy is intentionally simple: a password must exist, have at least six
 * characters, contain one digit and contain one uppercase letter.</p>
 */
public class AisafePasswordPolicy implements PasswordPolicy {

    /**
     * Checks whether the raw password satisfies the AISafe password rules.
     *
     * @param rawPassword password before encoding
     * @return true when the password follows the configured rules
     */
    @Override
    public boolean isSatisfiedBy(final String rawPassword) {
        if (StringPredicates.isNullOrEmpty(rawPassword)) {
            return false;
        }

        if (rawPassword.length() < 6) {
            return false;
        }

        if (!StringPredicates.containsDigit(rawPassword)) {
            return false;
        }

        return StringPredicates.containsCapital(rawPassword);
    }
}
