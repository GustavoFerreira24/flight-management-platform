package isep.aisafe.app.rcomp.remoteaccess;

import java.time.LocalDateTime;

public final class RemoteAccessEvent {

    private static final int FIELD_COUNT = 6;

    private final LocalDateTime timestamp;
    private final String service;
    private final RemoteAccessEventType type;
    private final String username;
    private final String clientIp;
    private final int clientPort;

    public RemoteAccessEvent(
            final LocalDateTime timestamp,
            final String service,
            final RemoteAccessEventType type,
            final String username,
            final String clientIp,
            final int clientPort) {

        this.timestamp = timestamp;
        this.service = valueOrUnknown(service);
        this.type = type;
        this.username = valueOrUnknown(username);
        this.clientIp = valueOrUnknown(clientIp);
        this.clientPort = clientPort;
    }

    public static RemoteAccessEvent now(
            final String service,
            final RemoteAccessEventType type,
            final String username,
            final String clientIp,
            final int clientPort) {

        return new RemoteAccessEvent(LocalDateTime.now(), service, type, username, clientIp, clientPort);
    }

    public static RemoteAccessEvent decode(final String line) {
        final String[] fields = line.split("\\|", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Remote access event must have " + FIELD_COUNT + " fields.");
        }

        return new RemoteAccessEvent(
                LocalDateTime.parse(fields[0]),
                fields[1],
                RemoteAccessEventType.valueOf(fields[2]),
                fields[3],
                fields[4],
                Integer.parseInt(fields[5])
        );
    }

    public String encode() {
        return timestamp + "|" + service + "|" + type + "|" + username + "|" + clientIp + "|" + clientPort;
    }

    public String activeUserKey() {
        return service + "|" + username + "|" + clientIp + "|" + clientPort;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public String service() {
        return service;
    }

    public RemoteAccessEventType type() {
        return type;
    }

    public String username() {
        return username;
    }

    public String clientIp() {
        return clientIp;
    }

    public int clientPort() {
        return clientPort;
    }

    private static String valueOrUnknown(final String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.trim();
    }
}
