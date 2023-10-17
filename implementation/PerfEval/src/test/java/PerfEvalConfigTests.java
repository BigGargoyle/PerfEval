import org.example.perfevalInit.PerfEvalConfig;
import org.example.perfevalInit.PerfEvalInvalidConfigException;
import org.example.measurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PerfEvalConfigTests {

    private PerfEvalConfig defaultConfig;
    private PerfEvalConfig validConfig;

    @BeforeEach
    public void setUp() throws PerfEvalInvalidConfigException {
        defaultConfig = PerfEvalConfig.getDefaultConfig();
        validConfig = new PerfEvalConfig(false, new UniversalTimeUnit(1, TimeUnit.HOURS), 0.1, 0.05, "VERSION");
    }

    @Test
    public void testDefaultConfigNotNull() {
        assertNotNull(defaultConfig);
    }

    @Test
    public void testDefaultConfigGitFilePresence() {
        assertFalse(defaultConfig.hasGitFilePresence());
    }

    @Test
    public void testDefaultConfigMaxTimeOnTest() {
        assertEquals((new UniversalTimeUnit(1, TimeUnit.HOURS)).getHours(), defaultConfig.getMaxTimeOnTest().getHours());
    }

    @Test
    public void testDefaultConfigCritValue() {
        assertEquals(0.05, defaultConfig.getCritValue(), 0.0);
    }

    @Test
    public void testDefaultConfigMaxCIWidth() {
        assertEquals(0.1, defaultConfig.getMaxCIWidth(), 0.0);
    }

    @Test
    public void testDefaultConfigVersion() {
        assertEquals("UNKNOWN_VERSION", defaultConfig.getVersion());
    }

    @Test
    public void testValidConfigNotNull() {
        assertNotNull(validConfig);
    }

    @Test
    public void testValidConfigGitFilePresence() {
        assertFalse(validConfig.hasGitFilePresence());
    }

    @Test
    public void testValidConfigMaxTimeOnTest() {
        assertEquals(new UniversalTimeUnit(1, TimeUnit.HOURS).getHours(), validConfig.getMaxTimeOnTest().getHours());
    }

    @Test
    public void testValidConfigCritValue() {
        assertEquals(0.05, validConfig.getCritValue());
    }

    @Test
    public void testValidConfigMaxCIWidth() {
        assertEquals(0.1, validConfig.getMaxCIWidth());
    }

    @Test
    public void testValidConfigVersion() {
        assertEquals("VERSION", validConfig.getVersion());
    }

    @Test
    public void testInvalidCritValue() {
        assertThrows(PerfEvalInvalidConfigException.class, ()->new PerfEvalConfig(false, new UniversalTimeUnit(1, TimeUnit.HOURS), -0.05, 0.1, "VERSION"));
    }

    @Test
    public void testInvalidMaxTimeOnTest() {
        assertThrows(PerfEvalInvalidConfigException.class, ()->new PerfEvalConfig(false, new UniversalTimeUnit(-1, TimeUnit.HOURS), 0.05, 0.1, "VERSION"));
    }

    @Test
    public void testInvalidMaxCIWidth() {
        assertThrows(PerfEvalInvalidConfigException.class, ()->new PerfEvalConfig(false, new UniversalTimeUnit(1, TimeUnit.HOURS), 0.0, 0.1, "VERSION"));
    }

    @Test
    public void testInvalidVersion() {
       assertThrows(PerfEvalInvalidConfigException.class, ()->new PerfEvalConfig(false, new UniversalTimeUnit(1, TimeUnit.HOURS), 0.05, 0.1, null));
    }
}
