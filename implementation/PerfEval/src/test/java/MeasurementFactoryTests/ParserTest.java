package MeasurementFactoryTests;

import java.util.List;
import org.example.measurementFactory.BenchmarkDotNetJSON.BenchmarkDotNetJSONParser;
import org.example.measurementFactory.JMHJSON.JMHJSONParser;
import org.example.measurementFactory.ParserFactory;
import org.example.measurementFactory.Samples;
import org.example.measurementFactory.MeasurementParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void RecognizingBenchmarkDotNetJSONFile()
    {
        String fileName = "src/test/resources/MyBenchmarks.SortingAlgorithms-report.json";

        MeasurementParser parser = ParserFactory.recognizeParserFactory(fileName);

        assertSame(BenchmarkDotNetJSONParser.class, parser.getClass());
    }
    @Test
    public void RecognizingBenchmarkDotNetJSONTestCount()
    {
        String fileName = "src/test/resources/Sorting1.json";
        int countOfTestInFile = 2;
        MeasurementParser parser = ParserFactory.recognizeParserFactory(fileName);
        List<Samples> measurements = parser.getTestsFromFile(fileName);

        assertEquals(2, measurements.size());
    }
    @Test
    public void RecognizingJMHJSONFile()
    {
        String fileName = "src/test/resources/resultJMH.json";

        MeasurementParser parser = ParserFactory.recognizeParserFactory(fileName);

        assertSame(JMHJSONParser.class, parser.getClass());
    }
    @Test
    public void RecognizingUnknownFile()
    {
        String fileName = "src/test/resources/unknownFileType";

        MeasurementParser parser = ParserFactory.recognizeParserFactory(fileName);

        assertNull(parser);
    }
}
