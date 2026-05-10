package isep.aisafe.flightplan.importer.dsl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Entry point for lexical and syntactic validation of Core Flight DSL files.
 */
public class FlightPlanDslSyntaxValidator {

    /**
     * Validates the contents of a Core Flight DSL source string.
     *
     * @param source the DSL source code
     * @return the validation result
     */
    public FlightPlanParseResult validate(final String source) {
        Objects.requireNonNull(source, "source cannot be null");

        return validate(CharStreams.fromString(source));
    }

    /**
     * Loads and validates a Core Flight DSL file.
     *
     * @param file the file path to validate
     * @return the validation result
     * @throws IOException if the file cannot be read
     */
    public FlightPlanParseResult validate(final Path file) throws IOException {
        Objects.requireNonNull(file, "file cannot be null");

        return validate(Files.readString(file));
    }

    private FlightPlanParseResult validate(final CharStream input) {
        final FlightPlanDslLexer lexer = new FlightPlanDslLexer(input);
        final CollectingFlightPlanDslErrorListener errorListener = new CollectingFlightPlanDslErrorListener();

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final FlightPlanDslParser parser = new FlightPlanDslParser(tokens);

        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        final FlightPlanDslParser.FlightPlanFileContext tree = parser.flightPlanFile();

        if (errorListener.hasErrors()) {
            return FlightPlanParseResult.failure(errorListener.errors());
        }

        final FlightPlanSemanticValidator semanticValidator = new FlightPlanSemanticValidator();
        ParseTreeWalker.DEFAULT.walk(semanticValidator, tree);

        if (semanticValidator.hasErrors()) {
            return FlightPlanParseResult.failure(semanticValidator.errors());
        }

        final DslFlightPlan flightPlan = new FlightPlanAstBuilder().build(tree);

        return FlightPlanParseResult.success(tree.toStringTree(parser), flightPlan);
    }
}
