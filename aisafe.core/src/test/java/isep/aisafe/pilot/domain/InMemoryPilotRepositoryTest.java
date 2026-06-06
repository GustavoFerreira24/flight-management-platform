package isep.aisafe.pilot.domain;

import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.airtransportcompany.domain.IATACode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPilotRepositoryTest {

    private InMemoryPilotRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPilotRepository();
    }

    private Pilot validPilot(String id) {
        final PilotID pilotID = PilotID.valueOf(id);
        final Certification certification = new Certification("01-01-2020", "01-01-2099");
        final PilotStatus status = PilotStatus.valueOf(certification);
        final IATACode companyCode = IATACode.valueOf("TP");
        final EmailAddress email = EmailAddress.valueOf("pilot@example.com");
        final PhoneNumber phoneNumber = PhoneNumber.valueOf("+351912345678");
        final String firstName = "Ben";
        final String lastName = "Dover";
        final Set<AircraftModelDesignator> certifiedAircraftModels = Set.of(AircraftModelDesignator.valueOf("A320"));

        return new Pilot(pilotID, certification, status, companyCode, email, phoneNumber, firstName, lastName, certifiedAircraftModels);
    }

    @Test
    void ensureSavedPilotCanBeFound() {
        // Arrange
        final Pilot pilot = validPilot("ABC");

        // Act
        repository.save(pilot);
        final Optional<Pilot> result = repository.findByCode(PilotID.valueOf("ABC"));

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void ensureFindByCodeReturnsEmptyWhenPilotDoesNotExist() {
        // Arrange
        final PilotID nonExistentID = PilotID.valueOf("ZZZ");

        // Act
        final Optional<Pilot> result = repository.findByCode(nonExistentID);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureSavedPilotHasCorrectIdentity() {
        // Arrange
        final Pilot pilot = validPilot("ABC");

        // Act
        repository.save(pilot);
        final Pilot found = repository.findByCode(PilotID.valueOf("ABC")).get();

        // Assert
        assertEquals(PilotID.valueOf("ABC"), found.identity());
    }

    @Test
    void ensureRepositoryStoresMultiplePilots() {
        // Arrange
        final Pilot pilotOne = validPilot("ABC");
        final Pilot pilotTwo = validPilot("CDE");

        // Act
        repository.save(pilotOne);
        repository.save(pilotTwo);

        // Assert
        assertTrue(repository.findByCode(PilotID.valueOf("ABC")).isPresent());
        assertTrue(repository.findByCode(PilotID.valueOf("CDE")).isPresent());
    }
}