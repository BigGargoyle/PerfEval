import com.fasterxml.jackson.databind.ObjectMapper;
import pojoJMH.BenchmarkJMHJSONBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IMeasurementParser for JMH framework test result in the JSON format
 */
public class JMHJSONParser implements IMeasurementParser {
    Long timestamp = null;
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
        return "framework: JMH, format: JSON ";
    }

    @Override
    public Long GetUniqueID() {
        return timestamp;
    }
}
