package isep.aisafe.application.remoteaccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoteAccessRequestTest {

    /**
     * Verifica que uma linha do protocolo e dividida em comando e argumentos.
     */
    @Test
    void ensureProtocolLineIsParsed() {
        final RemoteAccessRequest request = RemoteAccessRequest.parse("LOGIN|user|Password1");

        assertEquals("LOGIN", request.command());
        assertEquals(2, request.argumentCount());
        assertEquals("user", request.argument(0));
        assertEquals("Password1", request.argument(1));
    }

    /**
     * Verifica que o comando e normalizado para maiusculas e os argumentos sao aparados.
     */
    @Test
    void ensureCommandAndArgumentsAreNormalized() {
        final RemoteAccessRequest request = RemoteAccessRequest.parse("  list_fleet  |  a  | b ");

        assertEquals("LIST_FLEET", request.command());
        assertEquals("a", request.argument(0));
        assertEquals("b", request.argument(1));
    }

    /**
     * Verifica que argumentos vazios sao preservados para permitir validar aridade.
     */
    @Test
    void ensureEmptyArgumentsArePreserved() {
        final RemoteAccessRequest request = RemoteAccessRequest.parse("LOGIN|user|");

        assertEquals(2, request.argumentCount());
        assertEquals("", request.argument(1));
    }

    /**
     * Verifica que comandos vazios sao rejeitados.
     */
    @Test
    void ensureEmptyCommandIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> RemoteAccessRequest.parse("   "));
        assertThrows(IllegalArgumentException.class, () -> RemoteAccessRequest.parse(null));
    }
}
