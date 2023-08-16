package MeasurementFactoryTests;

import org.junit.jupiter.api.Test;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class UniversalTImeUnitTest {

    @Test
    public void OneHourSimpleTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.HOURS);
        assertEquals(1,timeUnit.GetHours());
        assertEquals(1,timeUnit.GetTime(TimeUnit.HOURS));
    }
    @Test
    public void OneHourComplexTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.HOURS);
        assertEquals(1,timeUnit.GetHours());
        assertEquals(1,timeUnit.GetTime(TimeUnit.HOURS));

        assertEquals(60,timeUnit.GetMinutes());
        assertEquals(60,timeUnit.GetTime(TimeUnit.MINUTES));

        assertEquals(3600,timeUnit.GetSeconds());
        assertEquals(3600,timeUnit.GetTime(TimeUnit.SECONDS));
    }

    @Test
    public void OneSecondSimpleTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.SECONDS);
        assertEquals(1,timeUnit.GetSeconds());
        assertEquals(1,timeUnit.GetTime(TimeUnit.SECONDS));
    }
    @Test
    public void OneSecondComplexTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.SECONDS);
        assertEquals(1,timeUnit.GetSeconds());
        assertEquals(1,timeUnit.GetTime(TimeUnit.SECONDS));

        assertEquals(0,timeUnit.GetMinutes());
        assertEquals(0,timeUnit.GetTime(TimeUnit.MINUTES));

        assertEquals(1_000,timeUnit.GetMilliSeconds());
        assertEquals(1_000,timeUnit.GetTime(TimeUnit.MILLISECONDS));

        assertEquals(1_000_000,timeUnit.GetMicroSeconds());
        assertEquals(1_000_000,timeUnit.GetTime(TimeUnit.MICROSECONDS));

        assertEquals(1_000_000_000,timeUnit.GetNanoSeconds());
        assertEquals(1_000_000_000,timeUnit.GetTime(TimeUnit.NANOSECONDS));
    }

    @Test
    public void OneDayTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(1, TimeUnit.DAYS);
        assertEquals(1,timeUnit.GetDays());
        assertEquals(1,timeUnit.GetTime(TimeUnit.DAYS));

        assertEquals(24, timeUnit.GetHours());
        assertEquals(24, timeUnit.GetTime(TimeUnit.HOURS));
    }

    @Test
    public void OneYearNoOverflowTest(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(365, TimeUnit.DAYS);
        assertEquals(365, timeUnit.GetTime(TimeUnit.DAYS));
        assertEquals(365*24, timeUnit.GetTime(TimeUnit.HOURS));
    }

    @Test
    public void FromSecondsToHours(){
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(119, TimeUnit.MINUTES);
        assertEquals(1, timeUnit.GetHours());
        timeUnit.SetTime(120, TimeUnit.MINUTES);
        assertEquals(2, timeUnit.GetHours());
    }

    @Test
    public void NegativeDaysTest(){
        // test just to define expected behaviour of UniversalTimeUnit class
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(-3, TimeUnit.DAYS);
        assertEquals(-3, timeUnit.GetDays());
        assertEquals(-72, timeUnit.GetHours());
        assertEquals(-72*60, timeUnit.GetMinutes());
    }


}
