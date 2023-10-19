package cz.cuni.mff.hrdydo.measurementFactory.JMHJSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.hrdydo.measurementFactory.MeasurementParser;
import cz.cuni.mff.hrdydo.Metric;
import cz.cuni.mff.hrdydo.measurementFactory.JMHJSON.pojoJMH.BenchmarkJMHJSONBase;
import cz.cuni.mff.hrdydo.Samples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IMeasurementParser for JMH framework test result in the JSON format
 */

public class JMHJSONParser implements MeasurementParser {

    Metric metric;
    public JMHJSONParser(Metric metric){
        this.metric = metric;
    }

    public List<Samples> getTestsFromFile(String fileName) {
        List<Samples> result = new ArrayList<>();
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONBase[] base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkJMHJSONBase[].class);
        }catch (Exception e){
            return null;
        }

        /*try {
            for (BenchmarkJMHJSONBase benchmark : base) {
                result.add(ConstructTest(benchmark));
            }
        }
        catch (IOException e){
            return null;
        }*/

        return result;
    }

    static final String[] acceptableScoreUnits = new String[] {"ns/op","us/op","ms/op","s/op"};

    /*public static Samples ConstructTest(BenchmarkJMHJSONBase input) throws IOException {
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
        return new Samples(name, measuredTimes);
    }*/

    @Override
    public List<Samples> getTestsFromFiles(String[] fileNames) {
        return null;
    }
}
