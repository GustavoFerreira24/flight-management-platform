package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IATACodeTest {

    /**
     * Verifica que existe um construtor protegido para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final IATACode code = new IATACode();

        assertNotNull(code);
    }

    /**
     * Verifica que um codigo IATA valido e aceite e mantem o seu valor.
     */
    @Test
    void ensureValidIataCodeIsAccepted() {
        final IATACode code = IATACode.valueOf("TP");

        assertEquals("TP", code.toString());
    }

    /**
     * Verifica que o codigo IATA e normalizado para maiusculas.
     */
    @Test
    void ensureIataCodeIsNormalizedToUppercase() {
        final IATACode code = IATACode.valueOf("tp");

        assertEquals("TP", code.toString());
    }

    /**
     * Verifica que o codigo IATA remove espacos antes e depois.
     */
    @Test
    void ensureIataCodeTrimsSpaces() {
        final IATACode code = IATACode.valueOf(" tp ");

        assertEquals("TP", code.toString());
    }

    /**
     * Verifica que um codigo IATA nulo e rejeitado.
     */
    @Test
    void ensureNullIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf(null));
    }

    /**
     * Verifica que um codigo IATA vazio e rejeitado.
     */
    @Test
    void ensureEmptyIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("   "));
    }

    /**
     * Verifica que um codigo IATA com uma letra e rejeitado.
     */
    @Test
    void ensureIataCodeWithOneLetterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("T"));
    }

    /**
     * Verifica que um codigo IATA com tres letras e rejeitado.
     */
    @Test
    void ensureIataCodeWithThreeLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("TAP"));
    }

    /**
     * Verifica que um codigo IATA com numeros e rejeitado.
     */
    @Test
    void ensureIataCodeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("T1"));
    }

    /**
     * Verifica que um codigo IATA com simbolos e rejeitado.
     */
    @Test
    void ensureIataCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IATACode.valueOf("T-"));
    }

    /**
     * Verifica que a mesma instancia de codigo IATA e igual a si propria.
     */
    @Test
    void ensureSameIataCodeInstanceIsEqual() {
        final IATACode code = IATACode.valueOf("TP");

        assertEquals(code, code);
    }

    /**
     * Verifica que codigos IATA com o mesmo valor sao iguais e tem o mesmo hash.
     */
    @Test
    void ensureIataCodesWithSameValueAreEqual() {
        final IATACode first = IATACode.valueOf("TP");
        final IATACode second = IATACode.valueOf("tp");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que codigos IATA com valores diferentes nao sao iguais.
     */
    @Test
    void ensureIataCodesWithDifferentValuesAreNotEqual() {
        final IATACode first = IATACode.valueOf("TP");
        final IATACode second = IATACode.valueOf("FR");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um codigo IATA nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureIataCodeIsNotEqualToDifferentObjectType() {
        final IATACode code = IATACode.valueOf("TP");

        assertNotEquals(code, "TP");
    }

    /**
     * Verifica que um codigo IATA nao e igual a nulo.
     */
    @Test
    void ensureIataCodeIsNotEqualToNull() {
        final IATACode code = IATACode.valueOf("TP");

        assertNotEquals(null, code);
    }

    /**
     * Verifica que codigos IATA podem ser comparados por ordem lexicografica.
     */
    @Test
    void ensureIataCodesCanBeCompared() {
        final IATACode first = IATACode.valueOf("AA");
        final IATACode second = IATACode.valueOf("BB");

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(IATACode.valueOf("AA")));
    }
}
