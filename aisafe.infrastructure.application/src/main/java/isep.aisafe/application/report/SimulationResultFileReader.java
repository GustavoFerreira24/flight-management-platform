package isep.aisafe.application.report;

import isep.aisafe.report.domain.SimulationFlightResult;
import isep.aisafe.report.domain.SimulationSafetyViolation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a C simulation report (scomp-simulation/simulation_report.txt)
 * to automatically extract data for the Java US111 report generator.
 */
public class SimulationResultFileReader {

    public static class ParsedData {
        public final boolean passedValidation;
        public final List<SimulationFlightResult> flightResults;
        public final List<SimulationSafetyViolation> safetyViolations;

        public ParsedData(boolean passedValidation, List<SimulationFlightResult> flightResults, List<SimulationSafetyViolation> safetyViolations) {
            this.passedValidation = passedValidation;
            this.flightResults = flightResults;
            this.safetyViolations = safetyViolations;
        }
    }

    /**
     * Reads and parses a C simulation report.
     *
     * @param filePath path to the C simulation report file
     * @return extracted simulation data
     * @throws IOException if the file cannot be read
     */
    public ParsedData readFile(final Path filePath) throws IOException {
        final List<String> lines = Files.readAllLines(filePath);
        
        boolean passedValidation = true;
        final List<SimulationFlightResult> flightResults = new ArrayList<>();
        final List<SimulationSafetyViolation> safetyViolations = new ArrayList<>();

        final Pattern flightPattern = Pattern.compile("\\[FLIGHT (.*?)\\]\\s*Status: (.*)");
        final Pattern violationPattern = Pattern.compile("\\[VIOLATION #\\d+\\]");
        final Pattern genDatePattern = Pattern.compile("Generated:\\s*(.*)");
        
        LocalDateTime generationDate = LocalDateTime.now();

        int i = 0;
        while (i < lines.size()) {
            final String line = lines.get(i).trim();
            
            // Extract Generation Date
            final Matcher dMatch = genDatePattern.matcher(line);
            if (dMatch.find()) {
                try {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    generationDate = LocalDateTime.parse(dMatch.group(1).trim(), formatter);
                } catch (Exception ignored) {
                    // Fallback to now if parse fails
                }
            }

            // Extract Passed Validation status
            if ((line.startsWith("Validation") && line.contains("FAILED")) ||
                (line.contains("FINAL RESULT:") && line.contains("FAILED"))) {
                passedValidation = false;
            }

            // Extract Flights
            final Matcher fMatch = flightPattern.matcher(line);
            if (fMatch.find()) {
                flightResults.add(new SimulationFlightResult(fMatch.group(1).trim(), fMatch.group(2).trim()));
            }

            // Extract Violations
            final Matcher vMatch = violationPattern.matcher(line);
            if (vMatch.find()) {
                String type = "UNKNOWN";
                String flights = "UNKNOWN";
                String position = "UNKNOWN";
                long stepSeconds = 0;

                while (i + 1 < lines.size() && !lines.get(i + 1).startsWith("===") && !lines.get(i + 1).startsWith("[VIOLATION")) {
                    i++;
                    final String vLine = lines.get(i).trim();
                    if (vLine.startsWith("Type")) type = vLine.substring(vLine.indexOf(":") + 1).trim();
                    if (vLine.startsWith("Step") && vLine.contains("t=")) {
                        try {
                            final String tStr = vLine.substring(vLine.indexOf("t=") + 2, vLine.indexOf("s)"));
                            stepSeconds = Long.parseLong(tStr.trim());
                        } catch (Exception ignored) {}
                    }
                    if (vLine.startsWith("Flights")) flights = vLine.substring(vLine.indexOf(":") + 1).trim();
                    if (vLine.contains("position (at detection) :")) {
                        position = vLine.substring(vLine.indexOf(":") + 1).trim();
                    }
                }
                
                final String description = "Type: " + type + " between flights " + flights;
                final LocalDateTime violationTime = generationDate.plusSeconds(stepSeconds);
                safetyViolations.add(new SimulationSafetyViolation(violationTime, position, description));
            } else {
                i++;
            }
        }

        return new ParsedData(passedValidation, flightResults, safetyViolations);
    }
}
