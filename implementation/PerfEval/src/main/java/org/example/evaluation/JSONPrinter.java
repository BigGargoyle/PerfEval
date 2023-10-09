package org.example.evaluation;

import org.codehaus.jackson.map.ObjectMapper;
import org.example.resultDatabase.FileWithResultsData;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;

public class JSONPrinter implements IResultPrinter{
    PrintStream printStream;
    final Comparator<MeasurementComparisonRecord> filter;
    public JSONPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter){
        this.printStream = printStream;
        this.filter = filter;
    }


    /**
     * Method to print comparisonResults in JSON list format
     *
     * @param results list of IComparisonResults to be printed
     * @param originalFiles               items from which comparison results were made of
     */
    @Override
    public void PrintResults(List<MeasurementComparisonRecord> results, FileWithResultsData[] originalFiles) {
        results.sort(filter);
        var objectMapper = new ObjectMapper();
        try {
            printStream.println("{");
            printStream.println("\"oldVersion\":\""+originalFiles[0].version()+"\",");
            printStream.println("\"newVersion\":\""+originalFiles[1].version()+"\",");
            printStream.println("\"testsResult\":");
            String json = objectMapper.writeValueAsString(results);
            printStream.println(json);
            printStream.println("}");
        }catch (IOException e){
            printStream.println("Cannot be formatted");
        }
    }
}
