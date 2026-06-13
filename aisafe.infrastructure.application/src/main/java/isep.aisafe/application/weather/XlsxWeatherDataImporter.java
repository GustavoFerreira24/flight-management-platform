package isep.aisafe.application.weather;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Imports weather measurements from the XLSX weather files supplied for US042.
 */
public class XlsxWeatherDataImporter implements WeatherDataImporter {

    private static final String DEFAULT_PROVIDER = "IPMA";
    private static final String DEFAULT_PROVIDER_TYPE = "SATELLITE";
    private static final LocalDate EXCEL_EPOCH = LocalDate.of(1899, 12, 30);

    /**
     * Reads the Wind sheet from the XLSX file and maps each wind row into speed and direction measurements.
     *
     * @param file XLSX file to read
     * @return imported weather measurement records
     * @throws IOException when the XLSX file cannot be read
     */
    @Override
    public List<ImportedWeatherMeasurement> importFrom(final Path file) throws IOException {
        Objects.requireNonNull(file, "XLSX weather data file is required.");

        try (ZipFile workbook = new ZipFile(file.toFile())) {
            final List<String> sharedStrings = readSharedStrings(workbook);
            final List<Map<Integer, String>> rows = readRows(workbook, "xl/worksheets/sheet2.xml", sharedStrings);
            ensureExpectedHeader(rows);

            final List<ImportedWeatherMeasurement> measurements = new ArrayList<>();
            for (int index = 1; index < rows.size(); index++) {
                final Map<Integer, String> row = rows.get(index);
                if (row.isEmpty()) {
                    continue;
                }
                measurements.addAll(parseWindRow(row, index + 1));
            }

            return measurements;
        }
    }

    private List<String> readSharedStrings(final ZipFile workbook) throws IOException {
        final ZipEntry entry = workbook.getEntry("xl/sharedStrings.xml");
        if (entry == null) {
            return List.of();
        }

        final Document document = readXml(workbook, entry);
        final NodeList stringItems = document.getElementsByTagNameNS("*", "si");
        final List<String> sharedStrings = new ArrayList<>();
        for (int index = 0; index < stringItems.getLength(); index++) {
            sharedStrings.add(stringItems.item(index).getTextContent());
        }
        return sharedStrings;
    }

