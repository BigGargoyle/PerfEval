package cz.cuni.mff.d3s.perfeval.performancecomparators;

/**
 * Enum representing result of comparison of two Samples
 */
public enum ComparisonResult {
    /**
     * Samples are from the same distribution
     */
    SameDistribution(1),
    /**
     * Samples are from different distributions
     */
    DifferentDistribution(2),
    /**
     * There are not enough samples to compare
     */
    NotEnoughSamples(3),
    /**
     * Bootstrap were used, or it should be used because there are not enough time to measure enough samples
     */
    Bootstrap(4),
    /**
     * There was an error during comparison
     */
    None(-1);
    /**
     * Number representing result of comparison
     */
    private final int resultNumber;

    /**
     * Constructor for ComparisonResult
     *
     * @param resultNumber number representing result of comparison
     */
    ComparisonResult(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    /**
     * Getter for resultNumber
     *
     * @return number representing result of comparison
     */
    public int getResultNumber() {
        return resultNumber;
    }

}
