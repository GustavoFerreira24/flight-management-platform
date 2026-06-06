package isep.aisafe.pilot.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PhoneNumber implements ValueObject, Comparable<PhoneNumber>, Serializable {

    private String value;

    /**
     * Required by ORM.
     */
    protected PhoneNumber() {}

    /**
     * Construtor principal - recebe o número completo com prefixo internacional
     */
    private PhoneNumber(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        final String normalized = normalize(value.trim());

        if (!isValidInternationalPhone(normalized)) {
            throw new IllegalArgumentException(
                    "Invalid phone number. Expected format: +[country code][number] (e.g. +351912345678)"
            );
        }

        this.value = normalized;
    }

    public static PhoneNumber valueOf(final String phone) {
        return new PhoneNumber(phone);
    }

    /**
     * Normalizes Phone Number by removing spaces, dashes, and parentheses, but keeping the '+' and digits.
     */
    private String normalize(String phone) {
        // Remove tudo que não seja + ou dígito
        return phone.replaceAll("[\\s\\-()]", "");
    }

    /**
     * Realistic validation of international phone number.
     */
    private boolean isValidInternationalPhone(String phone) {
        if (!phone.startsWith("+")) {
            return false;
        }

        String numberWithoutPlus = phone.substring(1);

        // Validações por prefixo (podes expandir facilmente)
        if (phone.startsWith("+351")) {           // Portugal
            return numberWithoutPlus.length() == 12 && numberWithoutPlus.matches("351[2-9]\\d{8}");

        } else if (phone.startsWith("+34")) {     // Spain
            return numberWithoutPlus.length() == 11 && numberWithoutPlus.matches("34[6-9]\\d{8}");

        } else if (phone.startsWith("+33")) {     // France
            return numberWithoutPlus.length() == 11 && numberWithoutPlus.matches("33[1-9]\\d{8}");

        } else if (phone.startsWith("+49")) {     // Germany
            return numberWithoutPlus.length() >= 11 && numberWithoutPlus.length() <= 14
                    && numberWithoutPlus.matches("49\\d+");

        } else if (phone.startsWith("+44")) {     // United Kingdoom
            return numberWithoutPlus.length() >= 11 && numberWithoutPlus.length() <= 13
                    && numberWithoutPlus.matches("44\\d+");

        } else if (phone.startsWith("+1")) {      // EUA / Canada
            return numberWithoutPlus.length() == 11 && numberWithoutPlus.matches("1[2-9]\\d{9}");

        } else if (phone.startsWith("+55")) {     // Brazil
            return numberWithoutPlus.length() == 13 && numberWithoutPlus.matches("55[1-9]\\d{10}");

        } else {

            return numberWithoutPlus.length() >= 8 && numberWithoutPlus.length() <= 15
                    && numberWithoutPlus.matches("\\d+");
        }
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(PhoneNumber o) {
        return this.value.compareTo(o.value);
    }
}