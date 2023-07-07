import java.util.List;

public interface IMeasurementParser {
    IMeasurement ParseTest(String sourceString);
    List<IMeasurement> GetTestsFromFile(String fileName);
    String GetParserType();
}
