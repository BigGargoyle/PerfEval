package cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.pojoJMH.BenchmarkJMHJSONRoot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, List<SampleMetadata>> samplesPerTestName = new HashMap<>();

        for(var fileName : fileNames){
            Map<String, List<SampleMetadata>> samplesPerTestNameFromFile = getSamplesFromOneFile(fileName);
            for(var key : samplesPerTestNameFromFile.keySet()){
                samplesPerTestName.computeIfAbsent(key, k -> new ArrayList<>());
                samplesPerTestName.get(key).addAll(samplesPerTestNameFromFile.get(key));
            }
        }

        return finishSamples(samplesPerTestName);
    }
    List<Samples> finishSamples(Map<String, List<SampleMetadata>> samplesPerTestName) {
        List<Samples> result = new ArrayList<>();
        for(var key : samplesPerTestName.keySet()){
            List<List<Double>> rawData = new ArrayList<>();
            for(var sample : samplesPerTestName.get(key)){
                rawData.addAll(sample.rawData);
            }
            double[][] rawDataArray = new double[rawData.size()][];
            for(int i = 0; i < rawData.size(); i++){
                rawDataArray[i] = new double[rawData.get(i).size()];
                for(int j = 0; j < rawData.get(i).size(); j++){
                    rawDataArray[i][j] = rawData.get(i).get(j);
                }
            }
            result.add(new Samples(rawDataArray, samplesPerTestName.get(key).get(0).metric, key));
        }
        return result;
    }

    @Override
    public List<Samples> getTestsFromFile(String fileName) {
        Map<String, List<SampleMetadata>> samplesPerTestName = getSamplesFromOneFile(fileName);
        return finishSamples(samplesPerTestName);
    }

    Map<String, List<SampleMetadata>> getSamplesFromOneFile(String fileName){
        Stream<String> iniStream = Arrays.stream(new String[] {fileName});
        Stream<SampleMetadata> samplesFromFile = iniStream.map(this::mapFileFromString).flatMap(this::mapRootFromJSON).map(this::mapMetadataFromRoot);
        Map<String, List<SampleMetadata>> samplesPerTestName = new HashMap<>();
        samplesFromFile.forEach(sampleMetadata -> {
            samplesPerTestName.computeIfAbsent(sampleMetadata.name, k -> new ArrayList<>());
            samplesPerTestName.get(sampleMetadata.name).add(sampleMetadata);
        });
        return samplesPerTestName;
    }

    File mapFileFromString(String fileName){
        return new File(fileName);
    }

    Stream<BenchmarkJMHJSONRoot> mapRootFromJSON(File file){
        ObjectMapper mapper = new ObjectMapper();
        try {
            BenchmarkJMHJSONRoot[] root = mapper.readValue(file, BenchmarkJMHJSONRoot[].class);
            return Arrays.stream(root);
        } catch (Exception e) {
            // TODO: dodat výjimku --> jak ošetřit -> problém ve streamu
            throw new RuntimeException();
            //throw new IOException("Unable to map JSON file to BenchmarkJMHJSONRoot.", e);
        }
    }

    SampleMetadata mapMetadataFromRoot(BenchmarkJMHJSONRoot root){
        SampleMetadata metadata = new SampleMetadata();
        metadata.name = root.getBenchmark();
        String scoreUnit = root.getPrimaryMetric().getScoreUnit();
        if (Arrays.asList(timeScoreUnits).contains(scoreUnit)) {
            metadata.metric = new Metric(scoreUnit, false);
        } else if (Arrays.asList(frequencyScoreUnits).contains(scoreUnit)) {
            metadata.metric = new Metric(scoreUnit, true);
        } else {
            //TODO: dodat výjimku
            throw new RuntimeException();
        }
        metadata.rawData = root.getPrimaryMetric().getRawData();
        return metadata;
    }

    /**
     * Class for storing metadata of one sample
     */
    // duplicate of BenchmarkDotNetJSONParser.SampleMetadata, but it is not possible to use it because of different benchmark framework
    static class SampleMetadata {
        public String name;
        public List<List<Double>> rawData;
        Metric metric;
    }

    public static String getParserName() {
        return "JMHJSONParser";
    }
}
