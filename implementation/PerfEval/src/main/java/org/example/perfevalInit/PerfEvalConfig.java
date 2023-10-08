package org.example.perfevalInit;

import org.example.measurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;

public final class PerfEvalConfig {

    public static PerfEvalConfig getDefaultConfig() throws PerfEvalInvalidConfigException {
        return new PerfEvalConfig(false, new UniversalTimeUnit(1, TimeUnit.HOURS), 0.1,0.05, "UNKNOWN_VERSION");
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

    public final boolean gitFilePresence;
    public final UniversalTimeUnit maxTimeOnTest;
    public final double maxCIWidth;
    public final double critValue;
    public final String version;
}
