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
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.Measurement;

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
        Map<String, Samples> samplesPerTestName = getTestsFromFiles(Arrays.stream(fileNames));
        return new ArrayList<>(samplesPerTestName.values());
    }
    private Map<String, Samples> getTestsFromFiles(Stream<String> fileNames) {
        Map<String, Samples> samplesPerTestName = new HashMap<>();
        //generating File objects from file names
        fileNames.map(this::mapStringToFile)
            //parsing JSON files to POJOs
            .map(this::mapFileToBenchmarkDotNetJSONRoot)
            //mapping POJOs to Stream of Benchmarks
            .flatMap(this::mapBenchmarkDotNetJSONRootToBenchmark)
            //mapping Benchmarks to Stream of Samples
            .forEach(sample -> {
                //if sample of that name doesn't exist, create it
                samplesPerTestName.computeIfAbsent(sample.getMethod(), k -> new Samples(metric, sample.getMethod()));
                //add sample to the Samples object
                samplesPerTestName.get(sample.getMethod()).addSample(
                    //getting measurement times from the sample
                    sample.getMeasurements().stream()
                        //filtering only the measurements that have the tested iteration mode and stage
                        .filter(sampleForFilter -> testedIterationMode.compareTo(sampleForFilter.getIterationMode())==0
                                && testedIterationStage.compareTo(sampleForFilter.getIterationStage())==0)
                        //mapping the measurements to doubles and storing them in an array
                        .mapToDouble(Measurement::getNanoseconds).toArray());
            });

        return samplesPerTestName;
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
    public static String getParserName() {
        return "BenchmarkDotNetJSONParser";
    }
}
