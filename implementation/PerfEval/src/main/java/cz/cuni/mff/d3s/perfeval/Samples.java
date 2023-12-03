package cz.cuni.mff.d3s.perfeval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    List<double[]> rawData;
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
        this.rawData = Arrays.stream(rawData).toList();
        this.metric = metric;
        this.name = name;
    }

    public Samples(Metric metric, String name) {
        this.metric = metric;
        this.name = name;
        this.rawData = new ArrayList<>();
    }
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

}
