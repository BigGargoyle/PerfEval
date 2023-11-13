package cz.cuni.mff.d3s.perfeval.performanceComparatorFactory;

import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;

public interface PerformanceComparator {
    MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples);

}
