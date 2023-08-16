package org.example;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import dnl.utils.text.table.TextTable;

public class ResultPrinter {

    public static void TablePrinter(List<MeasurementComparisonResult> measurementComparisonResults, PrintStream printStream){
        String[] tableHeader = CreateComparisonTableHeader();
        String[][] tableData = new String[measurementComparisonResults.size()][];
        for(int i =0;i<measurementComparisonResults.size();i++){
            tableData[i] = MeasurementComparisonToTableRow(measurementComparisonResults.get(i));
        }

        TextTable table = new TextTable(tableHeader, tableData);
        table.printTable(printStream,0);
    }

    private static String[] CreateComparisonTableHeader(){
        String[] tableRow = new String[6];
        tableRow[0]=("Name");
        tableRow[1]=("NewAverage");
        tableRow[2]=("OldAverage");
        tableRow[3]=("Change [%]");
        tableRow[4]=("Comparison result");
        tableRow[5]=("Comparison verdict");

        return tableRow;

    }
    private static String[] MeasurementComparisonToTableRow(MeasurementComparisonResult comparisonResult){
        String[] tableRow = new String[6];

        tableRow[0]=(comparisonResult.getName());
        tableRow[1]=(String.valueOf(comparisonResult.getNewAvg()));
        tableRow[2]=(String.valueOf(comparisonResult.getOldAvg()));
        tableRow[3]=(String.valueOf(comparisonResult.getChange()));
        switch (comparisonResult.getComparisonResult()){

            case SameDistribution -> {
                tableRow[4]=("same distribution");
            }
            case DifferentDistribution -> {
                tableRow[4]=("different distribution");
            }
            case NotEnoughSamples -> {
                // TODO: tato třída nepotřebuje vědět, co je performance comparator
                tableRow[4]=("not enough samples (" + comparisonResult.getPerformanceComparator().GetMinSampleCount()
                        + " samples needed)");
            }
            case Bootstrap -> {
                tableRow[4]=("note enough samples (bootstrap was made)");
            }
            case None -> {
                tableRow[4]=("NONE???");
            }
        }
        if(comparisonResult.getComparisonVerdict())
            tableRow[5]=("OK");
        else
            tableRow[5]=("NOT OK");

        return tableRow;
    }


    public static void JSONPrinter(List<MeasurementComparisonResult> measurementComparisonResults, PrintStream printStream){
        // TODO: serializable MeasurementComparisonResult image
    }


}
