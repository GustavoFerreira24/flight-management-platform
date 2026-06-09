package isep.aisafe.app.rcomp.remoteaccess;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public final class RemoteAccessUdpLogServer implements Runnable {

    private static final int BUFFER_SIZE = 512;

    private final int port;
    private final RemoteAccessEventStore store;

    public RemoteAccessUdpLogServer(final int port, final RemoteAccessEventStore store) {
        this.port = port;
        this.store = store;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Remote Access UDP logging server listening on port " + port);

            while (true) {
                final DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(packet);

                final String line = new String(
                        packet.getData(),
                        0,
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                try {
                    store.record(RemoteAccessEvent.decode(line));
                } catch (RuntimeException e) {
                    System.err.println("Invalid remote access log event ignored: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start UDP logging server.", e);
        }
    }
}
