package isep.aisafe.app.rcomp.remoteaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class RemoteAccessTcpClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9000;
    private static final String DEFAULT_SERVICE = "US78";

    private RemoteAccessTcpClient() {
    }

    public static void main(final String[] args) throws IOException {
        final String host = stringArgument(args, 0, DEFAULT_HOST);
        final int port = intArgument(args, 1, DEFAULT_PORT);
        final String service = stringArgument(args, 2, DEFAULT_SERVICE);

        try (
                Socket socket = new Socket(host, port);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {
            serverOut.println("SERVICE|" + service);
            System.out.println(serverIn.readLine());
            System.out.println("Type HELP to list commands. Type LOGOUT to end the remote session.");

            String line;
            while ((line = consoleIn.readLine()) != null) {
                serverOut.println(line);
                System.out.println(serverIn.readLine());

                if ("LOGOUT".equalsIgnoreCase(line.trim())) {
                    break;
                }
            }
        }
    }

    private static String stringArgument(final String[] args, final int index, final String defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }
        return args[index];
    }

    private static int intArgument(final String[] args, final int index, final int defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }
        return Integer.parseInt(args[index]);
    }
}
