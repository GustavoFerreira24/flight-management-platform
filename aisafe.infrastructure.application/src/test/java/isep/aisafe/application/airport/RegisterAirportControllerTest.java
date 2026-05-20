package isep.aisafe.application.airport;

import eapli.framework.infrastructure.authz.domain.model.Role;
import isep.aisafe.aircontrolarea.domain.*;
import isep.aisafe.airport.domain.*;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o registo de aeroportos utilizando repositórios em memória.
 */
class RegisterAirportControllerTest {

    @Test
    void ensureValidAirportRegistrationIsAccepted() {
        final Fixture fixture = new Fixture("backoffice@aisafe.com");
        fixture.bootstrapAirControlArea("LISACC");

        final Airport result = fixture.controller().registerAirport(
                "LPPR", "OPO", "Sá Carneiro", "Portugal","Porto", 41.2, -8.6, 69.0, "LISACC"
        );

        // Verifica se a permissão solicitada foi a de configuração de backoffice
        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, fixture.authz.requestedRoles());
        assertEquals(AirportICAOCode.valueOf("LPPR"), result.identity());
        assertTrue(fixture.airportRepository.ofIdentity(AirportICAOCode.valueOf("LPPR")).isPresent());
    }

    @Test
    void ensureDuplicateAirportIcaoIsRejected() {
        final Fixture fixture = new Fixture("backoffice@aisafe.com");
        fixture.bootstrapAirControlArea("LISACC");

        fixture.registerValidAirport("LPPT", "LIS");

        assertThrows(IllegalArgumentException.class, () ->
                fixture.registerValidAirport("LPPT", "LIS"));
    }

    @Test
    void ensureUnknownAirControlAreaIsRejected() {
        final Fixture fixture = new Fixture("backoffice@aisafe.com");

        assertThrows(IllegalArgumentException.class, () ->
                fixture.controller().registerAirport(
                        "LPPR", "OPO", "Aeroporto Duro", "Portugal","Douro", 41.0, -8.0, 50.0, "UNKNOWN"
                ));
    }

    @Test
    void ensureAirControlAreasContainingCoordinatesCanBeListed() {
        final Fixture fixture = new Fixture("backoffice@aisafe.com");
        fixture.bootstrapAirControlArea("LISACC", "Lisbon ACC", 30, 45, -15, 5);
        fixture.bootstrapAirControlArea("IBRACC", "Iberian ACC", 35, 43, -10, 0);

        final List<String> result = fixture.controller().airControlAreasContaining(40.0, -8.0);

        assertArrayEquals(AisafePermissions.BACKOFFICE_CONFIGURATION, fixture.authz.requestedRoles());
        assertTrue(result.containsAll(List.of("IBRACC | Iberian ACC", "LISACC | Lisbon ACC")));
    }

    @Test
    void ensureNoAirControlAreasContainingCoordinatesReturnsEmptyList() {
        final Fixture fixture = new Fixture("backoffice@aisafe.com");
        fixture.bootstrapAirControlArea("LISACC", "Lisbon ACC", 30, 45, -15, 5);

        final List<String> result = fixture.controller().airControlAreasContaining(50.0, 20.0);

        assertTrue(result.isEmpty());
    }

    private static class Fixture {
        private final TestAuthorizationService authz;
        // Inicializa o repositório para remover o erro de compilação
        private final AirportRepository airportRepository = new InMemoryAirportRepository();
        private final InMemoryAirControlAreaRepository areaRepository = new InMemoryAirControlAreaRepository();
        private AirControlArea area;

        Fixture(final String username) {
            this.authz = new TestAuthorizationService(username);
        }

        void bootstrapAirControlArea(final String areaCode) {
            bootstrapAirControlArea(areaCode, "Lisbon ACC", 30, 45, -15, 5);
        }

        void bootstrapAirControlArea(
                final String areaCode,
                final String name,
                final double minLatitude,
                final double maxLatitude,
                final double minLongitude,
                final double maxLongitude) {
            area = new AirControlArea(
                    AirControlAreaCode.valueOf(areaCode),
                    name,
                    GeographicBoundary.valueOf(minLatitude, maxLatitude, minLongitude, maxLongitude),
                    MinimumFuelRequirement.valueOf(5000, FuelUnit.valueOf("KG"))
            );
            areaRepository.save(area);
        }

        RegisterAirportController controller() {
            return new RegisterAirportController(authz, airportRepository, areaRepository);
        }

        Airport registerValidAirport(final String icao, final String iata) {
            return controller().registerAirport(
                    icao, iata, "Test", "Portugal","Test", 40.0, -9.0, 100.0, "LISACC"
            );
        }
    }

    private static class TestAuthorizationService extends AisafeAuthorizationService {
        private final String username;
        private Role[] requestedRoles;

        TestAuthorizationService(final String username) {
            this.username = username;
        }

        @Override
        public void ensureAuthenticatedUserHasAnyOf(final Role... roles) {
            this.requestedRoles = roles;
        }

        @Override
        public String authenticatedUsername() { return username; }

        Role[] requestedRoles() { return requestedRoles; }
    }
}
