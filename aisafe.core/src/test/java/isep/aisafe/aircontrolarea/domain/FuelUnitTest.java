package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuelUnitTest {

    /**
     * Verifica que o construtor protegido existe para uso pelo ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final FuelUnit unit = new FuelUnit();

        assertNotNull(unit);
    }

    /**
     * Verifica que a unidade de combustivel KG e aceite.
     */
    @Test
    void ensureKgFuelUnitIsAccepted() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertEquals("KG", unit.toString());
    }

    /**
     * Verifica que a unidade de combustivel L e aceite.
     */
    @Test
    void ensureLFuelUnitIsAccepted() {
        final FuelUnit unit = FuelUnit.valueOf("L");

        assertEquals("L", unit.toString());
    }

    /**
     * Verifica que a unidade de combustivel e normalizada para maiusculas.
     */
    @Test
    void ensureFuelUnitIsNormalizedToUppercase() {
        final FuelUnit unit = FuelUnit.valueOf("kg");

        assertEquals("KG", unit.toString());
    }

    /**
     * Verifica que a unidade de combustivel remove espacos no inicio e no fim.
     */
    @Test
    void ensureFuelUnitTrimsSpaces() {
        final FuelUnit unit = FuelUnit.valueOf(" kg ");

        assertEquals("KG", unit.toString());
    }

    /**
     * Verifica que uma unidade de combustivel nula e rejeitada.
     */
    @Test
    void ensureNullFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FuelUnit.valueOf(null));
    }

    /**
     * Verifica que uma unidade de combustivel vazia e rejeitada.
     */
    @Test
    void ensureEmptyFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FuelUnit.valueOf("   "));
    }

    /**
     * Verifica que uma unidade de combustivel invalida e rejeitada.
     */
    @Test
    void ensureInvalidFuelUnitIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> FuelUnit.valueOf("GAL"));
    }

    /**
     * Verifica que a mesma instancia de unidade de combustivel e igual a si propria.
     */
    @Test
    void ensureSameFuelUnitInstanceIsEqual() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertEquals(unit, unit);
    }

    /**
     * Verifica que unidades de combustivel com o mesmo valor normalizado sao iguais.
     */
    @Test
    void ensureFuelUnitsWithSameValueAreEqual() {
        final FuelUnit first = FuelUnit.valueOf("KG");
        final FuelUnit second = FuelUnit.valueOf("kg");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que unidades de combustivel com valores diferentes nao sao iguais.
     */
    @Test
    void ensureFuelUnitsWithDifferentValuesAreNotEqual() {
        final FuelUnit first = FuelUnit.valueOf("KG");
        final FuelUnit second = FuelUnit.valueOf("L");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que uma unidade de combustivel nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureFuelUnitIsNotEqualToDifferentObjectType() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertNotEquals(unit, "KG");
    }

    /**
     * Verifica que uma unidade de combustivel nao e igual a nulo.
     */
    @Test
    void ensureFuelUnitIsNotEqualToNull() {
        final FuelUnit unit = FuelUnit.valueOf("KG");

        assertNotEquals(null, unit);
    }
}