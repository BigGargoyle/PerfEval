package org.example.Evaluation;

import org.codehaus.jackson.annotate.JsonProperty;
import org.example.MeasurementFactory.IMeasurement;
import org.example.PerformanceComparatorFactory.ComparisonResult;

/**
 * Class that is representing serializable object of IMeasurementComparisonResult
 */
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

    @JsonProperty
    public String testName;

    /**
     * Creates an instance of MeasurementComparisonResultView from an instance of IMeasurementComparisonResult
     *
     * @param comparisonResult instance of IMeasurementComparisonResult that this instance will be image of
     */
    public MeasurementComparisonResultView(IMeasurementComparisonResult comparisonResult) {
        this.newAverage = comparisonResult.getNewAvg();
        this.oldAverage = comparisonResult.getOldAvg();
        this.performanceChange = comparisonResult.getChange();
        this.comparisonResult = comparisonResult.getComparisonResult();
        this.testVerdict = comparisonResult.getComparisonVerdict();
        this.testName = comparisonResult.getName();
    }

    /**
     * Crates an instance of MeasurementComparisonResultView from variables in arguments of method
     *
     * @param newAverage        value of newAverage to set
     * @param oldAverage        value of oldAverage to set
     * @param performanceChange value of performanceChange to set
     * @param comparisonResult  value of ComparisonResult to set
     * @param testVerdict       value of testVerdict to set
     * @param name              value of name of the test
     */
    public MeasurementComparisonResultView(double newAverage, double oldAverage, double performanceChange,
                                           ComparisonResult comparisonResult, boolean testVerdict, String name) {
        this.newAverage = newAverage;
        this.oldAverage = oldAverage;
        this.performanceChange = performanceChange;
        this.comparisonResult = comparisonResult;
        this.testVerdict = testVerdict;
        this.testName = name;
    }
}