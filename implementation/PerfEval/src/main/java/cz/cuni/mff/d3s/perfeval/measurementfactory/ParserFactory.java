package cz.cuni.mff.d3s.perfeval.measurementfactory;

import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.JMHJSONParser;

/**
 * Represents place where methods for constructing Parsers are stored
 */
public class ParserFactory {
    // construct parser according to file structure
    public static final String jmhJSONParserDescription = "JMHJSON";
    public static final String benchmarkDotNetJSONParserDescription = "BenchmarkDotNetJSON";

    /**
     * Recognize parser according to description
     * @param parserDescription description of parser (type and name of benchmark)
     * @return parser
     */
    public static MeasurementParser getParser(String parserDescription){
        if(parserDescription == null)
            return null;
        if(parserDescription.compareTo(jmhJSONParserDescription)==0){
            return new JMHJSONParser();
        }
        if(parserDescription.compareTo(benchmarkDotNetJSONParserDescription)==0){
            return new BenchmarkDotNetJSONParser();
        }
        return null;
    }
}
