package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirControlAreaTest {

    /**
     * Verifica que o construtor protegido existe para uso pelo ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirControlArea area = new AirControlArea();

        assertNotNull(area);
    }

    /**
     * Verifica que uma area de controlo aereo valida e aceite com todos os atributos.
     */
    @Test
    void ensureValidAirControlAreaIsAccepted() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertEquals(AirControlAreaCode.valueOf("LPPTFIR"), area.identity());
        assertEquals(AirControlAreaCode.valueOf("LPPTFIR"), area.code());
        assertEquals("Lisbon FIR", area.name());
        assertEquals(GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0), area.geographicBoundary());
        assertEquals(
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG")),
                area.minimumFuelRequirement()
        );
    }

    /**
     * Verifica que o construtor legado cria a area com limites e combustivel por defeito.
     */
    @Test
    void ensureLegacyConstructorCreatesAreaWithDefaultBoundaryAndFuelRequirement() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR"
        );

        assertEquals(AirControlAreaCode.valueOf("LPPTFIR"), area.identity());
        assertEquals("Lisbon FIR", area.name());
        assertEquals(GeographicBoundary.valueOf(-90.0, 90.0, -180.0, 180.0), area.geographicBoundary());
        assertEquals(
                MinimumFuelRequirement.valueOf(1.0, FuelUnit.valueOf("KG")),
                area.minimumFuelRequirement()
        );
    }

    /**
     * Verifica que o nome da area e normalizado removendo espacos em excesso.
     */
    @Test
    void ensureNameIsNormalized() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "  Lisbon   FIR  ",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertEquals("Lisbon FIR", area.name());
    }

    /**
     * Verifica que uma area sem codigo e rejeitada.
     */
    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirControlArea(
                null,
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        ));
    }

    /**
     * Verifica que uma area sem nome e rejeitada.
     */
    @Test
    void ensureNullNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                null,
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        ));
    }

    /**
     * Verifica que uma area com nome vazio e rejeitada.
     */
    @Test
    void ensureEmptyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "   ",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        ));
    }

    /**
     * Verifica que uma area sem limite geografico e rejeitada.
     */
    @Test
    void ensureNullGeographicBoundaryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                null,
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        ));
    }

    /**
     * Verifica que uma area sem requisito minimo de combustivel e rejeitada.
     */
    @Test
    void ensureNullMinimumFuelRequirementIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                null
        ));
    }

    /**
     * Verifica que a identidade da area corresponde ao seu codigo.
     */
    @Test
    void ensureIdentityIsAreaCode() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertEquals(AirControlAreaCode.valueOf("LPPTFIR"), area.identity());
    }

    /**
     * Verifica que sameAs devolve verdadeiro para a mesma instancia.
     */
    @Test
    void ensureSameAsReturnsTrueForSameInstance() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertTrue(area.sameAs(area));
    }

    /**
     * Verifica que sameAs devolve verdadeiro para areas com a mesma identidade.
     */
    @Test
    void ensureSameAsReturnsTrueForSameIdentity() {
        final AirControlArea first = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        final AirControlArea second = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Different Name",
                GeographicBoundary.valueOf(10.0, 20.0, 30.0, 40.0),
                MinimumFuelRequirement.valueOf(6000.0, FuelUnit.valueOf("L"))
        );

        assertTrue(first.sameAs(second));
    }

    /**
     * Verifica que sameAs devolve falso para areas com identidades diferentes.
     */
    @Test
    void ensureSameAsReturnsFalseForDifferentIdentity() {
        final AirControlArea first = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        final AirControlArea second = new AirControlArea(
                AirControlAreaCode.valueOf("LPPRFIR"),
                "Porto FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertFalse(first.sameAs(second));
    }

    /**
     * Verifica que sameAs devolve falso para objetos de outro tipo.
     */
    @Test
    void ensureSameAsReturnsFalseForDifferentObjectType() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertFalse(area.sameAs("not an air control area"));
    }

    /**
     * Verifica que sameAs devolve falso para valor nulo.
     */
    @Test
    void ensureSameAsReturnsFalseForNull() {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.valueOf("LPPTFIR"),
                "Lisbon FIR",
                GeographicBoundary.valueOf(30.0, 45.0, -15.0, 5.0),
                MinimumFuelRequirement.valueOf(5000.0, FuelUnit.valueOf("KG"))
        );

        assertFalse(area.sameAs(null));
    }
}