import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ParserIndustry {
    // construct parser according to file structure
    static ITestParser RecognizeParserFactory(String fileName){
        List<String> lines;
        try{
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }

        String oneLine = String.join("", lines);
        if(oneLine.contains("\"BenchmarkDotNetVersion\"") && oneLine.contains("\"BenchmarkDotNetCaption\""))
            return new BenchmarkDotNetJSONParser();
        if(oneLine.contains("\"jmhVersion\"") && oneLine.contains("\"benchmark\""))
            return new JMHJSONParser();
        return null;
    }
    static ITestParser BenchmarkDotNetJSONParserFactory(){
        return new BenchmarkDotNetJSONParser();
    }
    static ITestParser JMHJSONParserFactory(){
        return new JMHJSONParser();
    }
}
