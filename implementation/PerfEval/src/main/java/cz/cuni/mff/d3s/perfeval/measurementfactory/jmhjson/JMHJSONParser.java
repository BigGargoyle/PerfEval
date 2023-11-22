package cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.pojoJMH.BenchmarkJMHJSONRoot;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of MeasurementParser for JMH framework test result in the JSON format
 * @see MeasurementParser
 */

public class JMHJSONParser implements MeasurementParser {

    /**
     * Constructor for JMHJSONParser
     */
    public JMHJSONParser() {
    }

    /**
     * Possible units of metric when measuring time
     */
    static final String[] timeScoreUnits = new String[]{"ns/op", "us/op", "ms/op", "s/op"};
    /**
     * Possible units of metric when measuring frequency (throughput)
     */
    static final String[] frequencyScoreUnits = new String[]{"ops/s", "ops/ms", "ops/us", "ops/ns"};

    @Override
    public List<Samples> getTestsFromFiles(String[] fileNames) {
        Map<String, Samples> samplesPerTestName = new HashMap<>();
        //TODO: zbavit se for cyklů -> práce se streamem
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            var samplesMetadata = getSamplesMetadataFromFile(fileName);
            // finalI because of lambda and compiler
            int finalI = i;
            samplesMetadata.forEach(sampleMetadata -> {
                if (samplesPerTestName.get(sampleMetadata.name) == null) {
                    Samples samples = new Samples(new double[fileNames.length][], sampleMetadata.metric, sampleMetadata.name);
                    samplesPerTestName.put(sampleMetadata.name, samples);
                    for (int j = 0; j < fileNames.length; j++) {
                        samples.getRawData()[j] = new double[0];
                    }
                }
                if (sampleMetadata.metric.isCompatibleWith(samplesPerTestName.get(sampleMetadata.name).getMetric()))
                    samplesPerTestName.get(sampleMetadata.name).getRawData()[finalI] = sampleMetadata.rawData;
                // else nothing added
            });
        }
        return new ArrayList<>(samplesPerTestName.values());
    }

    /**
     * Class for storing metadata of one sample
     */
    // duplicate of BenchmarkDotNetJSONParser.SampleMetadata, but it is not possible to use it because of different benchmark framework
    static class SampleMetadata {
        public String name;
        public double[] rawData;
        Metric metric;
    }

    /**
     * Gets metadata of samples from one file
     *
     * @param fileName name of file with results of performance tests
     * @return stream of SampleMetadata objects
     */
    static Stream<SampleMetadata> getSamplesMetadataFromFile(String fileName) {
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONRoot[] roots;
        try {
            roots = objectMapper.readValue(inputFile, BenchmarkJMHJSONRoot[].class);
        } catch (Exception e) {
            return null;
        }
        assert roots != null;
        Stream.Builder<SampleMetadata> streamBuilder = Stream.builder();

        for (BenchmarkJMHJSONRoot benchmark : roots) {
            var name = benchmark.getBenchmark();
            var primaryMetric = benchmark.getPrimaryMetric();
            Metric metric = null;
            for (String scoreUnit : frequencyScoreUnits) {
                if (primaryMetric.getScoreUnit().compareTo(scoreUnit) == 0) {
                    metric = new Metric(scoreUnit, true);
                    break;
                }
            }
            if (metric == null)
                metric = new Metric(primaryMetric.getScoreUnit(), false);
            SampleMetadata sampleMetadata = new SampleMetadata();
            sampleMetadata.name = name;
            sampleMetadata.metric = metric;
            List<List<Double>> measuredValues = benchmark.getPrimaryMetric().getRawData();
            List<Double> measuredValues1D = new ArrayList<>();
            for (List<Double> measuredValue : measuredValues) {
                measuredValues1D.addAll(measuredValue);
            }
            sampleMetadata.rawData = measuredValues1D.stream().mapToDouble(Double::valueOf).toArray();
            streamBuilder.accept(sampleMetadata);
        }
        return streamBuilder.build();
    }

    @Override
    public String getParserName() {
        return "JMHJSONParser";
    }
}
