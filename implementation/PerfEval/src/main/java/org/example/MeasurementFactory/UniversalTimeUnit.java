package org.example.MeasurementFactory;

import java.util.concurrent.TimeUnit;

public class UniversalTimeUnit {

    // max value of 64-bit long is able to contain nanoseconds of up to ~10^4 days

    static int ONE_THOUSAND = 1000;
    static int SIXTY = 60;
    static int TWENTY_FOUR = 24;

    long nanoseconds;

    public UniversalTimeUnit(long value, TimeUnit timeUnit) {
        SetTime(value, timeUnit);
    }

    public void SetTime(long value, TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS -> nanoseconds = value;
            case MICROSECONDS -> SetTime(value * ONE_THOUSAND, TimeUnit.NANOSECONDS);
            case MILLISECONDS -> SetTime(value * ONE_THOUSAND, TimeUnit.MICROSECONDS);
            case SECONDS -> SetTime(value * ONE_THOUSAND, TimeUnit.MILLISECONDS);
            case MINUTES -> SetTime(value * SIXTY, TimeUnit.SECONDS);
            case HOURS -> SetTime(value * SIXTY, TimeUnit.MINUTES);
            case DAYS -> SetTime(value * TWENTY_FOUR, TimeUnit.HOURS);
        }
    }

    public long GetTime(TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS -> {
                return nanoseconds;
            }
            case MICROSECONDS -> {
                return GetTime(TimeUnit.NANOSECONDS) / ONE_THOUSAND;
            }
            case MILLISECONDS -> {
                return GetTime(TimeUnit.MICROSECONDS) / ONE_THOUSAND;
            }
            case SECONDS -> {
                return GetTime(TimeUnit.MILLISECONDS) / ONE_THOUSAND;
            }
            case MINUTES -> {
                return GetTime(TimeUnit.SECONDS) / SIXTY;
            }
            case HOURS -> {
                return GetTime(TimeUnit.MINUTES) / SIXTY;
            }
            case DAYS -> {
                return GetTime(TimeUnit.HOURS) / TWENTY_FOUR;
            }
        }
        return -1;
    }

    public long GetNanoSeconds() {
        return GetTime(TimeUnit.NANOSECONDS);
    }

    public long GetMicroSeconds() {
        return GetTime(TimeUnit.MICROSECONDS);
    }

    public long GetMilliSeconds() {
        return GetTime(TimeUnit.MILLISECONDS);
    }

    public long GetSeconds() {
        return GetTime(TimeUnit.SECONDS);
    }

    public long GetMinutes() {
        return GetTime(TimeUnit.MINUTES);
    }

    public long GetHours() {
        return GetTime(TimeUnit.HOURS);
    }

    public long GetDays() {
        return GetTime(TimeUnit.DAYS);
    }
}
