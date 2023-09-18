package MeasurementFactoryTests;

import java.util.List;
import org.example.MeasurementFactory.BenchmarkDotNetJSON.BenchmarkDotNetJSONParser;
import org.example.MeasurementFactory.JMHJSON.JMHJSONParser;
import org.example.MeasurementFactory.ParserIndustry;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.IMeasurementParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void RecognizingBenchmarkDotNetJSONFile()
    {
        String fileName = "src/test/resources/MyBenchmarks.SortingAlgorithms-report.json";

        IMeasurementParser parser = ParserIndustry.recognizeParserFactory(fileName);

        assertSame(BenchmarkDotNetJSONParser.class, parser.getClass());
    }
    @Test
    public void RecognizingBenchmarkDotNetJSONTestCount()
    {
        String fileName = "src/test/resources/Sorting1.json";
        int countOfTestInFile = 2;
        IMeasurementParser parser = ParserIndustry.recognizeParserFactory(fileName);
        List<IMeasurement> measurements = parser.getTestsFromFile(fileName);

        assertEquals(2, measurements.size());
    }
    @Test
    public void RecognizingJMHJSONFile()
    {
        String fileName = "src/test/resources/resultJMH.json";

        IMeasurementParser parser = ParserIndustry.recognizeParserFactory(fileName);

        assertSame(JMHJSONParser.class, parser.getClass());
    }
    @Test
    public void RecognizingUnknownFile()
    {
        String fileName = "src/test/resources/unknownFileType";

        IMeasurementParser parser = ParserIndustry.recognizeParserFactory(fileName);

        assertNull(parser);
    }
}
