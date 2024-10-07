package cz.cuni.mff.d3s.perfeval.printers;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;

/**
 * Printer for results in JSON format.
 */
public class JSONPrinter implements ResultPrinter {
    /**
     * PrintStream where the results will be printed.
     */
    final PrintStream printStream;
    /**
     * Comparator for sorting results.
     */
    final Comparator<MeasurementComparisonRecord> filter;

    /**
     * Constructor for JSONPrinter.
     *
     * @param printStream PrintStream where the results will be printed
     * @param filter      Comparator for sorting results
     */
    public JSONPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
    }

    /**
     * Prints MeasurementComparisonResultCollection object in JSON format.
     *
     * @param resultCollection collection of results to be printed
     * @see MeasurementComparisonResultCollection
     */
    @Override
    public void PrintResults(MeasurementComparisonResultCollection resultCollection) {
        resultCollection.sort(filter);
        var objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(resultCollection);
            printStream.println(json);
        } catch (IOException e) {
            printStream.println("Cannot be formatted");
        }
    }
}
