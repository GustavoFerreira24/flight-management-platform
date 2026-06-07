package isep.aisafe.weatherdata.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.validations.Preconditions;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registered weather data provider trusted by the system.
 */
@Entity
public class WeatherProvider implements AggregateRoot<WeatherProviderId>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long pk;

    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "WEATHER_PROVIDER_ID", nullable = false, unique = true))
    private WeatherProviderId identifier;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "WEATHER_PROVIDER_TYPE", joinColumns = @JoinColumn(name = "WEATHER_PROVIDER_PK"))
    @AttributeOverride(name = "value", column = @Column(name = "PROVIDER_TYPE", nullable = false))
    private List<WeatherProviderType> supportedTypes = new ArrayList<>();

    protected WeatherProvider() {
    }

    public WeatherProvider(final String name, final Iterable<WeatherProviderType> supportedTypes) {
        Preconditions.ensure(name != null && !name.isBlank(), "Weather provider name is required.");
        Preconditions.nonNull(supportedTypes, "Weather provider supported types are required.");

        this.name = name.trim().replaceAll("\\s+", " ");
        identifier = WeatherProviderId.valueOf(this.name);

        for (final WeatherProviderType supportedType : supportedTypes) {
            addSupportedType(supportedType);
        }

        Preconditions.ensure(!this.supportedTypes.isEmpty(), "Weather provider must support at least one type.");
    }

    /**
     * Adds a supported acquisition type to this provider.
     *
     * @param supportedType supported weather provider type
     */
    public void addSupportedType(final WeatherProviderType supportedType) {
        Preconditions.nonNull(supportedType, "Weather provider type is required.");
        if (!supportedTypes.contains(supportedType)) {
            supportedTypes.add(supportedType);
        }
    }

    /**
     * Checks whether this provider supports an acquisition type.
     *
     * @param providerType provider type used by the measurement
     * @return true when the provider supports the type
     */
    public boolean supports(final WeatherProviderType providerType) {
        return supportedTypes.contains(providerType);
    }

    /**
     * Returns the display name of this provider.
     *
     * @return provider name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the acquisition types supported by this provider.
     *
     * @return unmodifiable supported type list
     */
    public List<WeatherProviderType> supportedTypes() {
        return Collections.unmodifiableList(supportedTypes);
    }

    /**
     * Returns this aggregate identity.
     *
     * @return weather provider identifier
     */
    @Override
    public WeatherProviderId identity() {
        return identifier;
    }

    /**
     * Checks whether another object represents the same provider.
     *
     * @param other object to compare
     * @return true when both objects have the same identity
     */
    @Override
    public boolean sameAs(final Object other) {
        if (!(other instanceof WeatherProvider)) {
            return false;
        }
        final WeatherProvider that = (WeatherProvider) other;
        return identity().equals(that.identity());
    }
}
