package cz.cuni.mff.d3s.perfeval;

/**
 * Class representing a metric.
 * It contains the type of the metric and whether higher values are better.
 */
public class Metric {
    /**
     * Creates a new metric.
     *
     * @param metricType     Type of the metric.
     * @param higherIsBetter Whether higher values are better.
     */
    public Metric(String metricType, boolean higherIsBetter) {
        this.metricType = metricType;
        this.higherIsBetter = higherIsBetter;
    }

    /**
     * How many digits after the decimal point should be printed.
     */
    static int basicAccuracy = 5;
    /**
     * Type of the metric.
     */
    String metricType;
    /**
     * Whether higher values are better.
     */
    boolean higherIsBetter;

    /**
     * Converts a value to a string.
     *
     * @param value Value to be converted.
     * @return String representation of the value.
     */
    public String valueToString(double value) {
        assert basicAccuracy > 0;
        String format = "%." + basicAccuracy + "f " + metricType;
        return String.format(format, value);
    }

    /**
     * compares two metrics if they are compatible with each other (have the same type)
     *
     * @param metric metric to be compared with
     * @return if the metric is compatible with this metric
     */
    public boolean isCompatibleWith(Metric metric) {
        return metricType.compareTo(metric.metricType) == 0;
    }

    /**
     * returns metric performance direction
     *
     * @return if higher values are better, otherwise false
     */
    public boolean getMetricPerformanceDirection() {
        return higherIsBetter;
    }
}
