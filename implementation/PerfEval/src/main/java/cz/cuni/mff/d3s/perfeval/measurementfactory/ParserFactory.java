package cz.cuni.mff.d3s.perfeval.measurementfactory;

import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.JMHJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents place where methods for constructing Parsers are stored
 */
public class ParserFactory {
    // construct parser according to file structure
    private static final List<MeasurementParser> possibleParsers = new ArrayList<>();
    static {
        possibleParsers.add(new JMHJSONParser());
        possibleParsers.add(new BenchmarkDotNetJSONParser());
    }

    /**
     * Recognize parser according to description
     * @param parserName description of parser (name of parser)
     * @return parser
     */
    public static MeasurementParser getParser(String parserName){
        for(MeasurementParser parser : possibleParsers){
            if(parser.getParserName().equals(parserName))
                return parser;
        }
        return null;
    }
}
