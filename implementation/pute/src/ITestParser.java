import java.util.List;

public interface ITestParser {
    ITest ParseTest(String sourceString);
    List<ITest> GetTestsFromFile(String fileName);
    String GetParserType();
}
