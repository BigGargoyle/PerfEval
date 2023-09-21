package org.example.globalVariables;

import org.example.measurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;

public class DefaultIniValues {
    public static final double defaultStatisticCritValue = 0.05;
    public static final double defaultMaxCIWidth = 0.1;
    public static final UniversalTimeUnit defaultMaxTimeOnTest = new UniversalTimeUnit(1, TimeUnit.HOURS);
}
