package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimumFuelRequirementTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final MinimumFuelRequirement requirement = new MinimumFuelRequirement();

        assertNotNull(requirement);
    }

    @Test
    void ensureValidMinimumFuelRequirementIsAccepted() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertEquals(5000.0, requirement.amount());
        assertEquals(FuelUnit.valueOf("KG"), requirement.unit());
    }

    @Test
    void ensureZeroAmountIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                MinimumFuelRequirement.valueOf(0.0, FuelUnit.valueOf("KG")));
    }

    @Test
    void ensureNegativeAmountIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                MinimumFuelRequirement.valueOf(-1.0, FuelUnit.valueOf("KG")));
    }

    @Test
    void ensureNullFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                MinimumFuelRequirement.valueOf(5000.0, null));
    }

    @Test
    void ensureSameMinimumFuelRequirementInstanceIsEqual() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertEquals(requirement, requirement);
    }

    @Test
    void ensureMinimumFuelRequirementsWithSameValuesAreEqual() {
        final MinimumFuelRequirement first = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        final MinimumFuelRequirement second = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("kg")
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void ensureMinimumFuelRequirementsWithDifferentAmountsAreNotEqual() {
        final MinimumFuelRequirement first = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        final MinimumFuelRequirement second = MinimumFuelRequirement.valueOf(
                6000.0,
                FuelUnit.valueOf("KG")
        );

        assertNotEquals(first, second);
    }

    @Test
    void ensureMinimumFuelRequirementsWithDifferentUnitsAreNotEqual() {
        final MinimumFuelRequirement first = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        final MinimumFuelRequirement second = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("L")
        );

        assertNotEquals(first, second);
    }

    @Test
    void ensureMinimumFuelRequirementIsNotEqualToDifferentObjectType() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertNotEquals(requirement, "5000 KG");
    }

    @Test
    void ensureMinimumFuelRequirementIsNotEqualToNull() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertNotEquals(null, requirement);
    }

    @Test
    void ensureMinimumFuelRequirementCanBeRepresentedAsText() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertEquals("5000.0 KG", requirement.toString());
    }
}