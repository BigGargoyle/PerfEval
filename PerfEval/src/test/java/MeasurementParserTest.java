import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.jmhjson.JMHJSONParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.ParserFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MeasurementParserTest {

    @Test
    public void getJMHJSONParser(){
        new JMHJSONParser();
        var parser = ParserFactory.getParser(JMHJSONParser.getParserName());
        assertTrue(parser instanceof JMHJSONParser);
    }
    @Test
    public void getBenchmarkDotNetJSONParser(){
        new BenchmarkDotNetJSONParser();
        var parser = ParserFactory.getParser(BenchmarkDotNetJSONParser.getParserName());
        assertTrue(parser instanceof BenchmarkDotNetJSONParser);
    }
    @Test
    public void parseCorrectJMHJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("resultJMH.json")).getFile());
        MeasurementParser parser = new JMHJSONParser();
        assertNotNull(parser);
        List<Samples> result = parser.getTestsFromFiles(new String[]{file.getAbsolutePath()});
        assertEquals(1, result.size());
        assertTrue(result.get(0).getRawData().length > 0);
        assertTrue(result.get(0).getMetric().getMetricPerformanceDirection());
    }
    @Test
    public void parseCorrectBenchmarkDotNetJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("Sorting1.json")).getFile());
        MeasurementParser parser = new BenchmarkDotNetJSONParser();
        List<Samples> result = parser.getTestsFromFiles(new String[]{file.getAbsolutePath()});
        assertEquals(2, result.size());
        assertTrue(result.get(0).getRawData().length > 0);
        assertFalse(result.get(0).getMetric().getMetricPerformanceDirection());
    }
    @Test
    public void parseMultipleCorrectBenchmarkDotNetJSONFiles(){
        ClassLoader classLoader = getClass().getClassLoader();
        File[] file = new File[]{new File(Objects.requireNonNull(classLoader.getResource("Sorting1.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("Sorting2.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("Sorting3.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("Sorting4.json")).getFile())};
        MeasurementParser parser = new BenchmarkDotNetJSONParser();
        String[] fileNames = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            fileNames[i] = file[i].getAbsolutePath();
        }
        List<Samples> result = parser.getTestsFromFiles(fileNames);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getRawData().length);
        assertEquals(4, result.get(1).getRawData().length);
    }

    @Test
    public void parseMultipleCorrectJMHJSONFiles(){
        ClassLoader classLoader = getClass().getClassLoader();
        File[] file = new File[]{new File(Objects.requireNonNull(classLoader.getResource("resultJMH.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("jmh-result2.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("jmh-result3.json")).getFile())};

        MeasurementParser parser = new JMHJSONParser();
        String[] fileNames = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            fileNames[i] = file[i].getAbsolutePath();
        }
        List<Samples> result = parser.getTestsFromFiles(fileNames);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getRawData().length >= 3);
    }

}
