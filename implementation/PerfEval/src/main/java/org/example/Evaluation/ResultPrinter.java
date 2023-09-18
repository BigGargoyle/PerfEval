package org.example.Evaluation;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import dnl.utils.text.table.TextTable;
import org.codehaus.jackson.map.ObjectMapper;
import org.example.GlobalVars;
import org.example.PerformanceComparatorFactory.ComparisonResult;

public class ResultPrinter {
    /**
     * Method to print comparisonResults in form of simple table
     *
     * @param measurementComparisonResults list of IComparisonResults to be printed
     * @param printStream                  PrintStream to print result to
     */
    public static void tablePrinter(List<IMeasurementComparisonResult> measurementComparisonResults, PrintStream printStream) {
        String[] tableHeader = createComparisonTableHeader();
        String[][] tableData = new String[measurementComparisonResults.size()][];
        for (int i = 0; i < measurementComparisonResults.size(); i++) {
            tableData[i] = measurementComparisonToTableRow(measurementComparisonResults.get(i));
        }

        TextTable table = new TextTable(tableHeader, tableData);
        table.printTable(printStream, 0);
    }

    /**
     * Creates comparison table header that should be printed before the whole table
     *
     * @return header row of the table of results
     */
    private static String[] createComparisonTableHeader() {
        String[] tableRow = new String[6];
        tableRow[0] = ("Name");
        tableRow[1] = ("NewAverage");
        tableRow[2] = ("OldAverage");
        tableRow[3] = ("Change [%]");
        tableRow[4] = ("Comparison verdict");
        tableRow[5] = ("Comparison result");

        return tableRow;
    }

    /**
     * Creates table row from an instance of IMeasurementComparisonResult
     *
     * @param comparisonResult result of comparison
     * @return table row with info about comparisonResult
     */
    private static String[] measurementComparisonToTableRow(IMeasurementComparisonResult comparisonResult) {
        String[] tableRow = new String[8];

        tableRow[0] = (comparisonResult.getName());
        tableRow[1] = (String.valueOf(comparisonResult.getNewAvg()));
        tableRow[2] = (String.valueOf(comparisonResult.getOldAvg()));
        tableRow[3] = (String.valueOf(comparisonResult.getChange()));
        if (comparisonResult.getComparisonVerdict())
            tableRow[4] = ("OK");
        else
            tableRow[4] = ("NOT OK");
        switch (comparisonResult.getComparisonResult()) {

            case SameDistribution -> tableRow[5] = ("same distribution");
            case DifferentDistribution -> tableRow[5] = ("different distribution");
            case NotEnoughSamples -> tableRow[5] = ("not enough samples (" + comparisonResult.getMinSampleCount()
                    + " samples needed)");
            case Bootstrap -> tableRow[5] = ("note enough samples (bootstrap was made)");
            case None -> tableRow[5] = ("NONE???");
        }

        return tableRow;
    }

    /**
     * Method to print comparisonResults in JSON list format
     *
     * @param measurementComparisonResults list of IComparisonResults to be printed
     * @param printStream                  PrintStream to print result to
     */
    public static void JSONPrinter(List<IMeasurementComparisonResult> measurementComparisonResults, PrintStream printStream) {
        List<MeasurementComparisonResultView> measurementComparisonResultViews = new ArrayList<>();
        for (IMeasurementComparisonResult comparisonResult : measurementComparisonResults) {
            var measurementComparisonResultView = convertIMeasurementComparisonResult(comparisonResult);
            measurementComparisonResultViews.add(measurementComparisonResultView);
        }
        var objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(measurementComparisonResultViews);
            printStream.println(json);
        }catch (IOException e){
            printStream.println("Cannot be formatted");
        }
    }

    /**
     * Creates an instance of MeasurementComparisonResultView from IMeasurementComparisonResult
     *
     * @param comparisonResult An instance of IMeasurementComparisonResult for which is an instance of MeasurementComparisonResultView constructed
     * @return an instance of MeasurementComparisonResultView
     */
    public static MeasurementComparisonResultView convertIMeasurementComparisonResult(IMeasurementComparisonResult comparisonResult) {
        return new MeasurementComparisonResultView(comparisonResult);
    }

    /**
     * Method printing names and minimal sample counts for undecided tests
     *
     * @param measurementComparisonResults list from which are printed names and minimum sample counts of undecided tests
     * @param printStream                  PrintStream to print undecided results to
     */
    public static void printUndecided(List<IMeasurementComparisonResult> measurementComparisonResults, PrintStream printStream) {
        for (IMeasurementComparisonResult comparisonResult : measurementComparisonResults) {
            if (comparisonResult.getComparisonResult() == ComparisonResult.NotEnoughSamples) {
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
    private static void printUndecidedComparisonResult(IMeasurementComparisonResult comparisonResult, PrintStream printStream) {
        String name = comparisonResult.getName();
        int minSampleCount = comparisonResult.getMinSampleCount();
        printStream.println(name + GlobalVars.ColumnDelimiter + minSampleCount);
    }

}