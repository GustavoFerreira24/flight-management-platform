package isep.aisafe.application.remoteaccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parsed request received through the remote access protocol.
 */
public class RemoteAccessRequest {

    private final String command;
    private final List<String> arguments;

    private RemoteAccessRequest(final String command, final List<String> arguments) {
        this.command = command;
        this.arguments = List.copyOf(arguments);
    }

    /**
     * Parses one protocol line in the form COMMAND|arg1|arg2.
     *
     * @param line protocol line
     * @return parsed request
     */
    public static RemoteAccessRequest parse(final String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Remote command cannot be empty.");
        }

        final String[] parts = line.split("\\|", -1);
        final String command = parts[0].trim().toUpperCase();

        if (command.isBlank()) {
            throw new IllegalArgumentException("Remote command cannot be empty.");
        }

        final List<String> arguments = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            arguments.add(parts[i].trim());
        }

        return new RemoteAccessRequest(command, arguments);
    }

    public String command() {
        return command;
    }

    public List<String> arguments() {
        return Collections.unmodifiableList(arguments);
    }

    public String argument(final int index) {
        return arguments.get(index);
    }

    public int argumentCount() {
        return arguments.size();
    }
}
