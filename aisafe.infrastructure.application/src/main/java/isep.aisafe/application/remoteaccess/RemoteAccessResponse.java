package isep.aisafe.application.remoteaccess;

/**
 * Response returned by the remote access command handler.
 */
public class RemoteAccessResponse {

    private final String status;
    private final String payload;

    private RemoteAccessResponse(final String status, final String payload) {
        this.status = status;
        this.payload = sanitize(payload);
    }

    public static RemoteAccessResponse ok(final String message) {
        return new RemoteAccessResponse("OK", message);
    }

    public static RemoteAccessResponse data(final String payload) {
        return new RemoteAccessResponse("DATA", payload);
    }

    public static RemoteAccessResponse error(final String message) {
        return new RemoteAccessResponse("ERROR", message);
    }

    public String status() {
        return status;
    }

    public String payload() {
        return payload;
    }

    public boolean isOk() {
        return "OK".equals(status);
    }

    public boolean isError() {
        return "ERROR".equals(status);
    }

    public String encode() {
        return status + "|" + payload;
    }

    private static String sanitize(final String value) {
        if (value == null) {
            return "";
        }

        return value.replace('\r', ' ').replace('\n', ';').trim();
    }
}
