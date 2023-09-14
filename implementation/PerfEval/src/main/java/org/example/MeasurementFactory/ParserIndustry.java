package org.example.MeasurementFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.example.GlobalVars;
import org.example.MeasurementFactory.BenchmarkDotNetJSON.BenchmarkDotNetJSONParser;
import org.example.MeasurementFactory.JMHJSON.JMHJSONParser;
/**
 * Represents place where methods for constructing Parsers are stored
 */
public class ParserIndustry {
    // construct parser according to file structure

    /**
     * Method that recognizes Benchmark framework and construct Parser for that file
     *
     * @param fileName path to the file with benchmark test results
     * @return specialized Parser for that file
     */
    public static IMeasurementParser RecognizeParserFactory(String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String oneLine = String.join("", lines);
        if (oneLine.contains("\"BenchmarkDotNetVersion\"") && oneLine.contains("\"BenchmarkDotNetCaption\""))
            return BenchmarkDotNetJSONParserFactory();
        if (oneLine.contains("\"jmhVersion\"") && oneLine.contains("\"benchmark\""))
            return JMHJSONParserFactory();
        return null;
    }

    /**
     * @return IMeasurementParser specialized for BenchmarkDotNet framework with result in JSON format
     */
    static IMeasurementParser BenchmarkDotNetJSONParserFactory() {
        return new BenchmarkDotNetJSONParser();
    }

    /**
     * @return IMeasurementParser specialized for JMH framework with result in JSON format
     */
    static IMeasurementParser JMHJSONParserFactory() {
        return new JMHJSONParser();
    }
}
