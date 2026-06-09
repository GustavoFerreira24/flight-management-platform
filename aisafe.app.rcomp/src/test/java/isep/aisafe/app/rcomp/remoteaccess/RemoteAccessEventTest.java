package isep.aisafe.app.rcomp.remoteaccess;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteAccessEventTest {

    @Test
    void ensureEventCanBeEncodedAndDecoded() {
        final RemoteAccessEvent event = new RemoteAccessEvent(
                LocalDateTime.of(2026, 6, 9, 10, 30),
                "US78",
                RemoteAccessEventType.LOGIN_SUCCESS,
                "tap.collaborator@aisafe.com",
                "192.168.1.10",
                54000
        );

        final RemoteAccessEvent decoded = RemoteAccessEvent.decode(event.encode());

        assertEquals(event.timestamp(), decoded.timestamp());
        assertEquals(event.service(), decoded.service());
        assertEquals(event.type(), decoded.type());
        assertEquals(event.username(), decoded.username());
        assertEquals(event.clientIp(), decoded.clientIp());
        assertEquals(event.clientPort(), decoded.clientPort());
    }
}
