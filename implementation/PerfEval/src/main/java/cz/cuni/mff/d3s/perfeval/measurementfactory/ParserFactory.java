package cz.cuni.mff.d3s.perfeval.measurementfactory;

import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.JMHJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating parsers and getting their names
 */
public class ParserFactory {
    /**
     * List of possible parsers
     */
    private static final List<MeasurementParser> possibleParsers = new ArrayList<>();

    static {
        possibleParsers.add(new JMHJSONParser());
        possibleParsers.add(new BenchmarkDotNetJSONParser());
    }

    /**
     * Returns parser with given name
     *
     * @param parserName name of parser
     * @return parser with given name
     */
    public static MeasurementParser getParser(String parserName) {
        for (MeasurementParser parser : possibleParsers) {
            if (parser.getParserName().equals(parserName))
                return parser;
        }
        return null;
    }

    /**
     * Returns list of possible names of parsers
     *
     * @return list of possible names of parsers
     */
    public static List<String> getPossibleNames() {
        List<String> result = new ArrayList<>();
        for (MeasurementParser parser : possibleParsers) {
            result.add(parser.getParserName());
        }
        return result;
    }
}
