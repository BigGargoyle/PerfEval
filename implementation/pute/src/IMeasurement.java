import java.util.List;
public interface IMeasurement {
    String getName();
    List<Double> getValues();

    // larger value implies better performance
    boolean hasAscendingPerformanceUnit();
}
