import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import pojoBenchmarkDotNet.*;
public class BenchmarkDotNetJSONParser implements ITestParser{
    public ITest ParseTest(String sourceString){
        return null;
    }
    public List<ITest> GetTestsFromFile(String fileName){
        List<ITest> result = new ArrayList<ITest>();
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkDotNetJSONBase base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkDotNetJSONBase.class);
        }catch (Exception e){
            return null;
        }

        for(Benchmark benchmark: base.getBenchmarks()){
            result.add(BenchmarkDotNetTest.ConstructTest(benchmark));
        }

        return result;
    }
    public String GetParserType(){
        return null;
    }
}
