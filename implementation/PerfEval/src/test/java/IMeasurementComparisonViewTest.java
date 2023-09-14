import org.example.Evaluation.MeasurementComparisonResultView;
import org.example.Evaluation.IMeasurementComparisonResult;
import org.example.PerformanceComparatorFactory.ComparisonResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IMeasurementComparisonViewTest {
/*
    private IMeasurementComparisonResult mockComparisonResult;

    @BeforeEach
    public void setUp() {
        mockComparisonResult = mock(IMeasurementComparisonResult.class);
        when(mockComparisonResult.getNewAvg()).thenReturn(15.0);
        when(mockComparisonResult.getOldAvg()).thenReturn(10.0);
        when(mockComparisonResult.getChange()).thenReturn(-0.5);
        when(mockComparisonResult.getComparisonResult()).thenReturn(ComparisonResult.SameDistribution);
        when(mockComparisonResult.getComparisonVerdict()).thenReturn(true);
    }

    @Test
    public void testConstructorWithIMeasurementComparisonResult() {
        MeasurementComparisonResultView resultView = new MeasurementComparisonResultView(mockComparisonResult);
        assertEquals(15.0, resultView.newAverage, 0.001);
        assertEquals(10.0, resultView.oldAverage, 0.001);
        assertEquals(-0.5, resultView.performanceChange, 0.001);
        assertEquals(ComparisonResult.SameDistribution, resultView.comparisonResult);
        assertEquals(true, resultView.testVerdict);
    }

    @Test
    public void testConstructorWithValues() {
        MeasurementComparisonResultView resultView = new MeasurementComparisonResultView(15.0, 10.0, -0.5, ComparisonResult.SameDistribution, true, "test_name");
        assertEquals(15.0, resultView.newAverage, 0.001);
        assertEquals(10.0, resultView.oldAverage, 0.001);
        assertEquals(-0.5, resultView.performanceChange, 0.001);
        assertEquals(ComparisonResult.SameDistribution, resultView.comparisonResult);
        assertEquals(true, resultView.testVerdict);
    }*/
}
