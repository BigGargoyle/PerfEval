package cz.cuni.mff.d3s.perfeval.evaluation;

/**
 * Enum representing result of comparison of two Samples.
 */
public enum ComparisonResult {
    /**
     * Samples are from the same distribution.
     */
    SameDistribution(1),
    /**
     * Samples are from different distributions.
     */
    DifferentDistribution(2),
    /**
     * There are not enough samples to compare.
     */
    NotEnoughSamples(3),
    /**
     * There will never be enough samples to compare.
     */
    UnableToMeasureEnoughSamples(4),
    /**
     * There are only older samples.
     */
    OnlyOlderSamples(5),
    /**
     * There are only newer samples.
     */
    OnlyNewerSamples(6),
    /**
     * There was an error during comparison.
     */
    None(-1);
    /**
     * Number representing result of comparison.
     */
    private final int resultNumber;

    /**
     * Constructor for ComparisonResult.
     *
     * @param resultNumber number representing result of comparison
     */
    ComparisonResult(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    /**
     * Getter for resultNumber.
     *
     * @return number representing result of comparison
     */
    public int getResultNumber() {
        return resultNumber;
    }

}
