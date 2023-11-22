package cz.cuni.mff.d3s.perfeval.evaluation;

import dnl.utils.text.table.TextTable;

import java.io.PrintStream;
import java.util.Comparator;

/**
 * Printer for results in table format
 */
public class TablePrinter implements ResultPrinter {
    /**
     * PrintStream where the results will be printed
     */
    final PrintStream printStream;
    /**
     * Comparator for sorting results
     */
    final Comparator<MeasurementComparisonRecord> filter;

    /**
     * Constructor for TablePrinter
     *
     * @param printStream PrintStream where the results will be printed
     * @param filter      Comparator for sorting results
     */
    public TablePrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
    }

    /**
     * Prints MeasurementComparisonResultCollection object in table format
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
    private static String[] measurementComparisonToTableRow(MeasurementComparisonRecord comparisonResult) {
        String[] tableRow = new String[8];

        tableRow[0] = (comparisonResult.oldSamples().getName());
        tableRow[1] = (String.valueOf(comparisonResult.newAverage()));
        tableRow[2] = (String.valueOf(comparisonResult.oldAverage()));
        tableRow[3] = (String.valueOf(comparisonResult.performanceChange()));
        if (comparisonResult.testVerdict())
            tableRow[4] = ("OK");
        else
            tableRow[4] = ("NOT OK");
        switch (comparisonResult.comparisonResult()) {

            case SameDistribution -> tableRow[5] = ("same distribution");
            case DifferentDistribution -> tableRow[5] = ("different distribution");
            case NotEnoughSamples -> tableRow[5] = ("not enough samples (" + comparisonResult.minSampleCount()
                    + " samples needed)");
            case Bootstrap -> tableRow[5] = ("note enough samples (bootstrap was made)");
            case None -> tableRow[5] = ("NONE???");
        }

        return tableRow;
    }
}
