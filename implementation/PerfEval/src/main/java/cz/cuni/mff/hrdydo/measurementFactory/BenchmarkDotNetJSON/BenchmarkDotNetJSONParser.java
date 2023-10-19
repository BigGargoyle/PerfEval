package cz.cuni.mff.hrdydo.measurementFactory.BenchmarkDotNetJSON;
import java.io.File;
import java.util.*;

import cz.cuni.mff.hrdydo.Metric;
import cz.cuni.mff.hrdydo.Samples;
import cz.cuni.mff.hrdydo.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.Benchmark;
import cz.cuni.mff.hrdydo.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.BenchmarkDotNetJSONBase;
import cz.cuni.mff.hrdydo.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.Measurement;
import cz.cuni.mff.hrdydo.measurementFactory.MeasurementParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.measurementFactory.BenchmarkDotNetJSON.pojoBenchmarkDotNet.*;

/**
 * Implementation of IMeasurementParser for BenchmarkDotNet framework test results in the JSON format.
 */
public class BenchmarkDotNetJSONParser implements MeasurementParser {
    Long timestamp = null;
    Metric metric;
    public BenchmarkDotNetJSONParser(){
        this.metric = new Metric("Nanoseconds", false);
    }
    public BenchmarkDotNetJSONParser(Metric metric){
        this.metric = metric;
    }
    static final String testedIterationMode = "Workload";
    static final String testedIterationStage = "Actual";

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
        BenchmarkDotNetJSONBase base = getBaseFromPath(new File(fileName));
        assert base != null;
        for (Benchmark benchmark: base.getBenchmarks()) {
            var name = benchmark.getMethodTitle();
            Metric localMetric = new Metric("Nanoseconds", false);
            if(samplesDictionary.get(name)==null){
                Samples samples = new Samples(new double[fileCount][], this.metric, name);
                samplesDictionary.put(name, samples);
            }
            List<Double> measuredValues = new ArrayList<>();
            for(Measurement measurement : benchmark.getMeasurements()){
                if(measurement.getIterationMode().equals(testedIterationMode) &&
                        measurement.getIterationStage().equals(testedIterationStage) &&
                        this.metric.isCompatibleWith(localMetric)){
                    measuredValues.add((double)measurement.getNanoseconds());
                }
            }
            samplesDictionary.get(name).getRawData()[fileIndex] = measuredValues.stream().mapToDouble(Double::valueOf).toArray();
        }
    }

    private static BenchmarkDotNetJSONBase getBaseFromPath(File file) {
        List<Samples> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkDotNetJSONBase base;
        try{
            return objectMapper.readValue(file, BenchmarkDotNetJSONBase.class);
        }catch (Exception e){
            //TODO: dodat výjimku
            return null;
        }
    }
}
