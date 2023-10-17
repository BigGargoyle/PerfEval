package org.example;

public class Metric {

    public Metric(String metricType, boolean higherIsBetter){
        this.metricType = metricType;
        this.higherIsBetter = higherIsBetter;
    }

    static int basicAccuracy = 5;
    String metricType;
    boolean higherIsBetter;

    public String valueToString(double value) {
        assert basicAccuracy > 0;
        return valueToString(value, basicAccuracy);
    }

    String valueToString(double value, int accuracy) {
        String format = "%." + accuracy + "f " + metricType;
        return String.format(format, value);
    }

    public boolean isCompatibleWith(Metric metric){
        return metricType.compareTo(metric.metricType)==0;
    }
    public boolean getMetricPerformanceDirection(){return higherIsBetter;}
}
