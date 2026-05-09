package isep.aisafe.airtransportcompany.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirTransportCompanyTest {

    @Test
    void ensureProtectedConstructorExistsForOrm() {
        final AirTransportCompany company = new AirTransportCompany();

        assertNotNull(company);
    }

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

    @Test
    void ensureNullCompanyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                null,
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));
    }

    @Test
    void ensureEmptyCompanyNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "   ",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));
    }

    @Test
    void ensureNullCountryIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "TAP Air Portugal",
                null,
                IATACode.valueOf("TP"),
                ICAOCode.valueOf("TAP")
        ));
    }

    @Test
    void ensureNullIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                null,
                ICAOCode.valueOf("TAP")
        ));
    }

    @Test
    void ensureNullIcaoCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AirTransportCompany(
                "TAP Air Portugal",
                Country.valueOf("Portugal"),
                IATACode.valueOf("TP"),
                null
        ));
    }

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