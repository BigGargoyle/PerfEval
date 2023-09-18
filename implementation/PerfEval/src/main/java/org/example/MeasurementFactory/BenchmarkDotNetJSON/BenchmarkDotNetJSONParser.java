package org.example.MeasurementFactory.BenchmarkDotNetJSON;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.IMeasurementParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.MeasurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.*;
import org.example.MeasurementFactory.UniversalTimeUnit;

/**
 * Implementation of IMeasurementParser for BenchmarkDotNet framework test results in the JSON format.
 */
public class BenchmarkDotNetJSONParser implements IMeasurementParser {
    Long timestamp = null;
    public List<IMeasurement> getTestsFromFile(String fileName){
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
            result.add(ConstructTest(benchmark));
        }

        timestamp = inputFile.lastModified();

        return result;
    }
    public String GetParserType(){
        return "framework: BenchmarkDotNet, format: JSON ";
    }

    /*
     * testedIterationMode and testedIterationStage Strings are used for recognizing correct type of measured values
     * it is needed because the BenchmarkDotNet framework is measuring also warmup, jitting, overhead etc. phases
     * of the software running
     * */
    static final String testedIterationMode = "Workload";
    static final String testedIterationStage = "Actual";

    /*
     * This constructor is creating an instance of BenchmarkDotNetTest class that is based on values from
     * an instance of BenchmarkDotNetJSONBase class. This class is made from a JSON file that is a result
     * of BenchmarkDotNet run.
     * This class main usage is for simplifying and clarifying data package that is contained inside the
     * BenchmarkDotNetJSONBase class.
     * */
    public static IMeasurement ConstructTest(Benchmark pattern){
        var name = pattern.getMethodTitle();
        List<Measurement> measurements = pattern.getMeasurements();
        List<UniversalTimeUnit> measuredTimes = new ArrayList<>();
        for(Measurement measurement:measurements){
            // I want to test only measurements with 'Workload' mode and 'Actual' stage
            if(measurement.getIterationMode().equals(testedIterationMode) &&
                    measurement.getIterationStage().equals(testedIterationStage)){
                measuredTimes.add(new UniversalTimeUnit(measurement.getNanoseconds(), TimeUnit.NANOSECONDS));
            }
        }
        return new IMeasurement(name, measuredTimes);
    }

}
