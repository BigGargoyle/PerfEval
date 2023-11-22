package cz.cuni.mff.d3s.perfeval.evaluation;

import cz.cuni.mff.d3s.perfeval.performancecomparators.ComparisonResult;

import java.io.PrintStream;

/**
 * Printer for list-undecided command
 */
public class UndecidedPrinter implements ResultPrinter {
    /**
     * PrintStream where the results will be printed
     */
    final PrintStream printStream;
    /**
     * Delimiter for printing results
     */
    static final String COLUMN_DELIMITER = "\t";

    /**
     * Constructor for UndecidedPrinter
     *
     * @param printStream PrintStream where the results will be printed
     */
    public UndecidedPrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * Prints content of MeasurementComparisonResultCollection object
     * in format: name of test \t minimal sample count
     *
     * @param resultCollection collection of results to be printed
     * @see MeasurementComparisonResultCollection
     */
    @Override
    public void PrintResults(MeasurementComparisonResultCollection resultCollection) {
        for (MeasurementComparisonRecord comparisonResult : resultCollection) {
            if (comparisonResult.comparisonResult() == ComparisonResult.NotEnoughSamples) {
                printUndecidedComparisonResult(comparisonResult, printStream);
            }
        }
    }

    /**
     * Method printing name and minimal sample counts for a one undecided test
     *
     * @param comparisonResult an instance of undecided test comparison result (IMeasurementComparisonResult)
     * @param printStream      PrintStream to print undecided result to
     */
    private static void printUndecidedComparisonResult(MeasurementComparisonRecord comparisonResult, PrintStream printStream) {
        String name = comparisonResult.oldSamples().getName();
        int minSampleCount = comparisonResult.minSampleCount();
        printStream.println(name + COLUMN_DELIMITER + minSampleCount);
    }
}
