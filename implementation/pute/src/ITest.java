import java.util.List;
public interface ITest {
    String GetName();
    int GetInternalID();
    List<Double> GetValues();

    // larger value implies better performance
    boolean HasAscendingPerformanceUnit();
}
