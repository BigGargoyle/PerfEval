package MeasurementFactoryTests;

import org.junit.jupiter.api.Test;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class UniversalTImeUnitTest {

    @Test
    public void OneHourSimpleTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.HOURS);
        assertEquals(1,timeUnit.getHours());
        assertEquals(1,timeUnit.getTime(TimeUnit.HOURS));
    }
    @Test
    public void OneHourComplexTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.HOURS);
        assertEquals(1,timeUnit.getHours());
        assertEquals(1,timeUnit.getTime(TimeUnit.HOURS));

        assertEquals(60,timeUnit.getMinutes());
        assertEquals(60,timeUnit.getTime(TimeUnit.MINUTES));

        assertEquals(3600,timeUnit.getSeconds());
        assertEquals(3600,timeUnit.getTime(TimeUnit.SECONDS));
    }

    @Test
    public void OneSecondSimpleTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.SECONDS);
        assertEquals(1,timeUnit.getSeconds());
        assertEquals(1,timeUnit.getTime(TimeUnit.SECONDS));
    }
    @Test
    public void OneSecondComplexTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.SECONDS);
        assertEquals(1,timeUnit.getSeconds());
        assertEquals(1,timeUnit.getTime(TimeUnit.SECONDS));

        assertEquals(0,timeUnit.getMinutes());
        assertEquals(0,timeUnit.getTime(TimeUnit.MINUTES));

        assertEquals(1_000,timeUnit.getMilliSeconds());
        assertEquals(1_000,timeUnit.getTime(TimeUnit.MILLISECONDS));

        assertEquals(1_000_000,timeUnit.getMicroSeconds());
        assertEquals(1_000_000,timeUnit.getTime(TimeUnit.MICROSECONDS));

        assertEquals(1_000_000_000,timeUnit.getNanoSeconds());
        assertEquals(1_000_000_000,timeUnit.getTime(TimeUnit.NANOSECONDS));
    }

    @Test
    public void OneDayTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.DAYS);
        assertEquals(1,timeUnit.getDays());
        assertEquals(1,timeUnit.getTime(TimeUnit.DAYS));

        assertEquals(24, timeUnit.getHours());
        assertEquals(24, timeUnit.getTime(TimeUnit.HOURS));
    }

    @Test
    public void OneYearNoOverflowTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(365, TimeUnit.DAYS);
        assertEquals(365, timeUnit.getTime(TimeUnit.DAYS));
        assertEquals(365*24, timeUnit.getTime(TimeUnit.HOURS));
    }

    @Test
    public void FromSecondsToHours(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(119, TimeUnit.MINUTES);
        assertEquals(1, timeUnit.getHours());
        timeUnit.setTime(120, TimeUnit.MINUTES);
        assertEquals(2, timeUnit.getHours());
    }

    @Test
    public void NegativeDaysTest(){
        // test just to define expected behaviour of UniversalTimeUnit class
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(-3, TimeUnit.DAYS);
        assertEquals(-3, timeUnit.getDays());
        assertEquals(-72, timeUnit.getHours());
        assertEquals(-72*60, timeUnit.getMinutes());
    }


}
