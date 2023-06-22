import java.util.List;
public interface ITest {
    String getName();
    int getInternalID();
    List<Double> getValues();

    // larger value implies better performance
    boolean hasAscendingPerformanceUnit();
}
