package cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson;
import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.Benchmark;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.BenchmarkDotNetJSONRoot;
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
        Map<String, List<SampleMetadataKey>> samplesMetadataKeysPerTestName = new HashMap<>();
        Map<SampleMetadataKey, List<SampleMetadata>> samplesMetadataPerKey = new HashMap<>();
        Stream<SampleMetadata> samples = Arrays.stream(fileNames)
                .map(this::mapStringToFile)
                .map(this::mapFileToBenchmarkDotNetJSONRoot)
                .flatMap(this::mapBenchmarkDotNetJSONRootToBenchmark)
                .flatMap(this::mapBenchmarkToMeasurement);


        //collect data from stream
        samples.forEach(sampleMetadata -> {
            SampleMetadataKey key = new SampleMetadataKey();
            key.name = sampleMetadata.name;
            key.launchIndex = sampleMetadata.launchIndex;
            samplesMetadataKeysPerTestName.computeIfAbsent(sampleMetadata.name, k -> new ArrayList<>());
            samplesMetadataKeysPerTestName.get(sampleMetadata.name).add(key);
            samplesMetadataPerKey.computeIfAbsent(key, k -> new ArrayList<>());
            samplesMetadataPerKey.get(key).add(sampleMetadata);
        });

        List<Samples> result = new ArrayList<>();
        for(var key : samplesMetadataKeysPerTestName.keySet()){
            double[][] rawData = new double[samplesMetadataKeysPerTestName.get(key).size()][];
            int i = 0;
            for(var key2 : samplesMetadataKeysPerTestName.get(key)){
                rawData[i] = samplesMetadataPerKey.get(key2).stream().mapToDouble(sampleMetadata -> sampleMetadata.rawData).toArray();
                i++;
            }
            result.add(new Samples(rawData,
                    metric,
                    key
            ));
        }

        return result;
    }

    private File mapStringToFile(String fileName) {
        return new File(fileName);
    }
    private BenchmarkDotNetJSONRoot mapFileToBenchmarkDotNetJSONRoot(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(file, BenchmarkDotNetJSONRoot.class);
        } catch (Exception e) {
            //TODO: dodat výjimku
            throw new RuntimeException(e);
        }
    }
    private Stream<Benchmark> mapBenchmarkDotNetJSONRootToBenchmark(BenchmarkDotNetJSONRoot root) {
        return root.getBenchmarks().stream();
    }
    private Stream<SampleMetadata> mapBenchmarkToMeasurement(Benchmark benchmark) {
        String methodName = benchmark.getMethodTitle();
        return benchmark.getMeasurements().stream()
                .filter(measurement -> measurement.getIterationMode().equals(testedIterationMode) &&
                        measurement.getIterationStage().equals(testedIterationStage))
                .map(measurement -> {
                    SampleMetadata sampleMetadata = new SampleMetadata();
                    sampleMetadata.name = methodName;
                    sampleMetadata.launchIndex = measurement.getLaunchIndex();
                    sampleMetadata.rawData = measurement.getNanoseconds();
                    return sampleMetadata;
                });
    }

    static class SampleMetadataKey{
        public String name;
        public int launchIndex;
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SampleMetadataKey that)) return false;
            return launchIndex == that.launchIndex &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, launchIndex);
        }
    }

    /**
     * Class for storing metadata of one sample
     */
    static class SampleMetadata {
        public String name;
        public int launchIndex;
        public double rawData;
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SampleMetadata that)) return false;
            return launchIndex == that.launchIndex &&
                    Objects.equals(name, that.name);
        }
    }

    @Override
    public String getParserName() {
        return "BenchmarkDotNetJSONParser";
    }
}
