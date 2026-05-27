package isep.aisafe.manufacturer.domain;

import isep.aisafe.airtransportcompany.domain.Country;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryManufacturerRepositoryTest {

    private Manufacturer manufacturer() {
        return new Manufacturer(
                "Boeing",
                ManufacturerDesignator.valueOf("BOEING"),
                Country.valueOf("USA"),
                ManufacturerType.valueOf("AIRCRAFT")
        );
    }

    @Test
    void ensureSavedManufacturerCanBeFoundByDesignator() {
        // Arrange
        final InMemoryManufacturerRepository repository = new InMemoryManufacturerRepository();
        final Manufacturer manufacturer = manufacturer();

        // Act
        repository.save(manufacturer);
        final Optional<Manufacturer> result = repository.findByDesignator(manufacturer.designator());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(manufacturer, result.get());
    }

    @Test
    void ensureUnknownManufacturerDesignatorReturnsEmpty() {
        // Arrange
        final InMemoryManufacturerRepository repository = new InMemoryManufacturerRepository();

        // Act
        final Optional<Manufacturer> result = repository.findByDesignator(
                ManufacturerDesignator.valueOf("AIRBUS"));

        // Assert
        assertTrue(result.isEmpty());
    }
}
