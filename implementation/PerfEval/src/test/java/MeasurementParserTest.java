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
    public void recognizeCorrectJMHJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        var parser = ParserFactory.getParser(new JMHJSONParser().getParserName());
        assertTrue(parser instanceof JMHJSONParser);
    }
    @Test
    public void recognizeCorrectBenchmarkDotNetJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        var parser = ParserFactory.getParser(new JMHJSONParser().getParserName());
        assertTrue(parser instanceof BenchmarkDotNetJSONParser);
    }
    @Test
    public void parseCorrectJMHJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("resultJMH.json")).getFile());
        MeasurementParser parser = ParserFactory.getParser(new JMHJSONParser().getParserName());
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
        MeasurementParser parser = ParserFactory.getParser(new BenchmarkDotNetJSONParser().getParserName());
        assertNotNull(parser);
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
        MeasurementParser parser = ParserFactory.getParser(new BenchmarkDotNetJSONParser().getParserName());
        assert parser != null;
        String[] fileNames = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            fileNames[i] = file[i].getAbsolutePath();
        }
        List<Samples> result = parser.getTestsFromFiles(fileNames);
        assertEquals(2, result.size());
    }

    @Test
    public void parseMultipleCorrectJMHJSONFiles(){
        ClassLoader classLoader = getClass().getClassLoader();
        File[] file = new File[]{new File(Objects.requireNonNull(classLoader.getResource("resultJMH.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("jmh-result2.json")).getFile()),
                new File(Objects.requireNonNull(classLoader.getResource("jmh-result3.json")).getFile())};

        MeasurementParser parser = ParserFactory.getParser(new JMHJSONParser().getParserName());
        assertNotNull(parser);
        String[] fileNames = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            fileNames[i] = file[i].getAbsolutePath();
        }
        List<Samples> result = parser.getTestsFromFiles(fileNames);
        assertEquals(1, result.size());
    }

}
