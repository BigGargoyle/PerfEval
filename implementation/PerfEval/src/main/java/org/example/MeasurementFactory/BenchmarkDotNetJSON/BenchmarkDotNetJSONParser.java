package org.example.MeasurementFactory.BenchmarkDotNetJSON;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.IMeasurementParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.MeasurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.*;

/**
 * Implementation of IMeasurementParser for BenchmarkDotNet framework test results in the JSON format.
 */
public class BenchmarkDotNetJSONParser implements IMeasurementParser {
    Long timestamp = null;
    public List<IMeasurement> GetTestsFromFile(String fileName){
        List<IMeasurement> result = new ArrayList<>();
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkDotNetJSONBase base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkDotNetJSONBase.class);
        }catch (Exception e){
            return null;
        }

        for(Benchmark benchmark: base.getBenchmarks()){
            result.add(BenchmarkDotNetMeasurement.ConstructTest(benchmark));
        }

        timestamp = inputFile.lastModified();

        return result;
    }
    public String GetParserType(){
        return "framework: BenchmarkDotNet, format: JSON ";
    }

}
