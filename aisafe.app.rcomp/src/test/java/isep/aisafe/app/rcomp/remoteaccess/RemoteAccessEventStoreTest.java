package isep.aisafe.app.rcomp.remoteaccess;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoteAccessEventStoreTest {

    @Test
    void ensureSuccessfulLoginMarksUserAsActive() {
        final RemoteAccessEventStore store = new RemoteAccessEventStore();
        final RemoteAccessEvent login = event(RemoteAccessEventType.LOGIN_SUCCESS);

        store.record(login);

        assertEquals(1, store.recentEvents().size());
        assertEquals(login, store.activeUsers().getFirst());
    }

    @Test
    void ensureLogoutRemovesUserFromActiveUsers() {
        final RemoteAccessEventStore store = new RemoteAccessEventStore();

        store.record(event(RemoteAccessEventType.LOGIN_SUCCESS));
        store.record(event(RemoteAccessEventType.LOGOUT));

        assertEquals(2, store.recentEvents().size());
        assertTrue(store.activeUsers().isEmpty());
    }

    private RemoteAccessEvent event(final RemoteAccessEventType type) {
        return new RemoteAccessEvent(
                LocalDateTime.of(2026, 6, 9, 10, 30),
                "US44",
                type,
                "ipma",
                "127.0.0.1",
                54000
        );
    }
}
