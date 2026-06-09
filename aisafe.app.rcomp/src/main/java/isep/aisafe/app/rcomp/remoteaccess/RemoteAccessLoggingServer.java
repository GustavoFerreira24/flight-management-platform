package isep.aisafe.app.rcomp.remoteaccess;

import java.io.IOException;

public final class RemoteAccessLoggingServer {

    private static final int DEFAULT_UDP_PORT = 9001;
    private static final int DEFAULT_HTTP_PORT = 8080;

    private RemoteAccessLoggingServer() {
    }

    public static void main(final String[] args) throws IOException {
        final int udpPort = argument(args, 0, DEFAULT_UDP_PORT);
        final int httpPort = argument(args, 1, DEFAULT_HTTP_PORT);
        final RemoteAccessEventStore store = new RemoteAccessEventStore();

        final Thread udpServer = new Thread(new RemoteAccessUdpLogServer(udpPort, store), "remote-access-udp-log-server");
        udpServer.setDaemon(false);
        udpServer.start();

        new RemoteAccessHttpLogServer(httpPort, store).start();
    }

    private static int argument(final String[] args, final int index, final int defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }
        return Integer.parseInt(args[index]);
    }
}
