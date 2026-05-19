package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirTransportCompanyTest {

    /**
     * Verifica que existe um construtor protegido para suporte do ORM.
     */
    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirTransportCompany company = new AirTransportCompany();

        assertNotNull(company);
    }

    /**
     * Verifica que uma companhia aerea valida e aceite com todos os seus dados.
     */
    @Test
    void ensureValidAirTransportCompanyIsAccepted() {
        final AirTransportCompany company = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        assertEquals("TAP Air Portugal", company.name());
        assertEquals("Portugal", company.country().toString());
        assertEquals("TP", company.iataCode().toString());
        assertEquals("TAP", company.icaoCode().toString());
    }

    /**
     * Verifica que o nome da companhia e normalizado ao remover espacos extra.
     */
    @Test
    void ensureCompanyNameIsNormalized() {
        final AirTransportCompany company = new AirTransportCompany(
                "  TAP   Air   Portugal  ",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        assertEquals("TAP Air Portugal", company.name());
    }

    /**
     * Verifica que uma companhia sem nome e rejeitada.
     */
    @Test
    void ensureNullCompanyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                null,
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));
    }

    /**
     * Verifica que uma companhia com nome vazio e rejeitada.
     */
    @Test
    void ensureEmptyCompanyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "   ",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));
    }

    /**
     * Verifica que uma companhia sem pais e rejeitada.
     */
    @Test
    void ensureNullCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "TAP Air Portugal",
                null,
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));
    }

    /**
     * Verifica que uma companhia sem codigo IATA e rejeitada.
     */
    @Test
    void ensureNullIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                null,
                ICAOCode.valueOf("TAP")
        ));
    }

    /**
     * Verifica que uma companhia sem codigo ICAO e rejeitada.
     */
    @Test
    void ensureNullIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                null
        ));
    }

    /**
     * Verifica que a identidade da companhia corresponde ao codigo IATA.
     */
    @Test
    void ensureIdentityIsIataCode() {
        final AirTransportCompany company = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        assertEquals(IATACode.valueOf("TP"), company.identity());
    }

    /**
     * Verifica que a comparacao sameAs aceita a propria instancia.
     */
    @Test
    void ensureSameAsReturnsTrueForSameInstance() {
        final AirTransportCompany company = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        assertTrue(company.sameAs(company));
    }

    /**
     * Verifica que a comparacao sameAs aceita companhias com a mesma identidade.
     */
    @Test
    void ensureSameAsReturnsTrueForSameIdentity() {
        final AirTransportCompany first = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        final AirTransportCompany second = new AirTransportCompany(
                "Different Name",
                Country.valueOf("Spain"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("ABC")
        );

        assertTrue(first.sameAs(second));
    }

    /**
     * Verifica que a comparacao sameAs rejeita companhias com identidades diferentes.
     */
    @Test
    void ensureSameAsReturnsFalseForDifferentIdentity() {
        final AirTransportCompany first = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        final AirTransportCompany second = new AirTransportCompany(
                "Ryanair",
                Country.valueOf("Ireland"),
                IATACode.valueOf("FR"),
                ICAOCode.valueOf("RYR")
        );

        assertFalse(first.sameAs(second));
    }

    /**
     * Verifica que a comparacao sameAs rejeita objetos de outro tipo.
     */
    @Test
    void ensureSameAsReturnsFalseForDifferentObjectType() {
        final AirTransportCompany company = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        assertFalse(company.sameAs("not a company"));
    }

    /**
     * Verifica que a comparacao sameAs rejeita um valor nulo.
     */
    @Test
    void ensureSameAsReturnsFalseForNull() {
        final AirTransportCompany company = new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        );

        assertFalse(company.sameAs(null));
    }
}
