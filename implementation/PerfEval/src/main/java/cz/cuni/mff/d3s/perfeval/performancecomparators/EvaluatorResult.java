package cz.cuni.mff.d3s.perfeval.performancecomparators;

public record EvaluatorResult(double ciLowerBound, double ciUpperBound, boolean areSetsSame) {
}
