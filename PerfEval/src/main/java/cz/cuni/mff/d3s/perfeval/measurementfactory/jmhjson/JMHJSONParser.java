package cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParserException;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.pojoJMH.BenchmarkJMHJSONRoot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Implementation of MeasurementParser for JMH framework test result in the JSON format.
 * @see MeasurementParser
 */

public class JMHJSONParser implements MeasurementParser {

    /**
     * Constructor for JMHJSONParser.
     */
    public JMHJSONParser() {
    }

    /**
     * Possible units of metric when measuring time.
     * Field just for information purposes.
     */
    static final String[] timeScoreUnits = new String[]{"ns/op", "us/op", "ms/op", "s/op"};
    /**
     * Possible units of metric when measuring frequency (throughput).
     */
    static final String[] frequencyScoreUnits = new String[]{"ops/s", "ops/ms", "ops/us", "ops/ns"};

    /**
     * Parses files with results of performance tests.
     * @param fileNames names of files with results of performance tests
     * @return list of Samples objects
     */
    @Override
    public List<Samples> getTestsFromFiles(String[] fileNames) {
        Map<String, Samples> samplesPerTestName = getSamplesFromFiles(Arrays.stream(fileNames));
        return new ArrayList<>(samplesPerTestName.values());
    }

    /**
     * Parses files with results of performance tests.
     * @param fileNames names of files with results of performance tests
     * @return map of Samples objects with test name as key
     */
    Map<String, Samples> getSamplesFromFiles(Stream<String> fileNames) {
        Map<String, Samples> samplesPerTestName = new HashMap<>();
        //file names to files
        fileNames.map(this::mapFileFromString)
                //files to POJOs JMH JSON root objects
                .flatMap(this::mapRootFromJSON)
                //POJOs to Samples
                .forEach(sample -> {
                    //find out metric of sample
                    Metric metric = new Metric(sample.getPrimaryMetric().getScoreUnit(),
                            Arrays.asList(frequencyScoreUnits).contains(sample.getPrimaryMetric().getScoreUnit()));
                    //add sample to map if not present yet
                    samplesPerTestName.computeIfAbsent(sample.getBenchmark(), k -> new Samples(metric, sample.getBenchmark()));
                    //TODO: řešit kompatibilitu metrik
                    //add raw data to sample
                    if(sample.getPrimaryMetric().getRawData() != null)
                        for (List<Double> rawData : sample.getPrimaryMetric().getRawData()) {
                            samplesPerTestName.get(sample.getBenchmark())
                                    .addSample(rawData.stream().mapToDouble(Double::doubleValue).toArray());
                        }
                });
        return samplesPerTestName;
    }

    /**
     * Maps file name to File object.
     * @param fileName name of file
     * @return File object
     */
    private File mapFileFromString(String fileName) {
        //System.out.println(fileName);
        return new File(fileName);
    }

    /**
     * Maps file to JMH JSON root object.
     * @param file file to be mapped
     * @return JMH JSON root object
     */
    private Stream<BenchmarkJMHJSONRoot> mapRootFromJSON(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BenchmarkJMHJSONRoot[] root = mapper.readValue(file, BenchmarkJMHJSONRoot[].class);
            return Arrays.stream(root);
        } catch (Exception e) {
            //System.out.println(e.getClass());
            throw new MeasurementParserException("JMHJSONParserException, error while processing file: "
                    + file.getName(), e);
        }
    }

    /**
     * Getter for name of the parser.
     * @return name of the parser
     */
    public static String getParserName() {
        return "JMHJSONParser";
    }

    @Override
    public String toString() {
        return getParserName();
    }
}
