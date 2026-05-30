package isep.aisafe.airport.domain;

import isep.aisafe.aircontrolarea.domain.AirControlArea;
import isep.aisafe.aircontrolarea.domain.AirControlAreaCode;
import isep.aisafe.aircontrolarea.domain.FuelUnit;
import isep.aisafe.aircontrolarea.domain.GeographicBoundary;
import isep.aisafe.aircontrolarea.domain.MinimumFuelRequirement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportBuilderTest {

    @Test
    void ensureBuilderCreatesAirportWithValidData() {
        final AirportBuilder builder = new AirportBuilder();
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon",
                GeographicBoundary.valueOf(-90, 90, -180, 180),
                MinimumFuelRequirement.valueOf(1, FuelUnit.valueOf("KG"))
        );

        final Airport airport = builder
                .withData("LPPT", "LIS", "Lisbon Airport", "Portugal","Lisbon")
                .withLocation(38.7, -9.1, 114.0)
                .inArea(area)
                .build();

        assertNotNull(airport);
        assertEquals(AirportICAOCode.valueOf("LPPT"), airport.identity());
        assertEquals("LPPT", airport.icaoCode().toString());
        assertEquals("LIS", airport.iataCode().toString());
    }

    @Test
    void ensureBuilderFailsWhenMandatoryDataIsMissing() {
        final AirportBuilder builder = new AirportBuilder();

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void ensureBuilderPropagatesInvalidIcaoCodeValidationErrors() {
        final AirportBuilder builder = new AirportBuilder();

        assertThrows(IllegalArgumentException.class, () ->
                builder.withData("INVALIDICAO", "LIS", "Aeroporto 3", "Portugal","Vila Nova de Cerveira"));
    }

    @Test
    void ensureBuilderPropagatesInvalidIataCodeValidationErrors() {
        final AirportBuilder builder = new AirportBuilder();

        assertThrows(IllegalArgumentException.class, () ->
                builder.withData("LPPT", "LI", "Aeroporto 2", "Portugal","Vila Nova de Cerveira"));
    }
}
