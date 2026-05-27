package isep.aisafe.manufacturer.domain;

import isep.aisafe.airtransportcompany.domain.Country;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerTest {

    @Test
    void ensureValidManufacturerIsAccepted() {
        final Manufacturer manufacturer = new Manufacturer(
                "Boeing",
                ManufacturerDesignator.valueOf("BOEING"),
                Country.valueOf("USA"),
                ManufacturerType.valueOf("AIRCRAFT")
        );

        assertEquals(ManufacturerDesignator.valueOf("BOEING"), manufacturer.identity());
        assertEquals("Boeing", manufacturer.name());
        assertTrue(manufacturer.canManufactureAircraftModels());
        assertFalse(manufacturer.canManufactureEngineModels());
    }

    @Test
    void ensureEngineManufacturerTypeIsRecognized() {
        final Manufacturer manufacturer = new Manufacturer(
                "General Electric",
                ManufacturerDesignator.valueOf("GENERAL ELECTRIC"),
                Country.valueOf("USA"),
                ManufacturerType.valueOf("ENGINE")
        );

        assertTrue(manufacturer.canManufactureEngineModels());
        assertFalse(manufacturer.canManufactureAircraftModels());
    }

    @Test
    void ensureCombinedManufacturerTypeIsRecognized() {
        final Manufacturer manufacturer = new Manufacturer(
                "Test Manufacturer",
                ManufacturerDesignator.valueOf("TEST"),
                Country.valueOf("Portugal"),
                ManufacturerType.valueOf("AIRCRAFT_AND_ENGINE")
        );

        assertTrue(manufacturer.canManufactureAircraftModels());
        assertTrue(manufacturer.canManufactureEngineModels());
    }

    @Test
    void ensureRequiredDataIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Manufacturer(
                " ",
                ManufacturerDesignator.valueOf("BOEING"),
                Country.valueOf("USA"),
                ManufacturerType.valueOf("AIRCRAFT")
        ));
        assertThrows(IllegalArgumentException.class, () -> new Manufacturer(
                "Boeing",
                null,
                Country.valueOf("USA"),
                ManufacturerType.valueOf("AIRCRAFT")
        ));
        assertThrows(IllegalArgumentException.class, () -> ManufacturerType.valueOf("UNKNOWN"));
    }
}
