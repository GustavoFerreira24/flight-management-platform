package isep.aisafe.app.rcomp.remoteaccess;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Interactive TCP client for the RCOMP remote access user stories.
 *
 * The remote protocol is still text based (COMMAND|arg1|arg2), but this client
 * hides those commands behind simple menus so the actor does not need to type
 * protocol lines manually during the demonstration.
 */
public final class RemoteAccessTcpClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9000;
    private static final String DEFAULT_SERVICE = "US44";

    private RemoteAccessTcpClient() {
    }

    public static void main(final String[] args) throws IOException {
        final String host = stringArgument(args, 0, DEFAULT_HOST);
        final int port = intArgument(args, 1, DEFAULT_PORT);
        final String service = stringArgument(args, 2, DEFAULT_SERVICE).toUpperCase(Locale.ROOT);
        final boolean rawMode = args.length > 3 && "RAW".equalsIgnoreCase(args[3]);

        try (
                Socket socket = new Socket(host, port);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {
            final RemoteClientSession session = new RemoteClientSession(serverIn, serverOut, consoleIn, service);
            session.selectService();

            if (rawMode) {
                session.rawCommandLoop();
                return;
            }

            session.runInteractiveMenu();
        }
    }

    private static String stringArgument(final String[] args, final int index, final String defaultValue) {
        if (args.length <= index || args[index].isBlank()) {
            return defaultValue;
        }
        return args[index].trim();
    }

    private static int intArgument(final String[] args, final int index, final int defaultValue) {
        if (args.length <= index || args[index].isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(args[index].trim());
    }

    private static final class RemoteClientSession {

        private final BufferedReader serverIn;
        private final PrintWriter serverOut;
        private final BufferedReader consoleIn;
        private final String service;
        private String username;

        private RemoteClientSession(
                final BufferedReader serverIn,
                final PrintWriter serverOut,
                final BufferedReader consoleIn,
                final String service) {
            this.serverIn = serverIn;
            this.serverOut = serverOut;
            this.consoleIn = consoleIn;
            this.service = service;
        }

        private void selectService() throws IOException {
            printHeader(titleForService());
            sendAndPrint("SERVICE|" + service);
        }

        private void runInteractiveMenu() throws IOException {
            if (!"US44".equals(service) && !"US78".equals(service)) {
                printWarning("No interactive menu is available for " + service + ". Switching to raw command mode.");
                rawCommandLoop();
                return;
            }

            if (!loginLoop()) {
                return;
            }

            boolean keepRunning = true;
            while (keepRunning) {
                if ("US44".equals(service)) {
                    keepRunning = weatherMenu();
                } else {
                    keepRunning = airCompanyMenu();
                }
            }
        }

        private boolean loginLoop() throws IOException {
            printSection("Login");

            while (true) {
                final String typedUsername = askRequired("Username");
                final String password = askPassword("Password");

                final RemoteReply reply = send("LOGIN|" + typedUsername + "|" + password);
                printReply(reply);

                if (reply.isSuccess()) {
                    username = typedUsername;
                    return true;
                }

                final String option = ask("Try again? (Y/n)");
                if ("N".equalsIgnoreCase(option)) {
                    return false;
                }
            }
        }

        private boolean weatherMenu() throws IOException {
            printMenu(
                    "Weather Person Remote Access",
                    "1 - Consult weather data",
                    "2 - Register weather data",
                    "3 - Import bulk weather data",
                    "8 - Send raw command",
                    "9 - Help",
                    "0 - Logout"
            );

            switch (ask("Option")) {
                case "1":
                    consultWeatherData();
                    return true;
                case "2":
                    registerWeatherData();
                    return true;
                case "3":
                    importBulkWeatherData();
                    return true;
                case "8":
                    sendRawCommand();
                    return true;
                case "9":
                    sendAndPrint("HELP");
                    return true;
                case "0":
                    logout();
                    return false;
                default:
                    printWarning("Invalid option.");
                    return true;
            }
        }

        private void consultWeatherData() throws IOException {
            printSection("Consult weather data");
            final String day = askRequired("Day (YYYY-MM-DD)");
            final String areaCode = askRequired("Air control area code");

            sendAndPrint("CONSULT_WEATHER|" + day + "|" + areaCode);
        }

        private void registerWeatherData() throws IOException {
            printSection("Register weather data");
            final String day = askRequired("Day (YYYY-MM-DD)");
            final String areaCode = askRequired("Air control area code");
            final String providerName = askRequired("Provider name");
            final String providerType = askRequired("Provider type");
            final String parameter = askRequired("Parameter");
            final String value = askRequired("Value");
            final String unit = askRequired("Unit");
            final String altitude = askRequired("Altitude");
            final String latitude = askRequired("Latitude");
            final String longitude = askRequired("Longitude");

            sendAndPrint(String.join("|",
                    "REGISTER_WEATHER_DATA",
                    day,
                    areaCode,
                    providerName,
                    providerType,
                    parameter,
                    value,
                    unit,
                    altitude,
                    latitude,
                    longitude
            ));
        }

        private void importBulkWeatherData() throws IOException {
            printSection("Import bulk weather data");
            printInfo("The file path must exist on the server machine, not on the client computer.");
            final String serverFilePath = askRequired("Server file path");

            sendAndPrint("IMPORT_BULK_WEATHER_DATA|" + serverFilePath);
        }

        private boolean airCompanyMenu() throws IOException {
            printMenu(
                    "Air Transport Company Remote Access",
                    "1 - List aircraft fleet",
                    "2 - Register aircraft",
                    "3 - Decommission aircraft",
                    "4 - Create flight route",
                    "8 - Send raw command",
                    "9 - Help",
                    "0 - Logout"
            );

            switch (ask("Option")) {
                case "1":
                    sendAndPrint("LIST_FLEET");
                    return true;
                case "2":
                    registerAircraft();
                    return true;
                case "3":
                    decommissionAircraft();
                    return true;
                case "4":
                    createFlightRoute();
                    return true;
                case "8":
                    sendRawCommand();
                    return true;
                case "9":
                    sendAndPrint("HELP");
                    return true;
                case "0":
                    logout();
                    return false;
                default:
                    printWarning("Invalid option.");
                    return true;
            }
        }

        private void registerAircraft() throws IOException {
            printSection("Register aircraft");
            final String registration = askRequired("Registration");
            final String country = askRequired("Registration country");
            final String status = askRequired("Operational status");
            final String crewSize = askRequired("Crew size");
            final String economySeats = askRequired("Economy seats");
            final String businessSeats = askRequired("Business seats");
            final String firstClassSeats = askRequired("First class seats");
            final String modelName = askRequired("Aircraft model name");
            final String modelManufacturer = askRequired("Aircraft model manufacturer");
            final String engineModelName = askRequired("Engine model name");
            final String engineModelManufacturer = askRequired("Engine model manufacturer");

            sendAndPrint(String.join("|",
                    "REGISTER_AIRCRAFT",
                    registration,
                    country,
                    status,
                    crewSize,
                    economySeats,
                    businessSeats,
                    firstClassSeats,
                    modelName,
                    modelManufacturer,
                    engineModelName,
                    engineModelManufacturer
            ));
        }

        private void decommissionAircraft() throws IOException {
            printSection("Decommission aircraft");
            final String registration = askRequired("Registration");

            sendAndPrint("DECOMMISSION_AIRCRAFT|" + registration);
        }

        private void createFlightRoute() throws IOException {
            printSection("Create flight route");
            final String routeDesignator = askRequired("Route designator");
            final String departureAirportIcao = askRequired("Departure airport ICAO");
            final String destinationAirportIcao = askRequired("Destination airport ICAO");

            sendAndPrint("CREATE_ROUTE|" + routeDesignator + "|" + departureAirportIcao + "|" + destinationAirportIcao);
        }

        private void sendRawCommand() throws IOException {
            printSection("Raw command");
            printInfo("Use this only for testing/debugging. Example: HELP");
            final String command = askRequired("Command");

            sendAndPrint(command);
        }

        private void logout() throws IOException {
            sendAndPrint("LOGOUT");
            username = null;
        }

        private void rawCommandLoop() throws IOException {
            printSection("Raw command mode");
            printInfo("Type HELP to list commands. Type LOGOUT to end the remote session.");

            String line;
            while ((line = consoleIn.readLine()) != null) {
                sendAndPrint(line);

                if ("LOGOUT".equalsIgnoreCase(line.trim())) {
                    break;
                }
            }
        }

        private RemoteReply send(final String command) throws IOException {
            serverOut.println(command);
            return RemoteReply.parse(serverIn.readLine());
        }

        private void sendAndPrint(final String command) throws IOException {
            printReply(send(command));
        }

        private String askRequired(final String label) throws IOException {
            while (true) {
                final String value = ask(label);
                if (!value.isBlank()) {
                    return value;
                }
                printWarning(label + " is required.");
            }
        }

        private String askPassword(final String label) throws IOException {
            final Console console = System.console();
            if (console != null) {
                final char[] password = console.readPassword(label + ": ");
                return password == null ? "" : new String(password).trim();
            }
            return ask(label);
        }

        private String ask(final String label) throws IOException {
            System.out.print(label + ": ");
            final String value = consoleIn.readLine();
            return value == null ? "" : value.trim();
        }

        private String titleForService() {
            if ("US44".equals(service)) {
                return "AI-SAFE Remote Access - US44 Weather Person";
            }
            if ("US78".equals(service)) {
                return "AI-SAFE Remote Access - US78 Air Company";
            }
            return "AI-SAFE Remote Access - " + service;
        }

        private void printReply(final RemoteReply reply) {
            switch (reply.status()) {
                case "OK":
                    printSuccess(reply.payload());
                    break;
                case "DATA":
                    printData(reply.payload());
                    break;
                case "ERROR":
                    printError(reply.payload());
                    break;
                default:
                    printInfo(reply.encode());
                    break;
            }
        }

        private void printData(final String payload) {
            if (payload.isBlank()) {
                printInfo("No data returned.");
                return;
            }

            System.out.println("DATA");
            for (String line : payload.split(";")) {
                if (!line.isBlank()) {
                    System.out.println("  - " + line.trim());
                }
            }
        }

        private static void printHeader(final String title) {
            System.out.println();
            System.out.println("============================================================");
            System.out.println(" " + title);
            System.out.println("============================================================");
        }

        private static void printMenu(final String title, final String... options) {
            System.out.println();
            System.out.println("-- " + title + " --");
            for (String option : options) {
                System.out.println(option);
            }
            System.out.println();
        }

        private static void printSection(final String title) {
            System.out.println();
            System.out.println("-- " + title + " --");
        }

        private static void printSuccess(final String message) {
            System.out.println("OK " + message);
        }

        private static void printWarning(final String message) {
            System.out.println("WARN " + message);
        }

        private static void printError(final String message) {
            System.out.println("ERROR " + message);
        }

        private static void printInfo(final String message) {
            System.out.println("INFO " + message);
        }
    }

    private static final class RemoteReply {

        private final String status;
        private final String payload;

        private RemoteReply(final String status, final String payload) {
            this.status = status;
            this.payload = payload;
        }

        private static RemoteReply parse(final String encoded) {
            if (encoded == null) {
                return new RemoteReply("ERROR", "Connection closed by server.");
            }

            final String[] parts = encoded.split("\\|", 2);
            if (parts.length == 1) {
                return new RemoteReply("DATA", parts[0]);
            }

            return new RemoteReply(parts[0].trim().toUpperCase(Locale.ROOT), parts[1].trim());
        }

        private boolean isSuccess() {
            return "OK".equals(status);
        }

        private String status() {
            return status;
        }

        private String payload() {
            return payload;
        }

        private String encode() {
            return status + "|" + payload;
        }
    }
}
