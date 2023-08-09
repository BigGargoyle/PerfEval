package org.example.MeasurementFactory.JMHJSON;
import org.example.MeasurementFactory.JMHJSON.pojoJMH.BenchmarkJMHJSONBase;
import org.example.MeasurementFactory.JMHJSON.pojoJMH.PrimaryMetric;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of IMeasurement interface for JMH benchmark test results
 */
public class JMHMeasurement implements IMeasurement {

    static String[] acceptableScoreUnits = new String[] {"ns/op","us/op","ms/op","s/op"};

    String Name;
    List<UniversalTimeUnit> MeasuredTimes;
    public static IMeasurement ConstructTest(BenchmarkJMHJSONBase input) throws IOException {
        return new JMHMeasurement(input);
    }
    private JMHMeasurement(BenchmarkJMHJSONBase input) throws IOException {
        Name = input.getBenchmark();
        MeasuredTimes = new ArrayList<UniversalTimeUnit>();
        PrimaryMetric primaryMetric = input.getPrimaryMetric();
        // !!! adding average of each measuredData, because rawData is a field of list of lists of Doubles
        for (List<Double> measuredData: primaryMetric.getRawData()){
            String scoreUnit = primaryMetric.getScoreUnit();
            if(!Arrays.asList(acceptableScoreUnits).contains(scoreUnit)){
                throw new IOException("Unexpected scoreUnit type. Acceptable types are: ns/op, us/op, ms/op, s/op");
            }

            switch (scoreUnit){
                case "ns/op"-> MeasuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.NANOSECONDS));
                case "us/op"-> MeasuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.MICROSECONDS));
                case "ms/op"-> MeasuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.MILLISECONDS));
                case "s/op"-> MeasuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.SECONDS));
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

    @Override
    public boolean hasAscendingPerformanceUnit() {
        // by default ops/us -> more ops/us -> better performance
        return true;
    }
}
