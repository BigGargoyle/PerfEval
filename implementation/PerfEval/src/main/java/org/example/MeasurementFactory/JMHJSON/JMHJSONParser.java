package org.example.MeasurementFactory.JMHJSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.MeasurementFactory.JMHJSON.pojoJMH.BenchmarkJMHJSONBase;
import org.example.MeasurementFactory.IMeasurementParser;
import org.example.MeasurementFactory.IMeasurement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IMeasurementParser for JMH framework test result in the JSON format
 */
public class JMHJSONParser implements IMeasurementParser {
    Long timestamp = null;
    public List<IMeasurement> getTestsFromFile(String fileName) {
        List<IMeasurement> result = new ArrayList<>();
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONBase[] base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkJMHJSONBase[].class);
        }catch (Exception e){
            return null;
        }

        try {
            for (BenchmarkJMHJSONBase benchmark : base) {
                result.add(JMHMeasurement.ConstructTest(benchmark));
            }
        }
        catch (IOException e){
            return null;
        }

        timestamp = inputFile.lastModified();

        return result;
    }
    public String GetParserType(){
        return "framework: JMH, format: JSON ";
    }
}
