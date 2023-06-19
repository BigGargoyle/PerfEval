import java.util.List;

public class BenchmarkDotNetTest implements ITest {
    String Name;
    int InternalTestID;
    List<Double> Values;
    public static ITest ConstructTest(String input){
        return new BenchmarkDotNetTest(input);
    }
    private BenchmarkDotNetTest(String input){

    }
}
