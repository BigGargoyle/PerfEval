package cz.cuni.mff.d3s.perfeval.performancecomparators;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;

public interface PerformanceComparator {
    MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples);

}
