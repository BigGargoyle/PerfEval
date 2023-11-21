package cz.cuni.mff.d3s.perfeval.init;

import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;

import java.time.Duration;

public final class PerfEvalConfig {
    private static final boolean DEFAULT_GIT_PRESENCE = false;
    private static final Duration DEFAULT_MAX_TIME_ON_TEST = Duration.ofHours(1);
    private static final double DEFAULT_CRIT_VALUE = 0.05;
    private static final double DEFAULT_MAX_CI_WIDTH = 0.1;
    private static final String DEFAULT_VERSION = "UNKNOWN_VERSION";
    private static final MeasurementParser DEFAULT_PARSER = new BenchmarkDotNetJSONParser();
    private static final double DEFAULT_TOLERANCE = 0.05;

    public static PerfEvalConfig getDefaultConfig() throws PerfEvalInvalidConfigException {
        return new PerfEvalConfig(DEFAULT_GIT_PRESENCE, DEFAULT_MAX_TIME_ON_TEST, DEFAULT_MAX_CI_WIDTH,DEFAULT_CRIT_VALUE, DEFAULT_VERSION, DEFAULT_PARSER, DEFAULT_TOLERANCE);
    }

    public PerfEvalConfig(boolean gitFilePresence, Duration maxTimeOnTest, double maxCIWidth, double critValue, String version, MeasurementParser parser, double tolerance) throws PerfEvalInvalidConfigException {
        this.gitFilePresence = gitFilePresence;
        this.maxTimeOnTest = maxTimeOnTest;
        this.maxCIWidth = maxCIWidth;
        this.critValue = critValue;
        this.version = version;
        this.measurementParser = parser;
        this.tolerance = tolerance;
        if (critValue <= 0 || critValue >= 1) throw new PerfEvalInvalidConfigException();
        if (maxTimeOnTest.toNanos() < 0) throw new PerfEvalInvalidConfigException();
        if (maxCIWidth <= 0 || maxCIWidth >= 1) throw new PerfEvalInvalidConfigException();
        if (version == null) throw new PerfEvalInvalidConfigException();
        if (measurementParser == null) throw new PerfEvalInvalidConfigException();
        if (tolerance <= 0 || tolerance >= 1) throw new PerfEvalInvalidConfigException();
    }

    private final boolean gitFilePresence;
    private final Duration maxTimeOnTest;
    private final double maxCIWidth;
    private final double critValue;
    private final String version;
    private final MeasurementParser measurementParser;
    private final double tolerance;
    public boolean hasGitFilePresence() {
        return gitFilePresence;
    }

    public Duration getMaxTimeOnTest() {
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
    public MeasurementParser getMeasurementParser() {
        return measurementParser;
    }
    public double getTolerance() {
        return tolerance;
    }
}
