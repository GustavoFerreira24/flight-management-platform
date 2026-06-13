package isep.aisafe.persistence.impl.jpa;

import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;
import isep.aisafe.Application;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotRepository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * JPA implementation of the pilot repository.
 */
public class JpaPilotRepository extends JpaAutoTxRepository<Pilot, PilotID, PilotID> implements PilotRepository {

    JpaPilotRepository(final TransactionalContext autoTx) {
        super(autoTx, "id");
    }

    JpaPilotRepository(final String persistenceUnitName) {
        super(persistenceUnitName, Application.settings().extendedPersistenceProperties(), "id");
    }

     @Override
     public Optional<Pilot> findByCode(PilotID id) {
         final Map<String, Object> params = new HashMap<>();
         params.put("id", id);

         final Iterator<Pilot> matches = match("e.id=:id", params).iterator();

         if (matches.hasNext()) {
             return Optional.of(matches.next());
         }

         return Optional.empty();
     }

     @Override
     public Optional<Pilot> findByEmail(String email) {
         final Map<String, Object> params = new HashMap<>();
         params.put("email", email);

          final Iterator<Pilot> matches = match("e.email.email=:email", params).iterator();

         if (matches.hasNext()) {
             return Optional.of(matches.next());
         }

         return Optional.empty();
     }
}
