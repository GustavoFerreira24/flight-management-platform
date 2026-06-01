package isep.aisafe.application.remoteaccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoteAccessResponseTest {

    /**
     * Verifica que respostas OK sao codificadas no formato do protocolo.
     */
    @Test
    void ensureOkResponseCanBeEncoded() {
        final RemoteAccessResponse response = RemoteAccessResponse.ok("accepted");

        assertTrue(response.isOk());
        assertFalse(response.isError());
        assertEquals("OK|accepted", response.encode());
    }

    /**
     * Verifica que respostas de erro sao codificadas no formato do protocolo.
     */
    @Test
    void ensureErrorResponseCanBeEncoded() {
        final RemoteAccessResponse response = RemoteAccessResponse.error("failed");

        assertTrue(response.isError());
        assertEquals("ERROR|failed", response.encode());
    }

    /**
     * Verifica que quebras de linha no payload sao normalizadas para uma linha de protocolo.
     */
    @Test
    void ensurePayloadIsSanitized() {
        final RemoteAccessResponse response = RemoteAccessResponse.data("first\nsecond");

        assertEquals("DATA|first;second", response.encode());
    }
}
