package org.example.Evaluation;

import org.codehaus.jackson.annotate.JsonProperty;
import org.example.MeasurementFactory.IMeasurement;
import org.example.PerformanceComparatorFactory.ComparisonResult;

public class MeasurementComparisonResultView {
    @JsonProperty
    public double newAverage;
    @JsonProperty
    public double oldAverage;
    @JsonProperty
    public double performanceChange;
    @JsonProperty
    public ComparisonResult comparisonResult;
    @JsonProperty
    public boolean testVerdict;

    public MeasurementComparisonResultView() {
    }

    public MeasurementComparisonResultView(IMeasurementComparisonResult comparisonResult) {
        this.newAverage = comparisonResult.getNewAvg();
        this.oldAverage = comparisonResult.getOldAvg();
        this.performanceChange = comparisonResult.getChange();
        this.comparisonResult = comparisonResult.getComparisonResult();
        this.testVerdict = comparisonResult.getComparisonVerdict();
    }

    public MeasurementComparisonResultView(double newAverage, double oldAverage, double performanceChange,
                                           ComparisonResult comparisonResult, boolean testVerdict) {
        this.newAverage = newAverage;
        this.oldAverage = oldAverage;
        this.performanceChange = performanceChange;
        this.comparisonResult = comparisonResult;
        this.testVerdict = testVerdict;
    }
}
