package cz.cuni.mff.d3s.perfeval.printers;

import dnl.utils.text.table.TextTable;

import java.io.PrintStream;
import java.util.Comparator;

/**
 * Printer for results in table format.
 */
public class TablePrinter implements ResultPrinter {

    /**
     * Maximum length of the name of the sample.
     * Empirical value.
     */
    private static final int MAX_NAME_LENGTH = 40;

    /**
     * Number of columns in the table.
     */
    private static final int TABLE_COLUM_COUNT = 8;
    /**
     * Index of column with name of the sample.
     */
    private static final int NAME_COLUMN_INDEX = 0;
    /**
     * Index of column with new average.
     */
    private static final int NEW_AVERAGE_COLUMN_INDEX = 1;
    /**
     * Index of column with old average.
     */
    private static final int OLD_AVERAGE_COLUMN_INDEX = 2;
    /**
     * Index of column with change in performance.
     */
    private static final int CHANGE_COLUMN_INDEX = 3;
    /**
     * Index of column with lower bound of confidence interval.
     */
    private static final int LOWER_CI_BOUND_COLUMN_INDEX = 4;

    /**
     * Index of column with upper bound of confidence interval.
     */
    private static final int UPPER_CI_BOUND_COLUMN_INDEX = 5;
    /**
     * Index of column with result of comparison.
     */
    private static final int RESULT_COLUMN_INDEX = 6;

    /**
     * Index of column with verdict of comparison.
     */
    private static final int VERDICT_COLUMN_INDEX = 7;

    /**
     * PrintStream where the results will be printed.
     */
    private final PrintStream printStream;
    /**
     * Comparator for sorting results.
     */
    private final Comparator<MeasurementComparisonRecord> filter;

    /**
     * Constructor for TablePrinter.
     *
     * @param printStream PrintStream where the results will be printed
     * @param filter      Comparator for sorting results
     */
    public TablePrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
    }

    /**
     * Prints MeasurementComparisonResultCollection object in table format.
     *
     * @param resultCollection collection of results to be printed
     * @see MeasurementComparisonResultCollection
     */
    @Override
    public void PrintResults(MeasurementComparisonResultCollection resultCollection) {
        resultCollection.sort(filter);
        String[] tableHeader = createComparisonTableHeader();
        String[][] tableData = new String[resultCollection.size()][];
        for (int i = 0; i < resultCollection.size(); i++) {
            tableData[i] = measurementComparisonToTableRow(resultCollection.get(i));
        }

        printStream.println("old version: " + resultCollection.getOldVersion());
        printStream.println("new version: " + resultCollection.getNewVersion());

        TextTable table = new TextTable(tableHeader, tableData);
        table.printTable(printStream, 0);
    }


    /**
     * Creates comparison table header that should be printed before the whole table.
     *
     * @return header row of the table of results
     */
    private static String[] createComparisonTableHeader() {
        String[] tableRow = new String[TABLE_COLUM_COUNT];
        tableRow[NAME_COLUMN_INDEX] = ("Name");
        tableRow[NEW_AVERAGE_COLUMN_INDEX] = ("NewAverage");
        tableRow[OLD_AVERAGE_COLUMN_INDEX] = ("OldAverage");
        tableRow[CHANGE_COLUMN_INDEX] = ("Change [%]");
        tableRow[VERDICT_COLUMN_INDEX] = ("Comparison verdict");
        tableRow[RESULT_COLUMN_INDEX] = ("Comparison result");
        tableRow[LOWER_CI_BOUND_COLUMN_INDEX] = ("Lower CI bound");
        tableRow[UPPER_CI_BOUND_COLUMN_INDEX] = ("Upper CI bound");
        return tableRow;
    }

    /**
     * Creates table row from an instance of IMeasurementComparisonResult.
     *
     * @param comparisonResult result of comparison
     * @return table row with info about comparisonResult
     */
    private static String[] measurementComparisonToTableRow(MeasurementComparisonRecord comparisonResult) {
        String[] tableRow = new String[TABLE_COLUM_COUNT];

        tableRow[NAME_COLUMN_INDEX] = truncateName(comparisonResult.oldSamples().getName());
        tableRow[NEW_AVERAGE_COLUMN_INDEX] = comparisonResult.newAverageToString();
        tableRow[OLD_AVERAGE_COLUMN_INDEX] = comparisonResult.oldAverageToString();
        tableRow[CHANGE_COLUMN_INDEX] = String.valueOf(comparisonResult.changeToString());
        tableRow[VERDICT_COLUMN_INDEX] = comparisonResult.testVerdict() ? ("OK") : ("NOT OK");
        switch (comparisonResult.comparisonResult()) {
            case SameDistribution -> tableRow[RESULT_COLUMN_INDEX] = ("same distribution");
            case DifferentDistribution -> tableRow[RESULT_COLUMN_INDEX] = ("different distribution");
            case NotEnoughSamples -> tableRow[RESULT_COLUMN_INDEX] =
                    ("not enough samples (" + comparisonResult.minSampleCount() + " samples needed)");
            case UnableToMeasureEnoughSamples ->
                    tableRow[RESULT_COLUMN_INDEX] = ("impossible to measure (" + comparisonResult.minSampleCount() + " samples needed)");
            case OnlyOlderSamples -> tableRow[RESULT_COLUMN_INDEX] = ("only older samples");
            case OnlyNewerSamples -> tableRow[RESULT_COLUMN_INDEX] = ("only newer samples");
            default -> tableRow[RESULT_COLUMN_INDEX] = ("NONE???");
        }

        tableRow[LOWER_CI_BOUND_COLUMN_INDEX] = comparisonResult.lowerCIBoundToString();
        tableRow[UPPER_CI_BOUND_COLUMN_INDEX] = comparisonResult.upperCIBoundToString();

        return tableRow;
    }

    private static String truncateName(String name) {
        if (name.length() <= MAX_NAME_LENGTH) return name;
        StringBuilder result = new StringBuilder();
        String[] parts = name.split("\\.");
        int fullLength = name.length();
        int index = 0;
        while (fullLength > MAX_NAME_LENGTH) {
            if (index == parts.length - 1) break;
            fullLength -= parts[index].length() - 1;
            if (!result.isEmpty())
                result.deleteCharAt(result.length() - 1); // remove last dot (if it is there)
            result.append(Character.valueOf(parts[index].charAt(0)).toString()).append(".");
            index++;
        }
        while (index < parts.length) {
            if (parts[index].length() + result.length() > MAX_NAME_LENGTH) {
                result.append(truncateFromMiddle(parts[index], MAX_NAME_LENGTH - result.length() - 1));
            }
            else {
                result.append(parts[index]);
            }
            if (index != parts.length - 1)
                result.append(".");
            index++;
        }

        result = new StringBuilder(result.substring(0, Math.min(result.length(), MAX_NAME_LENGTH)));

        return result.toString();
    }

    private static String truncateFromMiddle(String part, int length) {
        int half = length / 2;
        return part.substring(0, half) + "~" + part.substring(part.length() - half);
    }

}
