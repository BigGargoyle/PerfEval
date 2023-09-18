import org.example.PerformanceComparatorFactory.Bootstrap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.example.MeasurementFactory.UniversalTimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootstrapTest {

    // critValue = 0.1 -> less accuracy, but passes more often -> bootstrap will have its own critValue
    @Test
    public void testEvaluateSameDistribution() {
        List<UniversalTimeUnit> newSet = new ArrayList<>();
        newSet.add(new UniversalTimeUnit(200, TimeUnit.NANOSECONDS));
        newSet.add(new UniversalTimeUnit(201, TimeUnit.NANOSECONDS));
        newSet.add(new UniversalTimeUnit(202, TimeUnit.NANOSECONDS));

        List<UniversalTimeUnit> oldSet = new ArrayList<>();
        oldSet.add(new UniversalTimeUnit(200, TimeUnit.NANOSECONDS));
        oldSet.add(new UniversalTimeUnit(201, TimeUnit.NANOSECONDS));
        oldSet.add(new UniversalTimeUnit(202, TimeUnit.NANOSECONDS));

        boolean result = Bootstrap.evaluate(newSet, oldSet, 0.1, 3);
        assertTrue(result);
    }

    @Test
    public void testEvaluateDifferentDistribution() {
        List<UniversalTimeUnit> newSet = new ArrayList<>();
        newSet.add(new UniversalTimeUnit(200, java.util.concurrent.TimeUnit.MILLISECONDS));
        newSet.add(new UniversalTimeUnit(300, java.util.concurrent.TimeUnit.MILLISECONDS));
        newSet.add(new UniversalTimeUnit(400, java.util.concurrent.TimeUnit.MILLISECONDS));

        List<UniversalTimeUnit> oldSet = new ArrayList<>();
        oldSet.add(new UniversalTimeUnit(500, java.util.concurrent.TimeUnit.MILLISECONDS));
        oldSet.add(new UniversalTimeUnit(600, java.util.concurrent.TimeUnit.MILLISECONDS));
        oldSet.add(new UniversalTimeUnit(700, java.util.concurrent.TimeUnit.MILLISECONDS));

        boolean result = Bootstrap.evaluate(newSet, oldSet, 0.05, 3);
        assertFalse(result);
    }
}
