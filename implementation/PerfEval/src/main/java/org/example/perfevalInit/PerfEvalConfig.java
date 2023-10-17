package org.example.perfevalInit;

import org.example.measurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;

public final class PerfEvalConfig {
    private static final boolean DEFAULT_GIT_PRESENCE = false;
    private static final UniversalTimeUnit DEFAULT_MAX_TIME_ON_TEST = new UniversalTimeUnit(1, TimeUnit.HOURS);
    private static final double DEFAULT_CRIT_VALUE = 0.05;
    private static final double DEFAULT_MAX_CI_WIDTH = 0.1;
    private static final String DEFAULT_VERSION = "UNKNOWN_VERSION";

    public static PerfEvalConfig getDefaultConfig() throws PerfEvalInvalidConfigException {
        return new PerfEvalConfig(DEFAULT_GIT_PRESENCE, DEFAULT_MAX_TIME_ON_TEST, DEFAULT_MAX_CI_WIDTH,DEFAULT_CRIT_VALUE, DEFAULT_VERSION);
    }

    public PerfEvalConfig(boolean gitFilePresence, UniversalTimeUnit maxTimeOnTest, double maxCIWidth, double critValue, String version) throws PerfEvalInvalidConfigException {
        this.gitFilePresence = gitFilePresence;
        this.maxTimeOnTest = maxTimeOnTest;
        this.maxCIWidth = maxCIWidth;
        this.critValue = critValue;
        this.version = version;
        if (critValue <= 0 || critValue >= 1) throw new PerfEvalInvalidConfigException();
        if (maxTimeOnTest.getNanoSeconds() < 0) throw new PerfEvalInvalidConfigException();
        if (maxCIWidth <= 0 || maxCIWidth >= 1) throw new PerfEvalInvalidConfigException();
        if (version == null) throw new PerfEvalInvalidConfigException();
    }

    private final boolean gitFilePresence;
    private final UniversalTimeUnit maxTimeOnTest;
    private final double maxCIWidth;
    private final double critValue;
    private final String version;
    public boolean hasGitFilePresence() {
        return gitFilePresence;
    }

    public UniversalTimeUnit getMaxTimeOnTest() {
        return maxTimeOnTest;
    }

    public double getMaxCIWidth() {
        return maxCIWidth;
    }

    public double getCritValue() {
        return critValue;
    }

    public String getVersion() {
        return version;
    }
}
