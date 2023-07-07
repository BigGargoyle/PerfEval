import com.fasterxml.jackson.databind.ObjectMapper;
import pojoJMH.BenchmarkJMHJSONBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JMHJSONParser implements IMeasurementParser {
    Long timestamp = null;
    public IMeasurement ParseTest(String sourceString){
        return  null;
    }
    public List<IMeasurement> GetTestsFromFile(String fileName){
        List<IMeasurement> result = new ArrayList<IMeasurement>();
        File inputFile = new File(fileName);
        ObjectMapper objectMapper = new ObjectMapper();
        BenchmarkJMHJSONBase[] base;
        try{
            base = objectMapper.readValue(inputFile, BenchmarkJMHJSONBase[].class);
        }catch (Exception e){
            return null;
        }

        for(BenchmarkJMHJSONBase benchmark: base){
            result.add(JMHMeasurement.ConstructTest(benchmark));
        }

        timestamp = inputFile.lastModified();

        return result;
    }
    public String GetParserType(){
        return null;
    }

    @Override
    public Long GetUniqueID() {
        return timestamp;
    }
}
