package org.example.evaluation;

import dnl.utils.text.table.TextTable;
import org.example.resultDatabase.FileWithResultsData;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;

public class TablePrinter implements ResultPrinter {
    final PrintStream printStream;
    final Comparator<MeasurementComparisonRecord> filter;
    public TablePrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
    }

    /**
     * Method to print comparisonResults in form of simple table
     *
     * @param results       list of IComparisonResults to be printed
     * @param originalFiles items from which comparison results were made of
     */
    @Override
    public void PrintResults(List<MeasurementComparisonRecord> results, FileWithResultsData[] originalFiles) {
        results.sort(filter);
        String[] tableHeader = createComparisonTableHeader();
        String[][] tableData = new String[results.size()][];
        for (int i = 0; i < results.size(); i++) {
            tableData[i] = measurementComparisonToTableRow(results.get(i));
        }

        printStream.println("old version: " + originalFiles[0].version());
        printStream.println("new version: " + originalFiles[1].version());

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

        tableRow[0] = (comparisonResult.oldSamples().name());
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
