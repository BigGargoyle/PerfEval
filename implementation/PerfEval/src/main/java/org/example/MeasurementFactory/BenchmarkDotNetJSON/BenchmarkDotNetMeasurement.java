package org.example.MeasurementFactory.BenchmarkDotNetJSON;
import org.example.MeasurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.Benchmark;
import org.example.MeasurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.Measurement;

import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of IMeasurement interface for JMH benchmark test results
 */
public class BenchmarkDotNetMeasurement implements IMeasurement {
    /*
    * testedIterationMode and testedIterationStage Strings are used for recognizing correct type of measured values
    * it is needed because the BenchmarkDotNet framework is measuring also warmup, jitting, overhead etc. phases
    * of the software running
    * */
    static final String testedIterationMode = "Workload";
    static final String testedIterationStage = "Actual";
    final String Name;
    final List<UniversalTimeUnit> MeasuredTimes;
    public static IMeasurement ConstructTest(Benchmark pattern){
        return new BenchmarkDotNetMeasurement(pattern);
    }
    /*
    * This constructor is creating an instance of BenchmarkDotNetTest class that is based on values from
    * an instance of BenchmarkDotNetJSONBase class. This class is made from a JSON file that is a result
    * of BenchmarkDotNet run.
    * This class main usage is for simplifying and clarifying data package that is contained inside the
    * BenchmarkDotNetJSONBase class.
    * */
    private BenchmarkDotNetMeasurement(Benchmark pattern){
        Name = pattern.getMethodTitle();
        List<Measurement> measurements = pattern.getMeasurements();
        MeasuredTimes = new ArrayList<>();
        for(Measurement measurement:measurements){
            // I want to test only measurements with 'Workload' mode and 'Actual' stage
            if(measurement.getIterationMode().equals(testedIterationMode) &&
                    measurement.getIterationStage().equals(testedIterationStage)){
                MeasuredTimes.add(new UniversalTimeUnit(measurement.getNanoseconds(), TimeUnit.NANOSECONDS));
            }
        }
    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public List<UniversalTimeUnit> getMeasuredTimes() {
        return MeasuredTimes;
    }
}
