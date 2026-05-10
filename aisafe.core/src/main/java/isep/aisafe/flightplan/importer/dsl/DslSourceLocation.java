package isep.aisafe.flightplan.importer.dsl;

public final class DslSourceLocation {

    private final int line;
    private final int column;

    public DslSourceLocation(final int line, final int column) {
        if (line < 1) {
            throw new IllegalArgumentException("line must be greater than or equal to 1");
        }

        if (column < 0) {
            throw new IllegalArgumentException("column must be greater than or equal to 0");
        }

        this.line = line;
        this.column = column;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }
}
