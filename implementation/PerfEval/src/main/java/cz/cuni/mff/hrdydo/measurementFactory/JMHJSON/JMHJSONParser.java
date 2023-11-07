package cz.cuni.mff.hrdydo.measurementFactory.JMHJSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.hrdydo.measurementFactory.MeasurementParser;
import cz.cuni.mff.hrdydo.Metric;
import cz.cuni.mff.hrdydo.measurementFactory.JMHJSON.pojoJMH.BenchmarkJMHJSONBase;
import cz.cuni.mff.hrdydo.Samples;

import java.io.File;
import java.util.*;

/**
 * Implementation of IMeasurementParser for JMH framework test result in the JSON format
 */

public class JMHJSONParser implements MeasurementParser {

    //Metric metric;
    public JMHJSONParser(){}

    static final String[] timeScoreUnits = new String[] {"ns/op","us/op","ms/op","s/op"};
    static final String[] frequencyScoreUnits = new String[] {"ops/s","ops/ms","ops/us","ops/ns"};
    @Override
    public List<Samples> getTestsFromFiles(String[] fileNames) {
        Dictionary<String, Samples> samplesDictionary = new Hashtable<>();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            addSamplesToDictionary(fileName, fileNames.length, i, samplesDictionary);
        }
        List<Samples> samples = new ArrayList<>();
        Enumeration<Samples> samplesEnumeration = samplesDictionary.elements();
        while(samplesEnumeration.hasMoreElements()){
            samples.add(samplesEnumeration.nextElement());
        }
        return samples;
    }
    void addSamplesToDictionary(String fileName, int fileCount, int fileIndex, Dictionary<String, Samples> samplesDictionary){
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONBase[] base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkJMHJSONBase[].class);
        }catch (Exception e){
            return;
        }
        assert base != null;
        for (BenchmarkJMHJSONBase benchmark: base) {
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

            if(samplesDictionary.get(name)==null){
                Samples samples = new Samples(new double[fileCount][], metric, name);
                samplesDictionary.put(name, samples);
                for(int i = 0; i < fileCount; i++){
                    samples.getRawData()[i] = new double[0];
                }
            }
            List<List<Double>> measuredValues = benchmark.getPrimaryMetric().getRawData();
            List<Double> measuredValues1D = new ArrayList<>();
            for (List<Double> measuredValue : measuredValues) {
                measuredValues1D.addAll(measuredValue);
            }
            if(metric.isCompatibleWith(samplesDictionary.get(name).getMetric()))
                samplesDictionary.get(name).getRawData()[fileIndex] = measuredValues1D.stream().mapToDouble(Double::valueOf).toArray();
            else throw new IllegalArgumentException("Incompatible metrics");
        }
    }
}
