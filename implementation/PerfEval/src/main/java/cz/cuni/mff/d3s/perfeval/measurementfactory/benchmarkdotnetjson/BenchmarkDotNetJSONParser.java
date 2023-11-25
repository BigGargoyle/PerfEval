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
        Map<String, List<double[]>> samplesPerTestName = new HashMap<>();

        for(var fileName : fileNames){
            Map<String, List<double[]>> samplesPerTestNameFromFile = getTestsFromOneFile(fileName);
            for(var key : samplesPerTestNameFromFile.keySet()){
                samplesPerTestName.computeIfAbsent(key, k -> new ArrayList<>());
                samplesPerTestName.get(key).addAll(samplesPerTestNameFromFile.get(key));
            }
        }

        return finishSamples(samplesPerTestName);
    }

    @Override
    public List<Samples> getTestsFromFile(String fileName) {
        Map<String, List<double[]>> samplesPerTestName = getTestsFromOneFile(fileName);
        return finishSamples(samplesPerTestName);
    }

    List<Samples> finishSamples(Map<String, List<double[]>> samplesPerTestName) {
        List<Samples> result = new ArrayList<>();
        for(var key : samplesPerTestName.keySet()){
            // new double[0][] because of compiler
            Samples samples = new Samples(samplesPerTestName.get(key).toArray(new double[0][]), metric, key);
            result.add(samples);
        }
        return result;
    }

    //@Override
    private Map<String, List<double[]>> getTestsFromOneFile(String fileName) {
        Map<String, List<double[]>> rawDataPerTestName = new HashMap<>();
        Map<SampleMetadataKey, List<SampleMetadata>> samplesMetadataPerKey = new HashMap<>();
        var iniStream = Stream.of(fileName);
        Stream<SampleMetadata> samples = iniStream.map(this::mapStringToFile)
                .map(this::mapFileToBenchmarkDotNetJSONRoot)
                .flatMap(this::mapBenchmarkDotNetJSONRootToBenchmark)
                .flatMap(this::mapBenchmarkToMeasurement);


        //collect data from stream
        samples.forEach(sampleMetadata -> {
            SampleMetadataKey key = new SampleMetadataKey();
            key.name = sampleMetadata.name;
            key.launchIndex = sampleMetadata.launchIndex;
            samplesMetadataPerKey.computeIfAbsent(key, k -> new ArrayList<>());
            samplesMetadataPerKey.get(key).add(sampleMetadata);
        });

        //convert data to rawDataPerTestName
        for(var key : samplesMetadataPerKey.keySet()){
            double[] data = samplesMetadataPerKey.get(key).stream().mapToDouble(sampleMetadata -> sampleMetadata.rawData).toArray();
            rawDataPerTestName.computeIfAbsent(key.name, k -> new ArrayList<>());
            rawDataPerTestName.get(key.name).add(data);
        }

        return rawDataPerTestName;
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
