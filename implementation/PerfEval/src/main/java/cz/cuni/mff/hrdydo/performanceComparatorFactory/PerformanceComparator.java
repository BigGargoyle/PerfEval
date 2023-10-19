package cz.cuni.mff.hrdydo.performanceComparatorFactory;

import cz.cuni.mff.hrdydo.Samples;
import cz.cuni.mff.hrdydo.evaluation.MeasurementComparisonRecord;

public interface PerformanceComparator {
    MeasurementComparisonRecord compareSets(Samples oldSamples, Samples newSamples);

}
