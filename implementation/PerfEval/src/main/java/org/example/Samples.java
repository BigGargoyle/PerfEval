package org.example;

public class Samples {
    String name;
    double [][] rawData;
    Metric metric;

    public Samples(double[][] rawData, Metric metric, String name){
        this.rawData = rawData;
        this.metric = metric;
        this.name = name;
    }

    public double[][] getRawData(){
        return rawData;
    }
    public Metric getMetric(){return metric;}
    public String getName() {return name;}

}
