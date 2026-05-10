package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuelUnitTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final FuelUnit unit = new FuelUnit();

        assertNotNull(unit);
    }

    @Test
    void ensureKgFuelUnitIsAccepted() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertEquals("KG", unit.toString());
    }

    @Test
    void ensureLFuelUnitIsAccepted() {
        final FuelUnit unit = FuelUnit.valueOf("L");

        assertEquals("L", unit.toString());
    }

    @Test
    void ensureFuelUnitIsNormalizedToUppercase() {
        final FuelUnit unit = FuelUnit.valueOf("kg");

        assertEquals("KG", unit.toString());
    }

    @Test
    void ensureFuelUnitTrimsSpaces() {
        final FuelUnit unit = FuelUnit.valueOf(" kg ");

        assertEquals("KG", unit.toString());
    }

    @Test
    void ensureNullFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FuelUnit.valueOf(null));
    }

    @Test
    void ensureEmptyFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FuelUnit.valueOf("   "));
    }

    @Test
    void ensureInvalidFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FuelUnit.valueOf("GAL"));
    }

    @Test
    void ensureSameFuelUnitInstanceIsEqual() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertEquals(unit, unit);
    }

    @Test
    void ensureFuelUnitsWithSameValueAreEqual() {
        final FuelUnit first = FuelUnit.valueOf("KG");
        final FuelUnit second = FuelUnit.valueOf("kg");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureFuelUnitsWithDifferentValuesAreNotEqual() {
        final FuelUnit first = FuelUnit.valueOf("KG");
        final FuelUnit second = FuelUnit.valueOf("L");

        assertNotEquals(first, second);
    }

    @Test
    void ensureFuelUnitIsNotEqualToDifferentObjectType() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertNotEquals(unit, "KG");
    }

    @Test
    void ensureFuelUnitIsNotEqualToNull() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertNotEquals(null, unit);
    }
}