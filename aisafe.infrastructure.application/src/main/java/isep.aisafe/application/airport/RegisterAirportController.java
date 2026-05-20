package isep.aisafe.application.airport;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.AirControlAreaRepository;
import isep.aisafe.airport.domain.*;
import isep.aisafe.infrastructure.authz.AisafeAuthorizationService;
import isep.aisafe.infrastructure.authz.AisafePermissions;
import isep.aisafe.infrastructure.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsável por registar novos aeroportos no sistema.
 * * <p>Este controlador garante que o utilizador autenticado possui as permissões
 * necessárias e que os dados do aeroporto respeitam as regras de negócio,
 * como a unicidade mundial dos códigos ICAO e IATA.</p>
 */
public class RegisterAirportController {

    private final AisafeAuthorizationService authz;
    private final AirportRepository airportRepo;
    private final AirControlAreaRepository areaRepo;

    /**
     * Construtor por defeito utilizado pela Interface de Utilizador (UI).
     * Inicializa os serviços e repositórios através do contexto de persistência.
     */
    public RegisterAirportController() {
        this(
                new AisafeAuthorizationService(),
                PersistenceContext.repositories().airports(),
                PersistenceContext.repositories().airControlAreas()
        );
    }

    /**
     * Construtor utilizado para testes unitários, permitindo a injeção de dependências.
     *
     * @param authz serviço de autorização
     * @param airportRepo repositório de aeroportos
     * @param areaRepo repositório de áreas de controlo
     */
    RegisterAirportController(
            final AisafeAuthorizationService authz,
            final AirportRepository airportRepo,
            final AirControlAreaRepository areaRepo) {

        if (authz == null || airportRepo == null || areaRepo == null) {
            throw new IllegalArgumentException("As dependências não podem ser nulas.");
        }

        this.authz = authz;
        this.airportRepo = airportRepo;
        this.areaRepo = areaRepo;
    }

    /**
     * Regista um novo aeroporto no sistema.
     *
     * @param icao código ICAO (4 letras)
     * @param iata código IATA (3 letras)
     * @param name nome oficial do aeroporto
     * @param town localidade/cidade
     * @param lat latitude em graus decimais
     * @param lon longitude em graus decimais
     * @param elev elevação em metros acima do nível do mar
     * @param areaCode código da área de controlo aérea associada
     * @return o aeroporto registado e persistido
     * @throws IllegalArgumentException se o código ICAO/IATA já existir ou se a área não for encontrada
     */
    public Airport registerAirport(
            final String icao,
            final String iata,
            final String name,
            final String countryName,
            final String town,
            final double lat,
            final double lon,
            final double elev,
            final String areaCode) {

        // Validar permissões de Backoffice Operator
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        // Validar unicidade do código ICAO
        final AirportICAOCode icaoCode = AirportICAOCode.valueOf(icao);
        if (airportRepo.ofIdentity(icaoCode).isPresent()) {
            throw new IllegalArgumentException("Airport with ICAO code " + icao + " already exists.");
        }

        // Validar unicidade do código IATA
        final AirportIATACode iataCode = AirportIATACode.valueOf(iata);
        if (airportRepo.findByIataCode(iataCode).isPresent()) {
            throw new IllegalArgumentException("Airport with IATA code " + iata + " already exists.");
        }

        // Procurar e validar a existência da Air Control Area [cite: 622]
        final AirControlArea area = areaRepo.ofIdentity(AirControlAreaCode.valueOf(areaCode)).orElseThrow(() -> new IllegalArgumentException("Air Control Area não encontrada!"));

        // Utilizar o Builder para garantir a integridade do domínio
        final AirportBuilder builder = new AirportBuilder();
        builder.withData(icao, iata, name, countryName, town)
                .withLocation(lat, lon, elev)
                .inArea(area);

        return airportRepo.save(builder.build());
    }

    /**
     * Lists air control areas whose geographic boundary contains the given coordinates.
     *
     * @param lat airport latitude
     * @param lon airport longitude
     * @return sorted area selection labels
     */
    public List<String> airControlAreasContaining(final double lat, final double lon) {
        authz.ensureAuthenticatedUserHasAnyOf(AisafePermissions.BACKOFFICE_CONFIGURATION);

        // Reuse the airport coordinate value object to reject invalid coordinates early.
        new GpsCoordinates(lat, lon);

        final List<String> areas = new ArrayList<>();

        for (final AirControlArea area : areaRepo.findAll()) {
            if (area.geographicBoundary().contains(lat, lon)) {
                areas.add(area.code() + " | " + area.name());
            }
        }

        areas.sort(String::compareTo);
        return areas;
    }
}
