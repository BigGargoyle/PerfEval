package cz.cuni.mff.d3s.perfeval.evaluation;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;

public class JSONPrinter implements ResultPrinter {
    PrintStream printStream;
    final Comparator<MeasurementComparisonRecord> filter;
    public JSONPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter){
        this.printStream = printStream;
        this.filter = filter;
    }

    @Override
    public void PrintResults(MeasurementComparisonResultCollection resultCollection) {
        resultCollection.sort(filter);
        var objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(resultCollection);
            printStream.println(json);
        }catch (IOException e) {
            printStream.println("Cannot be formatted");
        }
    }
}
