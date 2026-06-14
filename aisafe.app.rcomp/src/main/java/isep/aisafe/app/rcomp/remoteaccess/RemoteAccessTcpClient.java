package isep.aisafe.app.rcomp.remoteaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class RemoteAccessTcpClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9000;
    private static final String DEFAULT_SERVICE = "US78";

    private RemoteAccessTcpClient() {
    }

    public static void main(final String[] args) throws IOException {
        final String host = stringArgument(args, 0, DEFAULT_HOST);
        final int port = intArgument(args, 1, DEFAULT_PORT);
        final String service = stringArgument(args, 2, DEFAULT_SERVICE).toUpperCase(Locale.ROOT);

        try (
                Socket socket = new Socket(host, port);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {
            serverOut.println("SERVICE|" + service);
            printResponse(serverIn.readLine());

            if (!authenticate(consoleIn, serverIn, serverOut)) {
                return;
            }

            switch (service) {
                case "US44":
                    weatherMenu(consoleIn, serverIn, serverOut);
                    break;
                case "US78":
                    airCompanyMenu(consoleIn, serverIn, serverOut);
                    break;
                default:
                    genericMenu(consoleIn, serverIn, serverOut);
                    break;
            }
        }
    }

    private static boolean authenticate(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        System.out.println();
        System.out.println("=== Remote Access Login ===");
        System.out.println("Type 0 as username to exit.");

        while (true) {
            final String username = prompt(consoleIn, "Username");
            if (username == null || "0".equals(username.trim())) {
                return false;
            }

            final String password = prompt(consoleIn, "Password");
            if (password == null) {
                return false;
            }

            final String response = sendCommand(serverIn, serverOut, joinCommand("LOGIN", username, password));
            printResponse(response);

            if (isOk(response)) {
                return true;
            }

            System.out.println("Login failed. Try again or type 0 as username to exit.");
        }
    }

    private static void weatherMenu(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== US44 - Weather Person Remote Access ===");
            System.out.println("1 - Consult weather data");
            System.out.println("2 - Register weather data");
            System.out.println("3 - Import bulk weather data");
            System.out.println("8 - Send raw command");
            System.out.println("9 - Help");
            System.out.println("0 - Logout");

            final String option = prompt(consoleIn, "Option");
            if (option == null) {
                logout(serverIn, serverOut);
                return;
            }

            switch (option) {
                case "1":
                    consultWeather(consoleIn, serverIn, serverOut);
                    break;
                case "2":
                    registerWeatherData(consoleIn, serverIn, serverOut);
                    break;
                case "3":
                    importBulkWeatherData(consoleIn, serverIn, serverOut);
                    break;
                case "8":
                    sendRawCommand(consoleIn, serverIn, serverOut);
                    break;
                case "9":
                    printResponse(sendCommand(serverIn, serverOut, "HELP"));
                    break;
                case "0":
                    logout(serverIn, serverOut);
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void airCompanyMenu(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== US78 - Air Transport Company Remote Access ===");
            System.out.println("1 - List aircraft fleet");
            System.out.println("2 - Register aircraft");
            System.out.println("3 - Decommission aircraft");
            System.out.println("4 - Create flight route");
            System.out.println("5 - Create flight");
            System.out.println("6 - Manage pilots");
            System.out.println("8 - Send raw command");
            System.out.println("9 - Help");
            System.out.println("0 - Logout");

            final String option = prompt(consoleIn, "Option");
            if (option == null) {
                logout(serverIn, serverOut);
                return;
            }

            switch (option) {
                case "1":
                    printResponse(sendCommand(serverIn, serverOut, "LIST_FLEET"));
                    break;
                case "2":
                    registerAircraft(consoleIn, serverIn, serverOut);
                    break;
                case "3":
                    decommissionAircraft(consoleIn, serverIn, serverOut);
                    break;
                case "4":
                    createFlightRoute(consoleIn, serverIn, serverOut);
                    break;
                case "5":
                    createFlight(consoleIn, serverIn, serverOut);
                    break;
                case "6":
                    managePilotsMenu(consoleIn, serverIn, serverOut);
                    break;
                case "8":
                    sendRawCommand(consoleIn, serverIn, serverOut);
                    break;
                case "9":
                    printResponse(sendCommand(serverIn, serverOut, "HELP"));
                    break;
                case "0":
                    logout(serverIn, serverOut);
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void managePilotsMenu(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== Manage Pilots ===");
            System.out.println("1 - Add pilot to roster");
            System.out.println("2 - List pilot roster");
            System.out.println("3 - Delete / Deactivate pilot");
            System.out.println("0 - Back");

            final String option = prompt(consoleIn, "Option");
            if (option == null) {
                return;
            }

            switch (option) {
                case "1":
                    addPilot(consoleIn, serverIn, serverOut);
                    break;
                case "2":
                    printResponse(sendCommand(serverIn, serverOut, "LIST_PILOT_ROSTER"));
                    break;
                case "3":
                    deactivatePilot(consoleIn, serverIn, serverOut);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void genericMenu(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        System.out.println("Type HELP to list commands. Type LOGOUT to end the remote session.");

        String line;
        while ((line = consoleIn.readLine()) != null) {
            printResponse(sendCommand(serverIn, serverOut, line));

            if ("LOGOUT".equalsIgnoreCase(line.trim())) {
                break;
            }
        }
    }

    private static void consultWeather(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String day = prompt(consoleIn, "Day (YYYY-MM-DD)");
        final String areaCode = prompt(consoleIn, "Air control area code");

        printResponse(sendCommand(serverIn, serverOut, joinCommand("CONSULT_WEATHER", day, areaCode)));
    }

    private static void registerWeatherData(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String day = prompt(consoleIn, "Day (YYYY-MM-DD)");
        final String areaCode = prompt(consoleIn, "Air control area code");
        final String providerName = prompt(consoleIn, "Provider name");
        final String providerType = prompt(consoleIn, "Provider type");
        final String parameter = prompt(consoleIn, "Parameter");
        final String value = prompt(consoleIn, "Value");
        final String unit = prompt(consoleIn, "Unit");
        final String altitude = prompt(consoleIn, "Altitude");
        final String latitude = prompt(consoleIn, "Latitude");
        final String longitude = prompt(consoleIn, "Longitude");

        final String command = joinCommand(
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
        );

        printResponse(sendCommand(serverIn, serverOut, command));
    }

    private static void importBulkWeatherData(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String serverFilePath = prompt(consoleIn, "Server file path");
        printResponse(sendCommand(serverIn, serverOut, joinCommand("IMPORT_BULK_WEATHER_DATA", serverFilePath)));
    }

    private static void registerAircraft(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String registration = prompt(consoleIn, "Registration");
        final String country = prompt(consoleIn, "Registration country");
        final String status = prompt(consoleIn, "Operational status");
        final String crewSize = prompt(consoleIn, "Crew size");
        final String economySeats = prompt(consoleIn, "Economy seats");
        final String businessSeats = prompt(consoleIn, "Business seats");
        final String firstClassSeats = prompt(consoleIn, "First class seats");
        final String modelName = prompt(consoleIn, "Aircraft model name");
        final String modelManufacturer = prompt(consoleIn, "Aircraft model manufacturer");
        final String engineModelName = prompt(consoleIn, "Engine model name");
        final String engineModelManufacturer = prompt(consoleIn, "Engine model manufacturer");

        final String command = joinCommand(
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
        );

        printResponse(sendCommand(serverIn, serverOut, command));
    }

    private static void decommissionAircraft(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String registration = prompt(consoleIn, "Aircraft registration");
        printResponse(sendCommand(serverIn, serverOut, joinCommand("DECOMMISSION_AIRCRAFT", registration)));
    }

    private static void createFlightRoute(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String routeDesignator = prompt(consoleIn, "Route designator");
        final String departureAirportIcao = prompt(consoleIn, "Departure airport ICAO");
        final String destinationAirportIcao = prompt(consoleIn, "Destination airport ICAO");

        printResponse(sendCommand(
                serverIn,
                serverOut,
                joinCommand("CREATE_ROUTE", routeDesignator, departureAirportIcao, destinationAirportIcao)
        ));
    }

    private static void createFlight(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String routeDesignator = prompt(consoleIn, "Route designator");
        final String flightDesignator = prompt(consoleIn, "Flight designator");
        final String departureDay = prompt(consoleIn, "Departure day");
        final String departureTime = prompt(consoleIn, "Departure time");

        printResponse(sendCommand(
                serverIn,
                serverOut,
                joinCommand("CREATE_FLIGHT", routeDesignator, flightDesignator, departureDay, departureTime)
        ));
    }

    private static void addPilot(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String pilotId = prompt(consoleIn, "Pilot ID");
        printResponse(sendCommand(serverIn, serverOut, joinCommand("ADD_PILOT", pilotId)));
    }

    private static void deactivatePilot(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String pilotId = prompt(consoleIn, "Pilot ID");
        printResponse(sendCommand(serverIn, serverOut, joinCommand("DEACTIVATE_PILOT", pilotId)));
    }

    private static void sendRawCommand(
            final BufferedReader consoleIn,
            final BufferedReader serverIn,
            final PrintWriter serverOut) throws IOException {

        final String command = prompt(consoleIn, "Command");
        if (command != null && !command.isBlank()) {
            printResponse(sendCommand(serverIn, serverOut, command));
        }
    }

    private static void logout(final BufferedReader serverIn, final PrintWriter serverOut) throws IOException {
        printResponse(sendCommand(serverIn, serverOut, "LOGOUT"));
    }

    private static String sendCommand(
            final BufferedReader serverIn,
            final PrintWriter serverOut,
            final String command) throws IOException {

        serverOut.println(command);
        return serverIn.readLine();
    }

    private static String prompt(final BufferedReader consoleIn, final String label) throws IOException {
        System.out.print(label + ": ");
        final String value = consoleIn.readLine();
        return value == null ? null : value.trim();
    }

    private static void printResponse(final String response) {
        if (response == null || response.isBlank()) {
            System.out.println("No response from server.");
            return;
        }

        final int separator = response.indexOf('|');
        if (separator < 0) {
            System.out.println(response);
            return;
        }

        final String status = response.substring(0, separator);
        final String payload = response.substring(separator + 1);

        System.out.println(status + ": " + payload.replace(';', '\n'));
    }

    private static boolean isOk(final String response) {
        return response != null && response.startsWith("OK|");
    }

    private static String joinCommand(final String command, final String... arguments) {
        final StringBuilder builder = new StringBuilder(command);

        for (String argument : arguments) {
            builder.append('|').append(argument == null ? "" : argument.trim());
        }

        return builder.toString();
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