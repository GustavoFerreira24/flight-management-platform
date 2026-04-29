package isep.aisafe.usermanagement.domain;

import eapli.framework.infrastructure.authz.domain.model.PasswordPolicy;
import eapli.framework.strings.util.StringPredicates;

public class AisafePasswordPolicy implements PasswordPolicy {

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
