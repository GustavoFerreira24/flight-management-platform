package isep.aisafe.customercollaborator.domain;

import eapli.framework.domain.model.ValueObject;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.Objects;

/**
 * Identifies the single customer associated with a collaborator.
 */
@Embeddable
public class CustomerReference implements ValueObject, Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Column(nullable = false)
    private String customerIdentity;

    protected CustomerReference() {
    }

    private CustomerReference(final CustomerType customerType, final String customerIdentity) {
        Preconditions.nonNull(customerType, "Customer type is required.");
        Preconditions.ensure(customerIdentity != null && !customerIdentity.isBlank(), "Customer identity is required.");

        this.customerType = customerType;
        this.customerIdentity = customerIdentity.trim().toUpperCase();
    }

    public static CustomerReference airTransportCompany(final String iataCode) {
        return new CustomerReference(CustomerType.AIR_TRANSPORT_COMPANY, iataCode);
    }

    public static CustomerReference airControlArea(final String areaCode) {
        return new CustomerReference(CustomerType.AIR_CONTROL_AREA, areaCode);
    }

    public CustomerType customerType() {
        return customerType;
    }

    public String customerIdentity() {
        return customerIdentity;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CustomerReference)) {
            return false;
        }
        final CustomerReference that = (CustomerReference) other;
        return customerType == that.customerType
                && Objects.equals(customerIdentity, that.customerIdentity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerType, customerIdentity);
    }
}
