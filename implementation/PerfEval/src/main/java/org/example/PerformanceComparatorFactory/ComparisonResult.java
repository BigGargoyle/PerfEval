package org.example.PerformanceComparatorFactory;

public enum ComparisonResult {
    SameDistribution(1),
    DifferentDistribution(2),
    NotEnoughSamples(3),
    Bootstrap(4),
    None(-1);

    private final int resultNumber;

    ComparisonResult(int resultNumber){
        this.resultNumber = resultNumber;
    }
    public int getResultNumber(){
        return resultNumber;
    }

}
