package cz.cuni.mff.d3s.perfeval;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing samples of performance measurement.
 */
public class Samples {
    /**
     * Name of the method that data belongs to.
     */
    private final String name;
    /**
     * Raw data of the samples.
     * Array represents a single run, each element of the array represents a single measurement.
     * Because of variable number of runs, raw data are represented as a list of arrays.
     */
    private final List<double[]> rawData;
    /**
     * Metric used for the samples.
     */
    private final Metric metric;

    /**
     * Creates a new Samples object.
     *
     * @param metric Metric used for the samples.
     * @param name   Name of the method that data belongs to.
     */
    public Samples(Metric metric, String name) {
        this.metric = metric;
        this.name = name;
        this.rawData = new ArrayList<>();
    }

    /**
     * Adds a sample to the samples.
     *
     * @param sample Sample to be added.
     */
    public void addSample(double[] sample) {
        rawData.add(sample);
    }

    /**
     * Returns the raw data of the samples.
     *
     * @return Raw data of the samples.
     */
    public double[][] getRawData() {
        return rawData.toArray(new double[0][0]);
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

    /**
     * Merges two samples.
     *
     * @param samples Samples to be merged with.
     * @return Merged samples.
     */
    public Samples mergeSamples(Samples samples) {
        if (samples.getMetric().equals(this.metric)) {
            for (double[] sample : samples.getRawData()) {
                this.addSample(sample);
            }
        } else {
            throw new IllegalArgumentException("Samples have different metrics");
        }
        return this;
    }
}
