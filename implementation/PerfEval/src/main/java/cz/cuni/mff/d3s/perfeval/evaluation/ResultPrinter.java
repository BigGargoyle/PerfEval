package cz.cuni.mff.d3s.perfeval.evaluation;

/**
 * Interface for printing results of comparison of two sets of samples
 */
public interface ResultPrinter {
    /**
     * Prints results of comparison of two sets of samples
     *
     * @param resultCollection collection of results to be printed
     */
    void PrintResults(MeasurementComparisonResultCollection resultCollection) throws MeasurementPrinterException;
}
