package isep.aisafe.app.backoffice.console;

import eapli.framework.general.domain.model.EmailAddress;
import isep.aisafe.Application;
import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import isep.aisafe.aircraft.domain.Aircraft;
import isep.aisafe.aircraft.domain.AircraftOperationalStatus;
import isep.aisafe.aircraft.domain.AircraftRegistration;
import isep.aisafe.aircraft.domain.AircraftRegistrationCountry;
import isep.aisafe.aircraft.domain.CabinConfiguration;
import isep.aisafe.aircraftmodel.domain.AircraftModel;
import isep.aisafe.aircraftmodel.domain.AircraftModelDesignator;
import isep.aisafe.aircraftmodel.domain.AircraftModelMotorization;
import isep.aisafe.aircraftmodel.domain.AircraftModelType;
import isep.aisafe.airport.domain.Airport;
import isep.aisafe.airport.domain.AirportBuilder;
import isep.aisafe.airport.domain.AirportIATACode;
import isep.aisafe.airport.domain.AirportICAOCode;
import isep.aisafe.airtransportcompany.domain.AirTransportCompany;
import isep.aisafe.airtransportcompany.domain.Country;
import isep.aisafe.airtransportcompany.domain.IATACode;
import isep.aisafe.airtransportcompany.domain.ICAOCode;
import isep.aisafe.enginemodel.domain.EngineModel;
import isep.aisafe.enginemodel.domain.EngineModelDesignator;
import isep.aisafe.enginemodel.domain.EngineType;
import isep.aisafe.enginemodel.domain.FuelType;
import isep.aisafe.flight.domain.Departure;
import isep.aisafe.flight.domain.Flight;
import isep.aisafe.flight.domain.FlightDesignator;
import isep.aisafe.flightplan.domain.FlightPlan;
import isep.aisafe.flightplan.domain.FlightPlanIdentifier;
import isep.aisafe.flightplan.domain.FuelQuantity;
import isep.aisafe.flightroute.domain.FlightRoute;
import isep.aisafe.flightroute.domain.FlightRouteType;
import isep.aisafe.flightroute.domain.RouteDesignator;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.manufacturer.domain.Manufacturer;
import isep.aisafe.manufacturer.domain.ManufacturerDesignator;
import isep.aisafe.manufacturer.domain.ManufacturerType;
import isep.aisafe.pilot.domain.Certification;
import isep.aisafe.pilot.domain.PhoneNumber;
import isep.aisafe.pilot.domain.Pilot;
import isep.aisafe.pilot.domain.PilotID;
import isep.aisafe.pilot.domain.PilotStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DatasetLoader {

    private final Path datasetsFolder;

    private DatasetLoader(final Path datasetsFolder) {
        this.datasetsFolder = datasetsFolder;
    }

    public static void main(final String[] args) throws Exception {
        final Path folder = args.length > 0 ? Path.of(args[0]) : Path.of("datasets");

        System.out.println("Using datasets folder: " + folder.toAbsolutePath());
        System.out.println("Using DB URL: " + Application.settings()
                .extendedPersistenceProperties()
                .get("jakarta.persistence.jdbc.url"));

        new DatasetLoader(folder).load();

        System.out.println("Dataset load finished.");
    }

    private void load() throws Exception {
        loadAirControlAreas(datasetsFolder.resolve("air_control_areas.csv"));
        loadAirports(datasetsFolder.resolve("airports.csv"));
        loadAirTransportCompanies(datasetsFolder.resolve("air_transport_companies.csv"));
        loadAircraftModels(datasetsFolder.resolve("aircraft_models.xml"));
        loadAircraft(datasetsFolder.resolve("aircraft.csv"));

        loadPilots(datasetsFolder.resolve("pilots.csv"));
        loadFlightRoutes(datasetsFolder.resolve("flight_routes.csv"));
        loadFlights(datasetsFolder.resolve("flights.csv"));
        loadFlightPlans(datasetsFolder.resolve("flight_plans.csv"));
    }

    private void loadAirControlAreas(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var repository = PersistenceContext.repositories().airControlAreas();

        for (String[] row : csvRows(file)) {
            final String codeText = row[0];
            final String name = row[1];
            final double minFuelKg = Double.parseDouble(row[2]);
            final double minLat = Double.parseDouble(row[3]);
            final double maxLat = Double.parseDouble(row[4]);
            final double minLon = Double.parseDouble(row[5]);
            final double maxLon = Double.parseDouble(row[6]);

            final AirControlAreaCode code = AirControlAreaCode.valueOf(codeText);

            if (repository.findByCode(code).isPresent()) {
                System.out.println("Air control area already exists: " + code);
                continue;
            }

            final AirControlArea area = new AirControlArea(
                    code,
                    name,
                    GeographicBoundary.valueOf(minLat, maxLat, minLon, maxLon),
                    MinimumFuelRequirement.valueOf(minFuelKg, FuelUnit.valueOf("KG"))
            );

            repository.save(area);
            System.out.println("Inserted air control area: " + code);
        }
    }

    private void loadAirports(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var airportRepository = PersistenceContext.repositories().airports();
        final var areaRepository = PersistenceContext.repositories().airControlAreas();

        for (String[] row : csvRows(file)) {
            final String iata = row[0];
            final String icao = row[1];
            final String name = row[2];
            final String town = row[3];
            final String country = row[4];
            final double latitude = Double.parseDouble(row[5]);
            final double longitude = Double.parseDouble(row[6]);
            final double altitudeMeters = Double.parseDouble(row[7]);
            final String areaCodeText = row[8];

            final AirportICAOCode icaoCode = AirportICAOCode.valueOf(icao);

            if (airportRepository.findByIcaoCode(icaoCode).isPresent()) {
                System.out.println("Airport already exists: " + icaoCode);
                continue;
            }

            if (airportRepository.findByIataCode(AirportIATACode.valueOf(iata)).isPresent()) {
                System.out.println("Airport with IATA already exists: " + iata);
                continue;
            }

            final AirControlArea area = areaRepository
                    .findByCode(AirControlAreaCode.valueOf(areaCodeText))
                    .orElseThrow(() -> new IllegalStateException("Missing air control area: " + areaCodeText));

            final Airport airport = new AirportBuilder()
                    .withData(icao, iata, name, country, town)
                    .withLocation(latitude, longitude, altitudeMeters)
                    .inArea(area)
                    .build();

            airportRepository.save(airport);
            System.out.println("Inserted airport: " + icaoCode);
        }
    }

    private void loadAirTransportCompanies(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var repository = PersistenceContext.repositories().airTransportCompanies();

        for (String[] row : csvRows(file)) {
            final String iata = row[0];
            final String icao = row[1];
            final String name = row[2];
            final String country = row[3];

            final IATACode iataCode = IATACode.valueOf(iata);
            final ICAOCode icaoCode = ICAOCode.valueOf(icao);

            if (repository.findByIataCode(iataCode).isPresent()) {
                System.out.println("Air transport company already exists: " + iataCode);
                continue;
            }

            if (repository.findByIcaoCode(icaoCode).isPresent()) {
                System.out.println("Air transport company with ICAO already exists: " + icaoCode);
                continue;
            }

            final AirTransportCompany company = new AirTransportCompany(
                    name,
                    Country.valueOf(country),
                    iataCode,
                    icaoCode
            );

            repository.save(company);
            System.out.println("Inserted air transport company: " + iataCode);
        }
    }

    private void loadAircraftModels(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(file.toFile());

        final NodeList aircraftNodes = document.getElementsByTagName("aircraft");

        for (int i = 0; i < aircraftNodes.getLength(); i++) {
            final Element aircraftElement = (Element) aircraftNodes.item(i);

            final String modelId = aircraftElement.getAttribute("model_ID");
            final String maker = text(aircraftElement, "maker");
            final String type = text(aircraftElement, "type");
            final String engineName = text(aircraftElement, "motor");
            final String engineType = text(aircraftElement, "motor_type");

            final Manufacturer aircraftManufacturer = ensureManufacturer(
                    maker,
                    maker.toUpperCase(),
                    defaultCountryForAircraftMaker(maker),
                    "AIRCRAFT"
            );

            final String engineManufacturerDesignator = engineManufacturerFor(engineName);
            final Manufacturer engineManufacturer = ensureManufacturer(
                    displayNameForManufacturer(engineManufacturerDesignator),
                    engineManufacturerDesignator,
                    defaultCountryForEngineMaker(engineManufacturerDesignator),
                    "ENGINE"
            );

            final EngineModel engineModel = ensureEngineModel(
                    engineName,
                    engineManufacturer,
                    engineType,
                    quantity(aircraftElement, "cruise_speed"),
                    quantity(aircraftElement, "thrust_0"),
                    quantity(aircraftElement, "TSFC")
            );

            ensureAircraftModel(
                    aircraftElement,
                    modelId,
                    aircraftManufacturer,
                    type,
                    engineType,
                    engineModel
            );
        }
    }

    private void loadAircraft(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var aircraftRepository = PersistenceContext.repositories().aircraft();
        final var companyRepository = PersistenceContext.repositories().airTransportCompanies();
        final var aircraftModelRepository = PersistenceContext.repositories().aircraftModels();
        final var engineModelRepository = PersistenceContext.repositories().engineModels();

        for (String[] row : csvRows(file)) {
            final String registration = row[0];
            final String companyIata = row[1];
            final String modelId = row[2];
            final String engineName = row[3];
            final int economySeats = Integer.parseInt(row[4]);
            final int businessSeats = Integer.parseInt(row[5]);
            final int firstClassSeats = Integer.parseInt(row[6]);
            final int crewMembers = Integer.parseInt(row[7]);
            final int year = Integer.parseInt(row[8]);
            final String status = row[9];

            final AircraftRegistration aircraftRegistration = AircraftRegistration.valueOf(registration);

            if (aircraftRepository.findByRegistration(aircraftRegistration).isPresent()) {
                System.out.println("Aircraft already exists: " + aircraftRegistration);
                continue;
            }

            final AirTransportCompany company = companyRepository
                    .findByIataCode(IATACode.valueOf(companyIata))
                    .orElseThrow(() -> new IllegalStateException("Missing company: " + companyIata));

            final String modelMaker = aircraftMakerFor(modelId);
            final AircraftModel aircraftModel = aircraftModelRepository
                    .findByDesignator(AircraftModelDesignator.valueOf(modelId, modelMaker))
                    .orElseThrow(() -> new IllegalStateException("Missing aircraft model: " + modelId + " | " + modelMaker));

            final String engineMaker = engineManufacturerFor(engineName);
            final EngineModel engineModel = engineModelRepository
                    .findByDesignator(EngineModelDesignator.valueOf(engineName, engineMaker))
                    .orElseThrow(() -> new IllegalStateException("Missing engine model: " + engineName + " | " + engineMaker));

            final Aircraft aircraft = new Aircraft(
                    aircraftRegistration,
                    AircraftRegistrationCountry.valueOf(company.country().toString()),
                    CabinConfiguration.valueOf(economySeats, businessSeats, firstClassSeats),
                    AircraftOperationalStatus.valueOf(status),
                    crewMembers,
                    company,
                    aircraftModel,
                    engineModel,
                    year
            );

            aircraftRepository.save(aircraft);
            System.out.println("Inserted aircraft: " + aircraftRegistration);
        }
    }

    private void loadPilots(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var pilotRepository = PersistenceContext.repositories().pilots();
        final var companyRepository = PersistenceContext.repositories().airTransportCompanies();

        for (String[] row : csvRows(file)) {
            final String pilotIdText = row[0];
            final String companyIataText = row[1];
            final String emailText = row[2];
            final String phoneText = row[3];
            final String firstName = row[4];
            final String lastName = row[5];
            final String certificationIssuedAt = row[6];
            final String certificationValidUntil = row[7];
            final String certifiedModelsText = row[8];

            final PilotID pilotId = PilotID.valueOf(pilotIdText);

            if (pilotRepository.findByCode(pilotId).isPresent()) {
                System.out.println("Pilot already exists: " + pilotId);
                continue;
            }

            if (pilotRepository.findByEmail(emailText).isPresent()) {
                System.out.println("Pilot with email already exists: " + emailText);
                continue;
            }

            final IATACode companyIata = IATACode.valueOf(companyIataText);

            companyRepository.findByIataCode(companyIata)
                    .orElseThrow(() -> new IllegalStateException("Missing company: " + companyIataText));

            final Set<AircraftModelDesignator> certifiedModels = new HashSet<>();

            for (final String modelId : certifiedModelsText.split(";")) {
                final String cleanModelId = modelId.trim();
                certifiedModels.add(AircraftModelDesignator.valueOf(cleanModelId, aircraftMakerFor(cleanModelId)));
            }

            final Certification certification = new Certification(certificationIssuedAt, certificationValidUntil);

            final Pilot pilot = new Pilot(
                    pilotId,
                    certification,
                    PilotStatus.valueOf(certification),
                    companyIata,
                    EmailAddress.valueOf(emailText),
                    PhoneNumber.valueOf(phoneText),
                    firstName,
                    lastName,
                    certifiedModels
            );

            pilotRepository.save(pilot);
            System.out.println("Inserted pilot: " + pilotId);
        }
    }

    private void loadFlightRoutes(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var routeRepository = PersistenceContext.repositories().flightRoutes();
        final var companyRepository = PersistenceContext.repositories().airTransportCompanies();
        final var airportRepository = PersistenceContext.repositories().airports();

        for (String[] row : csvRows(file)) {
            final String routeCodeText = row[0];
            final String companyIataText = row[1];
            final String originIcaoText = row[2];
            final String destinationIcaoText = row[3];
            final String routeTypeText = row[4];

            final RouteDesignator routeDesignator = RouteDesignator.valueOf(routeCodeText);

            if (routeRepository.findByRouteDesignator(routeDesignator).isPresent()) {
                System.out.println("Flight route already exists: " + routeDesignator);
                continue;
            }

            final AirTransportCompany company = companyRepository
                    .findByIataCode(IATACode.valueOf(companyIataText))
                    .orElseThrow(() -> new IllegalStateException("Missing company: " + companyIataText));

            final Airport origin = airportRepository
                    .findByIcaoCode(AirportICAOCode.valueOf(originIcaoText))
                    .orElseThrow(() -> new IllegalStateException("Missing origin airport: " + originIcaoText));

            final Airport destination = airportRepository
                    .findByIcaoCode(AirportICAOCode.valueOf(destinationIcaoText))
                    .orElseThrow(() -> new IllegalStateException("Missing destination airport: " + destinationIcaoText));

            final FlightRoute route = new FlightRoute(
                    routeDesignator,
                    FlightRouteType.valueOf(routeTypeText),
                    company,
                    origin,
                    destination
            );

            routeRepository.save(route);
            System.out.println("Inserted flight route: " + routeDesignator);
        }
    }

    private void loadFlights(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var flightRepository = PersistenceContext.repositories().flights();
        final var routeRepository = PersistenceContext.repositories().flightRoutes();

        for (String[] row : csvRows(file)) {
            final String flightDesignatorText = row[0];
            final String routeCodeText = row[1];
            final String departureDateTimeText = row[2];

            final FlightDesignator flightDesignator = FlightDesignator.valueOf(flightDesignatorText);

            if (flightRepository.findByDesignator(flightDesignator).isPresent()) {
                System.out.println("Flight already exists: " + flightDesignator);
                continue;
            }

            final FlightRoute route = routeRepository
                    .findByRouteDesignator(RouteDesignator.valueOf(routeCodeText))
                    .orElseThrow(() -> new IllegalStateException("Missing flight route: " + routeCodeText));

            final Flight flight = new Flight(
                    flightDesignator,
                    route,
                    Departure.valueOf(LocalDateTime.parse(departureDateTimeText))
            );

            flightRepository.save(flight);
            System.out.println("Inserted flight: " + flightDesignator);
        }
    }

    private void loadFlightPlans(final Path file) throws Exception {
        if (!Files.exists(file)) {
            System.out.println("Skipping missing file: " + file);
            return;
        }

        final var flightPlanRepository = PersistenceContext.repositories().flightPlans();
        final var flightRepository = PersistenceContext.repositories().flights();
        final var aircraftRepository = PersistenceContext.repositories().aircraft();
        final var pilotRepository = PersistenceContext.repositories().pilots();

        for (String[] row : csvRows(file)) {
            final String flightPlanIdText = row[0];
            final String flightDesignatorText = row[1];
            final String aircraftRegistrationText = row[2];
            final String pilotIdText = row[3];
            final double fuelAmount = Double.parseDouble(row[4]);
            final String fuelUnitText = row[5];

            final FlightPlanIdentifier flightPlanIdentifier = FlightPlanIdentifier.valueOf(flightPlanIdText);

            if (flightPlanRepository.findByIdentifier(flightPlanIdentifier).isPresent()) {
                System.out.println("Flight plan already exists: " + flightPlanIdentifier);
                continue;
            }

            final Flight flight = flightRepository
                    .findByDesignator(FlightDesignator.valueOf(flightDesignatorText))
                    .orElseThrow(() -> new IllegalStateException("Missing flight: " + flightDesignatorText));

            final Aircraft aircraft = aircraftRepository
                    .findByRegistration(AircraftRegistration.valueOf(aircraftRegistrationText))
                    .orElseThrow(() -> new IllegalStateException("Missing aircraft: " + aircraftRegistrationText));

            final Pilot pilot = pilotRepository
                    .findByCode(PilotID.valueOf(pilotIdText))
                    .orElseThrow(() -> new IllegalStateException("Missing pilot: " + pilotIdText));

            final FlightPlan flightPlan = new FlightPlan(
                    flightPlanIdentifier,
                    flight,
                    aircraft,
                    pilot,
                    FuelQuantity.valueOf(fuelAmount, FuelUnit.valueOf(fuelUnitText))
            );

            flightPlanRepository.save(flightPlan);
            System.out.println("Inserted flight plan: " + flightPlanIdentifier);
        }
    }

    private Manufacturer ensureManufacturer(
            final String name,
            final String designatorText,
            final String country,
            final String type) {

        final var repository = PersistenceContext.repositories().manufacturers();
        final ManufacturerDesignator designator = ManufacturerDesignator.valueOf(designatorText);

        return repository.findByDesignator(designator).orElseGet(() -> {
            final Manufacturer manufacturer = new Manufacturer(
                    name,
                    designator,
                    Country.valueOf(country),
                    ManufacturerType.valueOf(type)
            );

            final Manufacturer saved = repository.save(manufacturer);
            System.out.println("Inserted manufacturer: " + designator);
            return saved;
        });
    }

    private EngineModel ensureEngineModel(
            final String engineName,
            final Manufacturer manufacturer,
            final String engineTypeText,
            final double power,
            final double thrust,
            final double tsfc) {

        final var repository = PersistenceContext.repositories().engineModels();
        final EngineModelDesignator designator = EngineModelDesignator.valueOf(
                engineName,
                manufacturer.identity().toString()
        );

        return repository.findByDesignator(designator).orElseGet(() -> {
            final EngineModel engineModel = new EngineModel(
                    designator,
                    manufacturer,
                    EngineType.valueOf(engineTypeText),
                    FuelType.valueOf("JET A-1"),
                    Math.max(power, 0.01),
                    Math.max(thrust, 0.01),
                    Math.max(tsfc, 0.000001)
            );

            final EngineModel saved = repository.save(engineModel);
            System.out.println("Inserted engine model: " + designator);
            return saved;
        });
    }

    private void ensureAircraftModel(
            final Element aircraftElement,
            final String modelId,
            final Manufacturer manufacturer,
            final String type,
            final String motorization,
            final EngineModel engineModel) {

        final var repository = PersistenceContext.repositories().aircraftModels();
        final AircraftModelDesignator designator = AircraftModelDesignator.valueOf(
                modelId,
                manufacturer.identity().toString()
        );

        if (repository.findByDesignator(designator).isPresent()) {
            System.out.println("Aircraft model already exists: " + designator);
            return;
        }

        final double maxRange = defaultRangeFor(modelId);
        final double emptyWeight = weightKg(aircraftElement, "EWeight");
        final double mtow = weightKg(aircraftElement, "MTOW");
        final double maxPayload = weightKg(aircraftElement, "max_payload");
        final double maxFuelCapacity = fuelCapacity(aircraftElement, "fuel_capacity");
        final double serviceCeiling = altitudeMeters(aircraftElement, "cruise_altitude");
        final double cruiseSpeed = speedKmh(aircraftElement, "cruise_speed");
        final double wingArea = quantity(aircraftElement, "wing_area");
        final double dragCoefficient = quantity(aircraftElement, "Cdrag_0");
        final double liftCoefficient = quantity(aircraftElement, "e");
        final int passengerCapacity = defaultCapacityFor(modelId);

        final AircraftModel aircraftModel = new AircraftModel(
                designator,
                manufacturer,
                AircraftModelType.valueOf(type),
                AircraftModelMotorization.valueOf(motorization),
                maxRange,
                emptyWeight,
                mtow,
                Math.max(1.0, mtow - maxPayload),
                maxFuelCapacity,
                serviceCeiling,
                cruiseSpeed,
                wingArea,
                dragCoefficient,
                liftCoefficient,
                passengerCapacity,
                List.of(engineModel)
        );

        repository.save(aircraftModel);
        System.out.println("Inserted aircraft model: " + designator);
    }

    private List<String[]> csvRows(final Path file) throws Exception {
        return Files.readAllLines(file)
                .stream()
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(line -> line.split(",", -1))
                .map(fields -> {
                    for (int i = 0; i < fields.length; i++) {
                        fields[i] = fields[i].trim();
                    }
                    return fields;
                })
                .toList();
    }

    private String text(final Element element, final String tagName) {
        final NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException("Missing XML tag: " + tagName);
        }
        return nodes.item(0).getTextContent().trim();
    }

    private double quantity(final Element element, final String tagName) {
        final String value = text(element, tagName);
        final String number = value.split("\\s+")[0];
        return Double.parseDouble(number);
    }

    private double weightKg(final Element element, final String tagName) {
        final String value = text(element, tagName);
        final double number = Double.parseDouble(value.split("\\s+")[0]);
        return value.toUpperCase().contains("US") ? number * 0.45359237 : number;
    }

    private double fuelCapacity(final Element element, final String tagName) {
        final String value = text(element, tagName);
        final double number = Double.parseDouble(value.split("\\s+")[0]);
        return value.toUpperCase().contains("US") ? number * 3.785411784 : number;
    }

    private double altitudeMeters(final Element element, final String tagName) {
        final String value = text(element, tagName);
        final double number = Double.parseDouble(value.split("\\s+")[0]);
        return value.toUpperCase().contains("US") ? number * 0.3048 : number;
    }

    private double speedKmh(final Element element, final String tagName) {
        final String value = text(element, tagName);
        final double number = Double.parseDouble(value.split("\\s+")[0]);
        return value.toUpperCase().contains("M") ? number * 1062.0 : number;
    }

    private String engineManufacturerFor(final String engineName) {
        final String value = engineName.toUpperCase();

        if (value.startsWith("RR") || value.contains("TRENT")) {
            return "ROLLS ROYCE";
        }

        if (value.startsWith("GE")) {
            return "GENERAL ELECTRIC";
        }

        if (value.contains("LEAP") || value.contains("CFM")) {
            return "CFM INTERNATIONAL";
        }

        return "GENERAL ELECTRIC";
    }

    private String displayNameForManufacturer(final String designator) {
        return switch (designator) {
            case "ROLLS ROYCE" -> "Rolls Royce";
            case "GENERAL ELECTRIC" -> "General Electric";
            case "CFM INTERNATIONAL" -> "CFM International";
            default -> designator;
        };
    }

    private String defaultCountryForAircraftMaker(final String maker) {
        return switch (maker.toUpperCase()) {
            case "AIRBUS" -> "France";
            case "BOEING" -> "USA";
            default -> "Unknown";
        };
    }

    private String defaultCountryForEngineMaker(final String maker) {
        return switch (maker.toUpperCase()) {
            case "ROLLS ROYCE" -> "UK";
            case "GENERAL ELECTRIC" -> "USA";
            case "CFM INTERNATIONAL" -> "USA";
            default -> "Unknown";
        };
    }

    private String aircraftMakerFor(final String modelId) {
        final String value = modelId.toUpperCase();

        if (value.startsWith("A")) {
            return "AIRBUS";
        }

        if (value.startsWith("7")) {
            return "BOEING";
        }

        return "BOEING";
    }

    private double defaultRangeFor(final String modelId) {
        return switch (modelId.toUpperCase()) {
            case "A380" -> 15200.0;
            case "777-200ER" -> 14200.0;
            default -> 8000.0;
        };
    }

    private int defaultCapacityFor(final String modelId) {
        return switch (modelId.toUpperCase()) {
            case "A380" -> 853;
            case "777-200ER" -> 314;
            default -> 300;
        };
    }
}