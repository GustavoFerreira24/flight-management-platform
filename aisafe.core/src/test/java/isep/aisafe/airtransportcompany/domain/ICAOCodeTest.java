package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ICAOCodeTest {

    /**
     * Verifica que existe um construtor protegido para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final ICAOCode code = new ICAOCode();

        assertNotNull(code);
    }

    /**
     * Verifica que um codigo ICAO valido com duas letras e aceite.
     */
    @Test
    void ensureValidIcaoCodeWithTwoLettersIsAccepted() {
        final ICAOCode code = ICAOCode.valueOf("TP");

        assertEquals("TP", code.toString());
    }

    /**
     * Verifica que um codigo ICAO valido com tres letras e aceite.
     */
    @Test
    void ensureValidIcaoCodeWithThreeLettersIsAccepted() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertEquals("TAP", code.toString());
    }

    /**
     * Verifica que o codigo ICAO e normalizado para maiusculas.
     */
    @Test
    void ensureIcaoCodeIsNormalizedToUppercase() {
        final ICAOCode code = ICAOCode.valueOf("tap");

        assertEquals("TAP", code.toString());
    }

    /**
     * Verifica que o codigo ICAO remove espacos antes e depois.
     */
    @Test
    void ensureIcaoCodeTrimsSpaces() {
        final ICAOCode code = ICAOCode.valueOf(" tap ");

        assertEquals("TAP", code.toString());
    }

    /**
     * Verifica que um codigo ICAO nulo e rejeitado.
     */
    @Test
    void ensureNullIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf(null));
    }

    /**
     * Verifica que um codigo ICAO vazio e rejeitado.
     */
    @Test
    void ensureEmptyIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("   "));
    }

    /**
     * Verifica que um codigo ICAO com uma letra e rejeitado.
     */
    @Test
    void ensureIcaoCodeWithOneLetterIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("T"));
    }

    /**
     * Verifica que um codigo ICAO com quatro letras e rejeitado.
     */
    @Test
    void ensureIcaoCodeWithFourLettersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("TAPP"));
    }

    /**
     * Verifica que um codigo ICAO com numeros e rejeitado.
     */
    @Test
    void ensureIcaoCodeWithNumbersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("TA1"));
    }

    /**
     * Verifica que um codigo ICAO com simbolos e rejeitado.
     */
    @Test
    void ensureIcaoCodeWithSymbolsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> ICAOCode.valueOf("TA-"));
    }

    /**
     * Verifica que a mesma instancia de codigo ICAO e igual a si propria.
     */
    @Test
    void ensureSameIcaoCodeInstanceIsEqual() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertEquals(code, code);
    }

    /**
     * Verifica que codigos ICAO com o mesmo valor sao iguais e tem o mesmo hash.
     */
    @Test
    void ensureIcaoCodesWithSameValueAreEqual() {
        final ICAOCode first = ICAOCode.valueOf("TAP");
        final ICAOCode second = ICAOCode.valueOf("tap");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * Verifica que codigos ICAO com valores diferentes nao sao iguais.
     */
    @Test
    void ensureIcaoCodesWithDifferentValuesAreNotEqual() {
        final ICAOCode first = ICAOCode.valueOf("TAP");
        final ICAOCode second = ICAOCode.valueOf("RYR");

        assertNotEquals(first, second);
    }

    /**
     * Verifica que um codigo ICAO nao e igual a um objeto de outro tipo.
     */
    @Test
    void ensureIcaoCodeIsNotEqualToDifferentObjectType() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertNotEquals(code, "TAP");
    }

    /**
     * Verifica que um codigo ICAO nao e igual a nulo.
     */
    @Test
    void ensureIcaoCodeIsNotEqualToNull() {
        final ICAOCode code = ICAOCode.valueOf("TAP");

        assertNotEquals(null, code);
    }
}
