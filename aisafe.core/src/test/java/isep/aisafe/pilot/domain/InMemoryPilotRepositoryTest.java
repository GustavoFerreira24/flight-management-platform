package isep.aisafe.pilot.domain;

import isep.aisafe.airtransportcompany.domain.IATACode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
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
        return new Pilot(pilotID, certification, status, companyCode);
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