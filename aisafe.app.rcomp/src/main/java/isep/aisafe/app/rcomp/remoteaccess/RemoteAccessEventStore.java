package isep.aisafe.app.rcomp.remoteaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RemoteAccessEventStore {

    private static final int MAX_EVENTS = 100;

    private final List<RemoteAccessEvent> events = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, RemoteAccessEvent> activeUsers = new ConcurrentHashMap<>();

    public void record(final RemoteAccessEvent event) {
        synchronized (events) {
            events.add(0, event);
            if (events.size() > MAX_EVENTS) {
                events.remove(events.size() - 1);
            }
        }

        if (event.type() == RemoteAccessEventType.LOGIN_SUCCESS) {
            activeUsers.put(event.activeUserKey(), event);
        }
        if (event.type() == RemoteAccessEventType.LOGOUT || event.type() == RemoteAccessEventType.DISCONNECT) {
            activeUsers.remove(event.activeUserKey());
        }
    }

    public List<RemoteAccessEvent> recentEvents() {
        synchronized (events) {
            return List.copyOf(events);
        }
    }

    public List<RemoteAccessEvent> activeUsers() {
        return List.copyOf(activeUsers.values());
    }
}
