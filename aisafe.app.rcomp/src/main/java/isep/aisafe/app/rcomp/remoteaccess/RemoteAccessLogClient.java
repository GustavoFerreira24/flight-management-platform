package isep.aisafe.app.rcomp.remoteaccess;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public final class RemoteAccessLogClient {

    private final String host;
    private final int port;

    public RemoteAccessLogClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public void send(final RemoteAccessEvent event) {
        try (DatagramSocket socket = new DatagramSocket()) {
            final byte[] data = event.encode().getBytes(StandardCharsets.UTF_8);
            final DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(host),
                    port
            );
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Unable to send remote access log: " + e.getMessage());
        }
    }
}
