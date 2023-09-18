package org.example.measurementFactory.JMHJSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.measurementFactory.JMHJSON.pojoJMH.BenchmarkJMHJSONBase;
import org.example.measurementFactory.IMeasurementParser;
import org.example.measurementFactory.Measurement;
import org.example.measurementFactory.JMHJSON.pojoJMH.PrimaryMetric;
import org.example.measurementFactory.UniversalTimeUnit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of IMeasurementParser for JMH framework test result in the JSON format
 */
public class JMHJSONParser implements IMeasurementParser {

    public List<Measurement> getTestsFromFile(String fileName) {
        List<Measurement> result = new ArrayList<>();
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
                result.add(ConstructTest(benchmark));
            }
        }
        catch (IOException e){
            return null;
        }

        return result;
    }

    static final String[] acceptableScoreUnits = new String[] {"ns/op","us/op","ms/op","s/op"};

    public static Measurement ConstructTest(BenchmarkJMHJSONBase input) throws IOException {
        String name = input.getBenchmark();
        List<UniversalTimeUnit> measuredTimes = new ArrayList<>();
        PrimaryMetric primaryMetric = input.getPrimaryMetric();
        // !!! adding average of each measuredData, because rawData is a field of list of lists of Doubles
        for (List<Double> measuredData: primaryMetric.getRawData()){
            String scoreUnit = primaryMetric.getScoreUnit();
            if(!Arrays.asList(acceptableScoreUnits).contains(scoreUnit)){
                throw new IOException("Unexpected scoreUnit type. Acceptable types are: ns/op, us/op, ms/op, s/op");
            }

            switch (scoreUnit){
                case "ns/op"-> measuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.NANOSECONDS));
                case "us/op"-> measuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.MICROSECONDS));
                case "ms/op"-> measuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.MILLISECONDS));
                case "s/op"-> measuredTimes.add(new UniversalTimeUnit(Math.round(measuredData.stream().mapToDouble(Double::doubleValue)
                        .average().orElse(0.0)), TimeUnit.SECONDS));
            }
        }
        return new Measurement(name, measuredTimes);
    }

}
