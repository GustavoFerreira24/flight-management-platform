package isep.aisafe.app.rcomp.remoteaccess;

import isep.aisafe.application.remoteaccess.RemoteAccessResponse;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;
import isep.aisafe.infrastructure.persistence.PersistenceContext;
import isep.aisafe.usermanagement.domain.AisafePasswordPolicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class RemoteAccessTcpServer {

    private static final int DEFAULT_TCP_PORT = 9000;
    private static final String DEFAULT_LOG_HOST = "localhost";
    private static final int DEFAULT_LOG_PORT = 9001;

    private RemoteAccessTcpServer() {
    }

    public static void main(final String[] args) throws IOException {
        configureAuthz();

        final int tcpPort = argument(args, 0, DEFAULT_TCP_PORT);
        final String logHost = stringArgument(args, 1, DEFAULT_LOG_HOST);
        final int logPort = argument(args, 2, DEFAULT_LOG_PORT);
        final RemoteAccessLogClient logClient = new RemoteAccessLogClient(logHost, logPort);
        final ExecutorService clients = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
            System.out.println("Remote Access TCP server listening on port " + tcpPort);

            while (true) {
                final Socket clientSocket = serverSocket.accept();
                clients.submit(() -> handleClient(clientSocket, logClient));
            }
        }
    }

    private static void handleClient(final Socket socket, final RemoteAccessLogClient logClient) {
        final String clientIp = socket.getInetAddress().getHostAddress();
        final int clientPort = socket.getPort();
        RemoteAccessService service = null;
        String username = "unknown";
        boolean authenticated = false;

        try (
                socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            final String serviceLine = in.readLine();
            service = selectService(serviceLine);
            final RemoteAccessService.RemoteCommandHandler handler = service.newHandler();
            out.println(RemoteAccessResponse.ok("Remote service selected: " + service + ".").encode());

            String line;
            while ((line = in.readLine()) != null) {
                final String candidateUsername = usernameFromLogin(line);
                final RemoteAccessResponse response = handler.handle(line);
                out.println(response.encode());

                if (candidateUsername != null) {
                    username = candidateUsername;
                    if (response.isOk()) {
                        authenticated = true;
                        log(logClient, service, RemoteAccessEventType.LOGIN_SUCCESS, username, clientIp, clientPort);
                    } else {
                        authenticated = false;
                        log(logClient, service, RemoteAccessEventType.LOGIN_FAILED, username, clientIp, clientPort);
                    }
                }

                if ("LOGOUT".equalsIgnoreCase(line.trim())) {
                    log(logClient, service, RemoteAccessEventType.LOGOUT, username, clientIp, clientPort);
                    authenticated = false;
                    break;
                }
            }
        } catch (RuntimeException | IOException e) {
            System.err.println("Remote client session ended: " + e.getMessage());
        } finally {
            if (service != null && authenticated) {
                log(logClient, service, RemoteAccessEventType.DISCONNECT, username, clientIp, clientPort);
            }
        }
    }

    private static RemoteAccessService selectService(final String serviceLine) {
        if (serviceLine == null || !serviceLine.startsWith("SERVICE|")) {
            throw new IllegalArgumentException("First TCP line must be SERVICE|US44, SERVICE|US78 or SERVICE|US86.");
        }
        return RemoteAccessService.from(serviceLine.substring("SERVICE|".length()));
    }

    private static String usernameFromLogin(final String line) {
        final String[] fields = line.split("\\|", -1);
        if (fields.length >= 2 && "LOGIN".equalsIgnoreCase(fields[0].trim())) {
            return fields[1].trim();
        }
        return null;
    }

    private static void log(
            final RemoteAccessLogClient logClient,
            final RemoteAccessService service,
            final RemoteAccessEventType type,
            final String username,
            final String clientIp,
            final int clientPort) {

        logClient.send(RemoteAccessEvent.now(service.name(), type, username, clientIp, clientPort));
    }

    private static int argument(final String[] args, final int index, final int defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }
        return Integer.parseInt(args[index]);
    }

    private static String stringArgument(final String[] args, final int index, final String defaultValue) {
        if (args.length <= index) {
            return defaultValue;
        }
        return args[index];
    }

    private static void configureAuthz() {
        AuthzRegistry.configure(
                PersistenceContext.repositories().users(),
                new AisafePasswordPolicy(),
                new PlainTextEncoder()
        );
    }
}
