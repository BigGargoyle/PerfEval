package cz.cuni.mff.d3s.perfeval.performancecomparators;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;

/**
 * Class that compares two sets of samples using bootstrap
 */
public class BootstrapPerformanceComparator implements PerformanceComparator {
    /**
     * Constant used for marking that minimal count of samples was not needed
     */
    static int MINUS_ONE = -1;
    /**
     * Bootstrap object
     */
    HierarchicalBootstrap bootstrap;
    /**
     * Critical value for bootstrap
     */
    double critValue;
    /**
     * Tolerance, how much can the performance get worse
     */
    double tolerance;

    /**
     * Constructor for BootstrapPerformanceComparator
     *
     * @param critValue            critical value for bootstrap
     * @param tolerance            tolerance, how much can the performance get worse
     * @param bootstrapSampleCount count of samples used for bootstrap
     */
    public BootstrapPerformanceComparator(double critValue, double tolerance, int bootstrapSampleCount) {
        bootstrap = new HierarchicalBootstrap(critValue, bootstrapSampleCount);
        this.critValue = critValue;
        this.tolerance = tolerance;
    }

    /**
     * Constructor for BootstrapPerformanceComparator
     *
     * @param oldSamples      samples from old version
     * @param newSamples      samples from new version
     * @param bootstrapResult result of bootstrap, true if performance of new version did not get worse, false otherwise
     * @return record with results of comparison
     */
    private MeasurementComparisonRecord constructRecord(Samples oldSamples, Samples newSamples, boolean bootstrapResult) {
        double oldAvg = ArrayUtilities.calculateAverage(oldSamples.getRawData());
        double newAvg = ArrayUtilities.calculateAverage(newSamples.getRawData());

        double performanceChange = oldSamples.getMetric().getMetricPerformanceDirection() ? newAvg / oldAvg : oldAvg / newAvg;
        performanceChange = performanceChange * 100 - 100;

        boolean testVerdict = bootstrapResult || performanceChange > 100 || Math.abs(performanceChange / 100) > 1 - tolerance;
        // inlined bootstrap, because there will always be a bootstrap
        return new MeasurementComparisonRecord(oldAvg, newAvg, performanceChange, ComparisonResult.Bootstrap, testVerdict, MINUS_ONE, oldSamples, newSamples);
    }

    @Override
    public MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples) {
        return constructRecord(oldSamples, newSamples,
                bootstrap.evaluateBootstrap(oldSamples.getRawData(), newSamples.getRawData()));
    }
}