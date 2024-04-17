package cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.Benchmark;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.BenchmarkDotNetJSONRoot;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParserException;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.pojoBenchmarkDotNet.Measurement;

/**
 * Implementation of MeasurementParser for BenchmarkDotNet framework test results in the JSON format.
 * @see MeasurementParser
 */
public class BenchmarkDotNetJSONParser implements MeasurementParser {
    /**
     * Metric used for parsing
     */
    private final Metric metric;

    /**
     * Constructor for BenchmarkDotNetJSONParser.
     */
    public BenchmarkDotNetJSONParser() {
        this.metric = new Metric("Nanoseconds", false);
    }

    /**
     * Iteration mode that is tested.
     */
    static final String testedIterationMode = "Workload";
    /**
     * Stage that is tested.
     */
    static final String testedIterationStage = "Actual";

    /**
     * Parses files with results of performance tests.
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
        //Map<String, Samples> samplesPerTestName = new HashMap<>();
        //generating File objects from file names
        return fileNames.map(this::mapStringToFile)
                //parsing JSON files to POJOs
                .map(this::mapFileToBenchmarkDotNetJSONRoot)
                //mapping POJOs to Stream of Benchmarks
                .flatMap(this::mapBenchmarkDotNetJSONRootToBenchmark)
                //mapping Benchmarks to Stream of Samples
                .map(this::mapBenchmarkToSamples)
                .collect(collectToMap());
    }

    private File mapStringToFile(String fileName) {
        return new File(fileName);
    }

    private BenchmarkDotNetJSONRoot mapFileToBenchmarkDotNetJSONRoot(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(file, BenchmarkDotNetJSONRoot.class);
        } catch (Exception e) {
            throw new MeasurementParserException("BenchmarkDotNetJSONParser, error while processing file: " + file.getName(), e);
        }
    }

    private Stream<Benchmark> mapBenchmarkDotNetJSONRootToBenchmark(BenchmarkDotNetJSONRoot root) {
        return root.getBenchmarks().stream();
    }

    private Samples mapBenchmarkToSamples(Benchmark benchmark) {
        // Assuming metric and testedIterationMode/testedIterationStage are defined elsewhere
        // and properly initialized.

        // Create a stream of Measurements filtered by iteration mode and stage
        double[] nanosecondsArray = benchmark.getMeasurements().stream()
                .filter(measurement ->
                        measurement.getIterationMode().equals(testedIterationMode)
                                && measurement.getIterationStage().equals(testedIterationStage))
                .mapToDouble(Measurement::getNanoseconds)
                .toArray();

        // Create Samples object and add the nanoseconds array
        Samples samples = new Samples(metric, benchmark.getMethod());
        samples.addSample(nanosecondsArray);

        // Return a Stream containing the Samples object
        return samples;
    }

    /**
     * Returns name of the parser.
     *
     * @return name of the parser
     */
    public static String getParserName() {
        return "BenchmarkDotNetJSONParser";
    }

    @Override
    public String toString() {
        return getParserName();
    }

    /**
     * Collects stream of sample containers into a map indexed by benchmark.
     * <p>
     * The map aggregates all samples from the stream that belong to the same benchmark.
     * The collector assumes only single metric is used with all runs of each benchmark.
     *
     * @return collector that collects stream of sample containers into a map indexed by benchmark
     */
    public static Collector<Samples, Map<String, Samples>, Map<String, Samples>> collectToMap() {
        return Collector.of(
                HashMap::new,
                (accumulator, item) -> accumulator.merge(item.getName(), item, Samples::mergeSamples),
                (left, right) -> {
                    right.forEach((name, item) -> left.merge(name, item, Samples::mergeSamples));
                    return left;
                }
        );
    }
}
