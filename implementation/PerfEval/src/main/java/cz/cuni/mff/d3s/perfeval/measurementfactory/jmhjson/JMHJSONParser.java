package cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.d3s.perfeval.Metric;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.pojoJMH.BenchmarkJMHJSONBase;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of IMeasurementParser for JMH framework test result in the JSON format
 */

public class JMHJSONParser implements MeasurementParser {

    public JMHJSONParser(){}
    static final String[] timeScoreUnits = new String[] {"ns/op","us/op","ms/op","s/op"};
    static final String[] frequencyScoreUnits = new String[] {"ops/s","ops/ms","ops/us","ops/ns"};
    @Override
    public List<Samples> getTestsFromFiles(String[] fileNames) {
        Map<String, Samples> samplesDictionary = new HashMap<>();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            var samplesMetadata = getSamplesMetadataFromFile(fileName);
            // finalI because of lambda and compiler
            int finalI = i;
            samplesMetadata.forEach(sampleMetadata -> {
                if(samplesDictionary.get(sampleMetadata.name)==null){
                    Samples samples = new Samples(new double[fileNames.length][], sampleMetadata.metric, sampleMetadata.name);
                    samplesDictionary.put(sampleMetadata.name, samples);
                    for(int j = 0; j < fileNames.length; j++){
                        samples.getRawData()[j] = new double[0];
                    }
                }
                if(sampleMetadata.metric.isCompatibleWith(samplesDictionary.get(sampleMetadata.name).getMetric()))
                    samplesDictionary.get(sampleMetadata.name).getRawData()[finalI] = sampleMetadata.rawData;
                // else nothing added
            });
        }
        return new ArrayList<>(samplesDictionary.values());
    }

    static class SampleMetadata{
        public String name;
        public double[] rawData;
        Metric metric;
    }

    Stream<SampleMetadata> getSamplesMetadataFromFile(String fileName){
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONBase[] base;
        try {
            base = objectMapper.readValue(inputFile, BenchmarkJMHJSONBase[].class);
        } catch (Exception e) {
            return null;
        }
        assert base != null;
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

        for(BenchmarkJMHJSONBase benchmark : base){
            var name = benchmark.getBenchmark();
            var primaryMetric = benchmark.getPrimaryMetric();
            Metric metric = null;
            for(String scoreUnit : frequencyScoreUnits){
                if(primaryMetric.getScoreUnit().compareTo(scoreUnit)==0){
                    metric = new Metric(scoreUnit, true);
                    break;
                }
            }
            if(metric==null)
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
}
