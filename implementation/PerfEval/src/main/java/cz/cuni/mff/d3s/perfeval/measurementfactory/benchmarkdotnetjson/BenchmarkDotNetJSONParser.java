package cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson;
import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.Benchmark;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.BenchmarkDotNetJSONBase;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.Measurement;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of MeasurementParser for BenchmarkDotNet framework test results in the JSON format.
 * @see MeasurementParser
 */
public class BenchmarkDotNetJSONParser implements MeasurementParser {
    /**
     * Metric used for parsing
     */
    final Metric metric;

    /**
     * Constructor for BenchmarkDotNetJSONParser
     */
    public BenchmarkDotNetJSONParser() {
        this.metric = new Metric("Nanoseconds", false);
    }

    /**
     * Constructor for BenchmarkDotNetJSONParser
     *
     * @param metric metric used for parsing
     */
    public BenchmarkDotNetJSONParser(Metric metric) {
        this.metric = metric;
    }

    /**
     * Iteration mode that is tested
     */
    static final String testedIterationMode = "Workload";
    /**
     * Stage that is tested
     */
    static final String testedIterationStage = "Actual";

    /**
     * Parses files with results of performance tests
     *
     * @param fileNames names of files with results of performance tests
     * @return list of Samples objects
     */
    @Override
    public List<Samples> getTestsFromFiles(String[] fileNames) {
        Map<String, Samples> samplesToTestName = new HashMap<>();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            var samplesMetadata = getSamplesMetadataFromFile(fileName);
            // finalI because of lambda and compiler
            int finalI = i;
            samplesMetadata.forEach(sampleMetadata -> {
                if (samplesToTestName.get(sampleMetadata.name) == null) {
                    Samples samples = new Samples(new double[fileNames.length][], this.metric, sampleMetadata.name);
                    samplesToTestName.put(sampleMetadata.name, samples);
                    for (int j = 0; j < fileNames.length; j++) {
                        samples.getRawData()[j] = new double[0];
                    }
                }
                samplesToTestName.get(sampleMetadata.name).getRawData()[finalI] = sampleMetadata.rawData;
            });
        }
        return new ArrayList<>(samplesToTestName.values());
    }

    /**
     * Class for storing metadata of one sample
     */
    static class SampleMetadata {
        public String name;
        public double[] rawData;
    }

    /**
     * Gets metadata of samples from one file
     *
     * @param fileName name of file with results of performance tests
     * @return stream of SampleMetadata objects
     */
    static Stream<SampleMetadata> getSamplesMetadataFromFile(String fileName) {
        BenchmarkDotNetJSONBase base = getBaseFromPath(new File(fileName));
        Stream.Builder<SampleMetadata> streamBuilder = new Stream.Builder<>() {
            final ArrayList<SampleMetadata> samples = new ArrayList<>();

            @Override
            public void accept(SampleMetadata sampleMetadata) {
                samples.add(sampleMetadata);
            }

            @Override
            public Stream<SampleMetadata> build() {
                return samples.stream();
            }
        };
        assert base != null;
        for (Benchmark benchmark : base.getBenchmarks()) {
            SampleMetadata sampleMetadata = new SampleMetadata();
            sampleMetadata.name = benchmark.getMethodTitle();
            sampleMetadata.rawData = benchmark.getMeasurements().stream()
                    .filter(measurement -> measurement.getIterationMode().equals(testedIterationMode) &&
                            measurement.getIterationStage().equals(testedIterationStage))
                    .mapToDouble(Measurement::getNanoseconds).toArray();
            streamBuilder.accept(sampleMetadata);
        }
        return streamBuilder.build();
    }

    /**
     * Gets BenchmarkDotNetJSONBase object from file
     *
     * @param file file with results of performance tests
     * @return BenchmarkDotNetJSONBase object (it represents the whole JSON file)
     */
    private static BenchmarkDotNetJSONBase getBaseFromPath(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(file, BenchmarkDotNetJSONBase.class);
        } catch (Exception e) {
            //TODO: dodat výjimku
            return null;
        }
    }

    @Override
    public String getParserName() {
        return "BenchmarkDotNetJSONParser";
    }
}
