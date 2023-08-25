package org.example.Evaluation;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import dnl.utils.text.table.TextTable;
import org.codehaus.jackson.map.ObjectMapper;

public class ResultPrinter {

    public static void TablePrinter(List<IMeasurementComparisonResult> measurementComparisonResults, PrintStream printStream){
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
        tableRow[4]=("Comparison verdict");
        tableRow[5]=("Comparison result");

        return tableRow;

    }
    private static String[] MeasurementComparisonToTableRow(IMeasurementComparisonResult comparisonResult){
        String[] tableRow = new String[6];

        tableRow[0]=(comparisonResult.getName());
        tableRow[1]=(String.valueOf(comparisonResult.getNewAvg()));
        tableRow[2]=(String.valueOf(comparisonResult.getOldAvg()));
        tableRow[3]=(String.valueOf(comparisonResult.getChange()));
        if(comparisonResult.getComparisonVerdict())
            tableRow[4]=("OK");
        else
            tableRow[4]=("NOT OK");
        switch (comparisonResult.getComparisonResult()){

            case SameDistribution -> tableRow[5]=("same distribution");
            case DifferentDistribution -> tableRow[5]=("different distribution");
            case NotEnoughSamples -> tableRow[5]=("not enough samples (" + comparisonResult.getMinSampleCount()
                    + " samples needed)");
            case Bootstrap -> tableRow[5]=("note enough samples (bootstrap was made)");
            case None -> tableRow[5]=("NONE???");
        }

        return tableRow;
    }


    public static void JSONPrinter(List<IMeasurementComparisonResult> measurementComparisonResults, PrintStream printStream) {
        // TODO: serializable MeasurementComparisonResult image
        var objectMapper = new ObjectMapper();
        for (IMeasurementComparisonResult comparisonResult: measurementComparisonResults) {
            var measurementComparisonResultView = ConvertIMeasurementComparisonResult(comparisonResult);
            try {
                String jsonView = objectMapper.writeValueAsString(measurementComparisonResultView);
            }
            catch (IOException e) {
                // TODO: exception handle
            }

        }
    }

    public static MeasurementComparisonResultView ConvertIMeasurementComparisonResult(IMeasurementComparisonResult comparisonResult){
        return new MeasurementComparisonResultView(comparisonResult);
    }


}
