package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimumFuelRequirementTest {

    /**
     * Verifica que o construtor protegido existe para uso pelo ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final MinimumFuelRequirement requirement = new MinimumFuelRequirement();

        assertNotNull(requirement);
    }

    /**
     * Verifica que um requisito minimo de combustivel valido e aceite.
     */
    @Test
    void ensureValidMinimumFuelRequirementIsAccepted() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertEquals(5000.0, requirement.amount());
        assertEquals(FuelUnit.valueOf("KG"), requirement.unit());
    }

    /**
     * Verifica que uma quantidade zero de combustivel e rejeitada.
     */
    @Test
    void ensureZeroAmountIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                MinimumFuelRequirement.valueOf(0.0, FuelUnit.valueOf("KG")));
    }

    /**
     * Verifica que uma quantidade negativa de combustivel e rejeitada.
     */
    @Test
    void ensureNegativeAmountIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                MinimumFuelRequirement.valueOf(-1.0, FuelUnit.valueOf("KG")));
    }

    /**
     * Verifica que um requisito sem unidade de combustivel e rejeitado.
     */
    @Test
    void ensureNullFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                MinimumFuelRequirement.valueOf(5000.0, null));
    }

    /**
     * Verifica que a mesma instancia de requisito minimo de combustivel e igual a si propria.
     */
    @Test
    void ensureSameMinimumFuelRequirementInstanceIsEqual() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertEquals(requirement, requirement);
    }

    /**
     * Verifica que requisitos minimos de combustivel com os mesmos valores sao iguais.
     */
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

    /**
     * Verifica que requisitos minimos com quantidades diferentes nao sao iguais.
     */
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

    /**
     * Verifica que requisitos minimos com unidades diferentes nao sao iguais.
     */
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

    /**
     * Verifica que um requisito minimo nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureMinimumFuelRequirementIsNotEqualToDifferentObjectType() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertNotEquals(requirement, "5000 KG");
    }

    /**
     * Verifica que um requisito minimo nao e igual a nulo.
     */
    @Test
    void ensureMinimumFuelRequirementIsNotEqualToNull() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertNotEquals(null, requirement);
    }

    /**
     * Verifica que um requisito minimo pode ser representado como texto.
     */
    @Test
    void ensureMinimumFuelRequirementCanBeRepresentedAsText() {
        final MinimumFuelRequirement requirement = MinimumFuelRequirement.valueOf(
                5000.0,
                FuelUnit.valueOf("KG")
        );

        assertEquals("5000.0 KG", requirement.toString());
    }
}