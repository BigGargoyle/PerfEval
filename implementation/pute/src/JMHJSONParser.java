import com.fasterxml.jackson.databind.ObjectMapper;
import pojoJMH.BenchmarkJMHJSONBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JMHJSONParser implements ITestParser{
    public ITest ParseTest(String sourceString){
        return  null;
    }
    public List<ITest> GetTestsFromFile(String fileName){
        List<ITest> result = new ArrayList<ITest>();
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONBase[] base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkJMHJSONBase[].class);
        }catch (Exception e){
            return null;
        }

        for(BenchmarkJMHJSONBase benchmark: base){
            result.add(JMHTest.ConstructTest(benchmark));
        }

        return result;
    }
    public String GetParserType(){
        return null;
    }
}
