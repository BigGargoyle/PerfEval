package cz.cuni.mff.d3s.perfeval.measurementfactory;

import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.JMHJSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating parsers and getting their names
 */
public class ParserFactory {
    /**
     * Map of registered parsers
     */
    private static final Map<String, Supplier<MeasurementParser>> registeredParsers = new HashMap<>();

    static {
        // constructed parsers of all possible types
        // factory can be extended by adding new parsers here
        registeredParsers.put(JMHJSONParser.getParserName(), JMHJSONParser::new);
        registeredParsers.put(BenchmarkDotNetJSONParser.getParserName(), BenchmarkDotNetJSONParser::new);
    }

    /**
     * Returns list of possible names of parsers
     *
     * @return list of possible names of parsers
     */
    public static List<String> getPossibleNames() {
        return new ArrayList<>(registeredParsers.keySet());
    }

    public static MeasurementParser getParser(String value) {
        return registeredParsers.get(value).get();
    }
}
