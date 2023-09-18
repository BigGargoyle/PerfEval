package org.example.measurementFactory;

import java.util.concurrent.TimeUnit;

/**
 * Class used to unify timeunits through the whole program
 * Can represent time interval in multiple different time units (ns, us, ms, s, min, h, d)
 * Can represent time interval with length up to ~10^4 days
 */
public class UniversalTimeUnit {

    // max value of 64-bit long is able to contain nanoseconds of up to ~10^4 days

    /**
     * Number used to recalculation between time units (ns, us, ms, s)
     */
    static final int ONE_THOUSAND = 1000;
    /**
     * Number used to recalculation between time units (s, min, h)
     */
    static final int SIXTY = 60;

    /**
     * Number used to recalculation between time units (h, d)
     */
    static final int TWENTY_FOUR = 24;

    /**
     * Field used to store time in ns (smallest possible time unit)
     */
    long nanoseconds;

    /**
     * Creates an instance of TimeUnit
     *
     * @param value    Length of time interval that the new instance of UniversalTimeUnit will be representing
     * @param timeUnit TimeUnit of the value
     */
    public UniversalTimeUnit(long value, TimeUnit timeUnit) {
        setTime(value, timeUnit);
    }

    /**
     * Used for changing time interval of an existing instance of UniversalTimeUnit
     *
     * @param value    Length of time interval that the new instance of TimeUnit will be representing
     * @param timeUnit TimeUnit of the value
     */
    public void setTime(long value, TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS -> nanoseconds = value;
            case MICROSECONDS -> setTime(value * ONE_THOUSAND, TimeUnit.NANOSECONDS);
            case MILLISECONDS -> setTime(value * ONE_THOUSAND, TimeUnit.MICROSECONDS);
            case SECONDS -> setTime(value * ONE_THOUSAND, TimeUnit.MILLISECONDS);
            case MINUTES -> setTime(value * SIXTY, TimeUnit.SECONDS);
            case HOURS -> setTime(value * SIXTY, TimeUnit.MINUTES);
            case DAYS -> setTime(value * TWENTY_FOUR, TimeUnit.HOURS);
        }
    }

    /**
     * Used for get time interval that the instance of UniversalTimeUnit is representing
     *
     * @param timeUnit TimeUnit of returned value
     * @return length of time interval that this instance is representing in the TimeUnit time unit
     */
    public long getTime(TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS -> {
                return nanoseconds;
            }
            case MICROSECONDS -> {
                return getTime(TimeUnit.NANOSECONDS) / ONE_THOUSAND;
            }
            case MILLISECONDS -> {
                return getTime(TimeUnit.MICROSECONDS) / ONE_THOUSAND;
            }
            case SECONDS -> {
                return getTime(TimeUnit.MILLISECONDS) / ONE_THOUSAND;
            }
            case MINUTES -> {
                return getTime(TimeUnit.SECONDS) / SIXTY;
            }
            case HOURS -> {
                return getTime(TimeUnit.MINUTES) / SIXTY;
            }
            case DAYS -> {
                return getTime(TimeUnit.HOURS) / TWENTY_FOUR;
            }
        }
        return -1;
    }

    /**
     * @return time value represented by this instance in nanoseconds
     */
    public long getNanoSeconds() {
        return getTime(TimeUnit.NANOSECONDS);
    }

    /**
     * @return time value represented by this instance in microseconds
     */
    public long getMicroSeconds() {
        return getTime(TimeUnit.MICROSECONDS);
    }

    /**
     * @return time value represented by this instance in milliseconds
     */
    public long getMilliSeconds() {
        return getTime(TimeUnit.MILLISECONDS);
    }

    /**
     * @return time value represented by this instance in seconds
     */
    public long getSeconds() {
        return getTime(TimeUnit.SECONDS);
    }

    /**
     * @return time value represented by this instance in minutes
     */
    public long getMinutes() {
        return getTime(TimeUnit.MINUTES);
    }

    /**
     * @return time value represented by this instance in hours
     */
    public long getHours() {
        return getTime(TimeUnit.HOURS);
    }

    /**
     * @return time value represented by this instance in days
     */
    public long getDays() {
        return getTime(TimeUnit.DAYS);
    }
}
