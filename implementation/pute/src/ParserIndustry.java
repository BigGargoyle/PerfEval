import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class ParserIndustry {
    // construct parser according to file structure
    static IMeasurementParser RecognizeParserFactory(String fileName){
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
    static IMeasurementParser BenchmarkDotNetJSONParserFactory(){
        return new BenchmarkDotNetJSONParser();
    }
    static IMeasurementParser JMHJSONParserFactory(){
        return new JMHJSONParser();
    }
}
