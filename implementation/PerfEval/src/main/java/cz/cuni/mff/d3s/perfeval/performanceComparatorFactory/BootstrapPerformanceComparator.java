package cz.cuni.mff.d3s.perfeval.performanceComparatorFactory;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;

public class BootstrapPerformanceComparator implements PerformanceComparator {
    static int MINUS_ONE = -1;
    HierarchicalBootstrap bootstrap;
    double critValue;
    double tolerance;

    public BootstrapPerformanceComparator(double critValue, double tolerance, int bootstrapSampleCount){
        bootstrap = new HierarchicalBootstrap(critValue, bootstrapSampleCount);
        this.critValue = critValue;
        this.tolerance = tolerance;
    }

    private MeasurementComparisonRecord constructRecord(Samples oldSamples, Samples newSamples, boolean bootstrapResult){
        double oldAvg = ArrayUtilities.calculateAverage(oldSamples.getRawData());
        double newAvg = ArrayUtilities.calculateAverage(newSamples.getRawData());

        double performanceChange = oldSamples.getMetric().getMetricPerformanceDirection() ? newAvg/oldAvg : oldAvg/newAvg;
        performanceChange = performanceChange*100 - 100;

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