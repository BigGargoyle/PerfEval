package org.example.evaluation;

import org.example.performanceComparatorFactory.ComparisonResult;
import org.example.resultDatabase.FileWithResultsData;

import java.io.PrintStream;
import java.util.List;

public class UndecidedPrinter implements ResultPrinter {
    PrintStream printStream;
    static final String COLUMN_DELIMITER = "\t";
    public UndecidedPrinter(PrintStream printStream){
        this.printStream = printStream;
    }

    @Override
    public void PrintResults(List<MeasurementComparisonRecord> results, FileWithResultsData[] originalFiles) {
        for (MeasurementComparisonRecord comparisonResult : results) {
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
        String name = comparisonResult.oldSamples().name();
        int minSampleCount = comparisonResult.minSampleCount();
        printStream.println(name + COLUMN_DELIMITER + minSampleCount);
    }
}
