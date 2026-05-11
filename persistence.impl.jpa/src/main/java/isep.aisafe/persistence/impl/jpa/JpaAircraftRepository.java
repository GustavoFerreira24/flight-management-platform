package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRepository;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the aircraft repository.
 */
class JpaAircraftRepository
        extends JpaAutoTxRepository<Aircraft, AircraftRegistration, AircraftRegistration>
        implements AircraftRepository {

    JpaAircraftRepository(final TransactionalContext autoTx) {
        super(autoTx, "registration");
    }

    JpaAircraftRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "registration");
    }

    @Override
    public Optional<Aircraft> findByRegistration(final AircraftRegistration registration) {
        final Map<String, Object> params = new HashMap<>();
        params.put("registration", registration);

        final Iterator<Aircraft> matches = match("e.registration=:registration", params).iterator();

        if (matches.hasNext()) {
            return Optional.of(matches.next());
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Aircraft> findByCompany(final AirTransportCompany company) {
        final Map<String, Object> params = new HashMap<>();
        params.put("company", company);

        return match("e.company=:company", params);
    }
}