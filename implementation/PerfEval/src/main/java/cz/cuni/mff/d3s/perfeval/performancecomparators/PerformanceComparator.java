package cz.cuni.mff.d3s.perfeval.performancecomparators;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;

/**
 * Interface for comparing two sets of samples
 */
public interface PerformanceComparator {
    /**
     * Compares two sets of samples (with statistical tests)
     *
     * @param oldSamples samples from old version
     * @param newSamples samples from new version
     * @return record with results of comparison
     */
    MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples);
}
