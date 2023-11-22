package cz.cuni.mff.d3s.perfeval.measurementfactory;

import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.JMHJSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating parsers and getting their names
 */
public class ParserFactory {
    /**
     * List of possible parsers
     */
    private static final Map<String, MeasurementParser> registeredParsers = new HashMap<>();

    static {
        // constructed parsers of all possible types
        // factory can be extended by adding new parsers here
        registeredParsers.put(new JMHJSONParser().getParserName(), new JMHJSONParser());
        registeredParsers.put(new BenchmarkDotNetJSONParser().getParserName(), new BenchmarkDotNetJSONParser());
    }

    /**
     * Returns parser with given name
     *
     * @param parserName name of parser
     * @return parser with given name
     */
    public static MeasurementParser getParser(String parserName) {
        return registeredParsers.get(parserName);
    }

    /**
     * Returns list of possible names of parsers
     *
     * @return list of possible names of parsers
     */
    public static List<String> getPossibleNames() {
        return new ArrayList<>(registeredParsers.keySet());
    }
}
