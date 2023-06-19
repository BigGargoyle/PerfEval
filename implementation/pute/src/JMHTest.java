import java.util.List;

public class JMHTest implements ITest{
    String Name;
    int InternalTestID;
    List<Double> Values;
    public static ITest ConstructTest(String input){
        return new JMHTest(input);
    }
    private JMHTest(String input){

    }

}
