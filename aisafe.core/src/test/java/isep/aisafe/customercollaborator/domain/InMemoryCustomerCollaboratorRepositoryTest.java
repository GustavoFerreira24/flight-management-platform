package isep.aisafe.customercollaborator.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCustomerCollaboratorRepositoryTest {
    /**
     * Purpose: verify the in-memory repository lookup needed by application tests.
     * Scenario: save a collaborator and search by the same email.
     * Expected output: the collaborator is found.
     */
    @Test
    void ensureCollaboratorCanBeFoundByEmail() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final CollaboratorEmail email = CollaboratorEmail.valueOf("collaborator@aisafe.com");
        final CustomerCollaborator collaborator = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                email,
                airTransportCompany("TP", "TAP"),
                SystemUsername.valueOf("collaborator@aisafe.com")
        );

        // Act
        repository.save(collaborator);

        // Assert
        assertTrue(repository.findByEmail(email).isPresent());
    }

    /**
     * Purpose: verify lookup by associated system username.
     * Scenario: save a collaborator and search by the same system username.
     * Expected output: the collaborator is found.
     */
    @Test
    void ensureCollaboratorCanBeFoundBySystemUsername() {
        // Arrange
        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();
        final SystemUsername systemUsername = SystemUsername.valueOf("collaborator@aisafe.com");
        final CustomerCollaborator collaborator = new CustomerCollaborator(
                "Customer",
                "Collaborator",
                CollaboratorEmail.valueOf("collaborator@aisafe.com"),
                airTransportCompany("TP", "TAP"),
                systemUsername
        );

        // Act
        repository.save(collaborator);

        // Assert
        assertTrue(repository.findBySystemUsername(systemUsername).isPresent());
    }

    @Test
    void ensureFindByAirTransportCompanyOnlyReturnsMatchingCompany() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final AirTransportCompany tpCompany = airTransportCompany("TP", "TAP");
        final AirTransportCompany frCompany = airTransportCompany("FR", "RYR");

        final CustomerCollaborator tpCollaborator =
                new CustomerCollaborator(
                        "John",
                        "Doe",
                        CollaboratorEmail.valueOf("john@tp.com"),
                        tpCompany,
                        SystemUsername.valueOf("john@tp.com")
                );

        final CustomerCollaborator frCollaborator =
                new CustomerCollaborator(
                        "Jane",
                        "Doe",
                        CollaboratorEmail.valueOf("jane@fr.com"),
                        frCompany,
                        SystemUsername.valueOf("jane@fr.com")
                );

        repository.save(tpCollaborator);
        repository.save(frCollaborator);

        final List<CustomerCollaborator> result =
                repository.findByAirTransportCompany(tpCompany);

        assertEquals(1, result.size());
        assertTrue(result.contains(tpCollaborator));
    }

    @Test
    void ensureFindByAirTransportCompanyReturnsEmptyWhenRepositoryIsEmpty() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final List<CustomerCollaborator> result =
                repository.findByAirTransportCompany(airTransportCompany("TP", "TAP"));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureCustomerAssociationsAreNotMixed() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final AirTransportCompany tpCompany = airTransportCompany("TP", "TAP");

        final CustomerCollaborator transportCollaborator =
                new CustomerCollaborator(
                        "John",
                        "Doe",
                        CollaboratorEmail.valueOf("john@tp.com"),
                        tpCompany,
                        SystemUsername.valueOf("john@tp.com")
                );

        final CustomerCollaborator controlAreaCollaborator =
                new CustomerCollaborator(
                        "Jane",
                        "Doe",
                        CollaboratorEmail.valueOf("jane@tp.com"),
                        airControlArea("LISACC"),
                        SystemUsername.valueOf("jane@tp.com")
                );

        repository.save(transportCollaborator);
        repository.save(controlAreaCollaborator);

        final List<CustomerCollaborator> result =
                repository.findByAirTransportCompany(tpCompany);

        assertEquals(1, result.size());
        assertTrue(result.contains(transportCollaborator));
        assertFalse(result.contains(controlAreaCollaborator));
    }

    @Test
    void ensureFindByAirControlAreaOnlyReturnsMatchingArea() {

        final InMemoryCustomerCollaboratorRepository repository =
                new InMemoryCustomerCollaboratorRepository();

        final AirControlArea lisacc = airControlArea("LISACC");
        final AirControlArea lppo = airControlArea("LPPO");

        final CustomerCollaborator lisaccCollaborator =
                new CustomerCollaborator(
                        "John",
                        "Doe",
                        CollaboratorEmail.valueOf("john@lisacc.com"),
                        lisacc,
                        SystemUsername.valueOf("john@lisacc.com")
                );

        final CustomerCollaborator lppoCollaborator =
                new CustomerCollaborator(
                        "Jane",
                        "Doe",
                        CollaboratorEmail.valueOf("jane@lppo.com"),
                        lppo,
                        SystemUsername.valueOf("jane@lppo.com")
                );

        repository.save(lisaccCollaborator);
        repository.save(lppoCollaborator);

        final List<CustomerCollaborator> result =
                repository.findByAirControlArea(lisacc);

        assertEquals(1, result.size());
        assertTrue(result.contains(lisaccCollaborator));
    }

    private AirTransportCompany airTransportCompany(final String iataCode, final String icaoCode) {

        return new AirTransportCompany(
                "Test Company " + iataCode,
                Country.valueOf("Portugal"),
                IATACode.valueOf(iataCode),
                ICAOCode.valueOf(icaoCode)
        );
    }

    private AirControlArea airControlArea(final String code) {

        return new AirControlArea(
                AirControlAreaCode.valueOf(code),
                "Test Area " + code,
                GeographicBoundary.valueOf(35.0, 45.0, -15.0, -5.0),
                MinimumFuelRequirement.valueOf(1000, FuelUnit.valueOf("KG"))
        );
    }
}