    private List<Map<Integer, String>> readRows(
            final ZipFile workbook,
            final String sheetPath,
            final List<String> sharedStrings) throws IOException {

        final ZipEntry entry = workbook.getEntry(sheetPath);
        if (entry == null) {
            throw new IllegalArgumentException("XLSX weather data file must contain a Wind sheet.");
        }

        final Document document = readXml(workbook, entry);
        final NodeList rowNodes = document.getElementsByTagNameNS("*", "row");
        final List<Map<Integer, String>> rows = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rowNodes.getLength(); rowIndex++) {
            final Element row = (Element) rowNodes.item(rowIndex);
            rows.add(readCells(row, sharedStrings));
        }
        return rows;
    }

    private Map<Integer, String> readCells(final Element row, final List<String> sharedStrings) {
        final Map<Integer, String> cells = new HashMap<>();
        final NodeList cellNodes = row.getElementsByTagNameNS("*", "c");
        for (int cellIndex = 0; cellIndex < cellNodes.getLength(); cellIndex++) {
            final Element cell = (Element) cellNodes.item(cellIndex);
            final String reference = cell.getAttribute("r");
            final String value = cellValue(cell, sharedStrings);
            if (!reference.isBlank() && value != null) {
                cells.put(columnIndex(reference), value);
            }
        }
        return cells;
    }

    private String cellValue(final Element cell, final List<String> sharedStrings) {
        final NodeList values = cell.getElementsByTagNameNS("*", "v");
        if (values.getLength() == 0) {
            return null;
        }

        final String rawValue = values.item(0).getTextContent();
        if ("s".equals(cell.getAttribute("t"))) {
            final int sharedStringIndex = Integer.parseInt(rawValue);
            return sharedStrings.get(sharedStringIndex);
        }

        return rawValue;
    }

    private int columnIndex(final String cellReference) {
        int index = 0;
        for (int i = 0; i < cellReference.length(); i++) {
            final char character = cellReference.charAt(i);
            if (!Character.isLetter(character)) {
                break;
            }
            index = index * 26 + Character.toUpperCase(character) - 'A' + 1;
        }
        return index;
    }

    private void ensureExpectedHeader(final List<Map<Integer, String>> rows) {
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("XLSX weather data file must contain a Wind sheet header.");
        }

        final Map<Integer, String> header = rows.getFirst();
        ensureHeader(header, 1, "ACA");
        ensureHeader(header, 2, "Lat.");
        ensureHeader(header, 3, "Long.");
        ensureHeader(header, 6, "Alt_inf (ft)");
        ensureHeader(header, 7, "Alt_Sup (ft)");
        ensureHeader(header, 8, "Direction (degree)");
        ensureHeader(header, 9, "Value (knot)");
        ensureHeader(header, 10, "Day");
    }

    private void ensureHeader(final Map<Integer, String> header, final int column, final String expected) {
        if (!expected.equals(header.get(column))) {
            throw new IllegalArgumentException("XLSX weather data Wind sheet header is invalid.");
        }
    }

    private List<ImportedWeatherMeasurement> parseWindRow(final Map<Integer, String> row, final int sourceLine) {
        final String areaCode = normalizeAreaCode(required(row, 1, sourceLine));
        final double latitude = parseDouble(required(row, 2, sourceLine), sourceLine, "Lat.");
        final double longitude = parseDouble(required(row, 3, sourceLine), sourceLine, "Long.");
        final double altitude = averageAltitude(
                parseDouble(required(row, 6, sourceLine), sourceLine, "Alt_inf (ft)"),
                parseDouble(required(row, 7, sourceLine), sourceLine, "Alt_Sup (ft)")
        );
        final double direction = parseDouble(required(row, 8, sourceLine), sourceLine, "Direction (degree)");
        final double speed = parseDouble(required(row, 9, sourceLine), sourceLine, "Value (knot)");
        final String day = parseExcelDay(required(row, 10, sourceLine));

        return List.of(
                measurement(sourceLine, day, areaCode, "WIND_SPEED", speed, "KT", altitude, latitude, longitude),
                measurement(sourceLine, day, areaCode, "WIND_DIRECTION", direction, "DEG", altitude, latitude, longitude)
        );
    }

    private ImportedWeatherMeasurement measurement(
            final int sourceLine,
            final String day,
            final String areaCode,
            final String parameter,
            final double value,
            final String unit,
            final double altitude,
            final double latitude,
            final double longitude) {

        return new ImportedWeatherMeasurement(
                sourceLine,
                day,
                areaCode,
                DEFAULT_PROVIDER,
                DEFAULT_PROVIDER_TYPE,
                parameter,
                value,
                unit,
                altitude,
                latitude,
                longitude
        );
    }

    private String required(final Map<Integer, String> row, final int column, final int sourceLine) {
        final String value = row.get(column);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Line " + sourceLine + " has a missing XLSX weather data value.");
        }
        return value;
    }

    private double parseDouble(final String value, final int sourceLine, final String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Line " + sourceLine + " has invalid numeric value for " + fieldName + ".",
                    ex
            );
        }
    }

    private double averageAltitude(final double lowerAltitude, final double upperAltitude) {
        return (lowerAltitude + upperAltitude) / 2.0;
    }

    private String normalizeAreaCode(final String value) {
        final double numericValue = Double.parseDouble(value);
        if (numericValue == Math.rint(numericValue)) {
            return Long.toString(Math.round(numericValue));
        }
        return value.trim();
    }

    private String parseExcelDay(final String value) {
        final double serialValue = Double.parseDouble(value);
        return EXCEL_EPOCH.plusDays((long) serialValue).toString();
    }

    private Document readXml(final ZipFile workbook, final ZipEntry entry) throws IOException {
        try (InputStream input = workbook.getInputStream(entry)) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(new String(input.readAllBytes()))));
        } catch (Exception ex) {
            throw new IllegalArgumentException("XLSX weather data file is invalid.", ex);
        }
    }
}
