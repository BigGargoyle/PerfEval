package cz.cuni.mff.d3s.perfeval;

/**
 * Class for storing samples of performance measurement.
 */
public class Samples {
    /**
     * Name of the method that data belongs to.
     */
    String name;
    /**
     * Raw data of the samples.
     */
    double[][] rawData;
    /**
     * Metric used for the samples.
     */
    Metric metric;

    /**
     * Constructor for the Samples class.
     *
     * @param rawData Raw data of the samples.
     * @param metric  Metric used for the samples.
     * @param name    Name of the method that data belongs to.
     */
    public Samples(double[][] rawData, Metric metric, String name) {
        this.rawData = rawData;
        this.metric = metric;
        this.name = name;
    }

    /**
     * Returns the raw data of the samples.
     *
     * @return Raw data of the samples.
     */
    public double[][] getRawData() {
        return rawData;
    }

    /**
     * Returns the metric used for the samples.
     *
     * @return Metric used for the samples.
     */
    public Metric getMetric() {
        return metric;
    }

    /**
     * Returns the name of the method that data belongs to.
     *
     * @return Name of the method that data belongs to.
     */
    public String getName() {
        return name;
    }

}
