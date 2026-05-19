package isep.aisafe.aircontrolarea.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirControlAreaCodeTest {

    /**
     * Verifica que o construtor protegido existe para uso pelo ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirControlAreaCode code = new AirControlAreaCode();

        assertNotNull(code);
    }

    /**
     * Verifica que um codigo valido de area de controlo aereo e aceite.
     */
    @Test
    void ensureValidCodeIsAccepted() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertEquals("LPPTFIR", code.toString());
    }

    /**
     * Verifica que o codigo e normalizado para maiusculas.
     */
    @Test
    void ensureCodeIsNormalizedToUppercase() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("lpptfir");

        assertEquals("LPPTFIR", code.toString());
    }

    /**
     * Verifica que o codigo remove espacos no inicio e no fim.
     */
    @Test
    void ensureCodeTrimsSpaces() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf(" lpptfir ");

        assertEquals("LPPTFIR", code.toString());
    }

    /**
     * Verifica que um codigo nulo e rejeitado.
     */
    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf(null));
    }

    /**
     * Verifica que um codigo vazio e rejeitado.
     */
    @Test
    void ensureEmptyCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("   "));
    }

    /**
     * Verifica que um codigo com apenas um caracter e rejeitado.
     */
    @Test
    void ensureCodeWithOneCharacterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("A"));
    }

    /**
     * Verifica que um codigo com mais de dez caracteres e rejeitado.
     */
    @Test
    void ensureCodeWithMoreThanTenCharactersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("ABCDEFGHIJK"));
    }

    /**
     * Verifica que um codigo com simbolos e rejeitado.
     */
    @Test
    void ensureCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> AirControlAreaCode.valueOf("LPPT-FIR"));
    }

    /**
     * Verifica que a mesma instancia de codigo e igual a si propria.
     */
    @Test
    void ensureSameCodeInstanceIsEqual() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertEquals(code, code);
    }

    /**
     * Verifica que codigos com o mesmo valor normalizado sao iguais.
     */
    @Test
    void ensureCodesWithSameValueAreEqual() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("LPPTFIR");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("lpptfir");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que codigos com valores diferentes nao sao iguais.
     */
    @Test
    void ensureCodesWithDifferentValuesAreNotEqual() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("LPPTFIR");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("LPPRFIR");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um codigo nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureCodeIsNotEqualToDifferentObjectType() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertNotEquals(code, "LPPTFIR");
    }

    /**
     * Verifica que um codigo nao e igual a nulo.
     */
    @Test
    void ensureCodeIsNotEqualToNull() {
        final AirControlAreaCode code = AirControlAreaCode.valueOf("LPPTFIR");

        assertNotEquals(null, code);
    }

    /**
     * Verifica que codigos podem ser comparados pela sua ordem natural.
     */
    @Test
    void ensureCodesCanBeCompared() {
        final AirControlAreaCode first = AirControlAreaCode.valueOf("AAAA");
        final AirControlAreaCode second = AirControlAreaCode.valueOf("BBBB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(AirControlAreaCode.valueOf("AAAA")));
    }
}