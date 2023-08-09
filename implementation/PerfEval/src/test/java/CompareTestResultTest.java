import MeasurementFactoryTests.TestMeasurement;
import org.junit.jupiter.api.Test;

import java.util.List;
import org.example.MeasurementFactory.IMeasurement;
import org.example.CompareTestResult;

import static org.junit.jupiter.api.Assertions.*;

class CompareTestResultTest {
    /*@Test
    void compareMeasurements() {
    }*/

    /*@Test
    public void testComparisonResult() {
        // Sample data
        double criticalValue = 0.05;
        IMeasurement oldTest = new TestMeasurement("New Test", List.of(10.0, 12.0, 15.0), true);
        IMeasurement newTest = new TestMeasurement("Old Test", List.of(8.0, 9.0, 10.0), true);

        // Create CompareTestResult instance
        CompareTestResult compareTestResult = new CompareTestResult(criticalValue, newTest, oldTest);

        // Assert the comparison result
        assertTrue(compareTestResult.getCompareResult()); // As the p-value is smaller than the critical value, the test should pass.
    }

    @Test
    public void testComparisonResultWithFailedTest() {
        // Sample data
        double criticalValue = 0.05;
        IMeasurement oldTest = new TestMeasurement("New Test", List.of(10.0, 12.0, 15.0), true);
        IMeasurement newTest = new TestMeasurement("Old Test", List.of(8.0, 9.0, 20.0), true);

        // Create CompareTestResult instance
        CompareTestResult compareTestResult = new CompareTestResult(criticalValue, newTest, oldTest);

        // Assert the comparison result
        assertFalse(compareTestResult.getCompareResult()); // As the p-value is larger than the critical value, the test should fail.
    }*/

}