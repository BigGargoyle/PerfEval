import cz.cuni.mff.hrdydo.Samples;
import cz.cuni.mff.hrdydo.measurementFactory.BenchmarkDotNetJSON.BenchmarkDotNetJSONParser;
import cz.cuni.mff.hrdydo.measurementFactory.JMHJSON.JMHJSONParser;
import cz.cuni.mff.hrdydo.measurementFactory.MeasurementParser;
import cz.cuni.mff.hrdydo.measurementFactory.ParserFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResultParserTest {

    @Test
    public void recognizeCorrectJMHJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("resultJMH.json").getFile());
        var parser = ParserFactory.recognizeParserFactory(file.getAbsolutePath());
        assertTrue(parser instanceof JMHJSONParser);
    }
    @Test
    public void recognizeCorrectBenchmarkDotNetJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Sorting1.json").getFile());
        var parser = ParserFactory.recognizeParserFactory(file.getAbsolutePath());
        assertTrue(parser instanceof BenchmarkDotNetJSONParser);
    }
    @Test
    public void parseCorrectJMHJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("resultJMH.json").getFile());
        MeasurementParser parser = ParserFactory.recognizeParserFactory(file.getAbsolutePath());
        assert parser != null;
        List<Samples> result = parser.getTestsFromFiles(new String[]{file.getAbsolutePath()});
        assertEquals(1, result.size());
        assertTrue(result.get(0).getRawData().length > 0);
        assertTrue(result.get(0).getMetric().getMetricPerformanceDirection());
    }
    @Test
    public void parseCorrectBenchmarkDotNetJSONOutputFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Sorting1.json").getFile());
        MeasurementParser parser = ParserFactory.recognizeParserFactory(file.getAbsolutePath());
        assert parser != null;
        List<Samples> result = parser.getTestsFromFiles(new String[]{file.getAbsolutePath()});
        assertEquals(2, result.size());
        assertTrue(result.get(0).getRawData().length > 0);
        assertFalse(result.get(0).getMetric().getMetricPerformanceDirection());
    }
    @Test
    public void parseMultipleCorrectBenchmarkDotNetJSONFiles(){
        ClassLoader classLoader = getClass().getClassLoader();
        File[] file = new File[]{new File(classLoader.getResource("Sorting1.json").getFile()),
                new File(classLoader.getResource("Sorting2.json").getFile()),
                new File(classLoader.getResource("Sorting3.json").getFile()),
                new File(classLoader.getResource("Sorting4.json").getFile())};

        MeasurementParser parser = ParserFactory.recognizeParserFactory(file[0].getAbsolutePath());
        assert parser != null;
        String[] fileNames = new String[file.length];
        for (int i = 0; i < file.length; i++) {
            fileNames[i] = file[i].getAbsolutePath();
        }
        List<Samples> result = parser.getTestsFromFiles(fileNames);
        assertEquals(2, result.size());
    }

}
