package isep.aisafe.flightplan.importer.dsl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

/**
 * ANTLR error listener that collects lexical and syntactic errors instead of writing them to stderr.
 */
final class CollectingFlightPlanDslErrorListener extends BaseErrorListener {

    private final List<FlightPlanDslError> errors = new ArrayList<>();

    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer,
                            final Object offendingSymbol,
                            final int line,
                            final int charPositionInLine,
                            final String msg,
                            final RecognitionException e) {
        final FlightPlanDslErrorType type = recognizer instanceof Lexer
                ? FlightPlanDslErrorType.LEXICAL
                : FlightPlanDslErrorType.SYNTACTIC;

        errors.add(new FlightPlanDslError(type, line, charPositionInLine, msg));
    }

    /**
     * Returns true when at least one error was collected.
     *
     * @return true if errors exist
     */
    boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns the collected errors.
     *
     * @return immutable copy of the collected errors
     */
    List<FlightPlanDslError> errors() {
        return List.copyOf(errors);
    }
}
