package cz.cuni.mff.d3s.perfeval.init;

import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.benchmarkdotnetjson.BenchmarkDotNetJSONParser;

/**
 * Class for storing configuration of PerfEval.
 */
public final class PerfEvalConfig {
    /**
     * Default value for gitFilePresence.
     */
    private static final boolean DEFAULT_GIT_PRESENCE = false;
    /**
     * Default value for maxTimeOnTest.
     */
    private static final int DEFAULT_MAX_TEST_COUNT = 100;
    /**
     * Default value for critValue.
     */
    private static final double DEFAULT_CRIT_VALUE = 0.05;
    /**
     * Default value for maxCIWidth.
     */
    private static final double DEFAULT_MAX_CI_WIDTH = 0.1;
    /**
     * Default value for version.
     */
    private static final String DEFAULT_VERSION = "UNKNOWN_VERSION";
    /**
     * Default value for parser.
     */
    private static final MeasurementParser DEFAULT_PARSER = new BenchmarkDotNetJSONParser();
    /**
     * Default value for tolerance.
     */
    private static final double DEFAULT_TOLERANCE = 0.05;

    /**
     * Getter for default configuration.
     *
     * @return default configuration
     * @throws PerfEvalInvalidConfigException if default configuration is invalid (it should not happen)
     */
    public static PerfEvalConfig getDefaultConfig() throws PerfEvalInvalidConfigException {
        return new PerfEvalConfig(DEFAULT_GIT_PRESENCE, DEFAULT_MAX_TEST_COUNT, DEFAULT_MAX_CI_WIDTH, DEFAULT_CRIT_VALUE, DEFAULT_VERSION, DEFAULT_PARSER, DEFAULT_TOLERANCE);
    }

    /**
     * Constructor for PerfEvalConfig.
     *
     * @param gitFilePresence if true, the project where PerfEval is initialized in is git repository
     * @param maxTestCount    maximal count of test that is possible to do
     * @param maxCIWidth      maximal width of confidence interval
     * @param critValue       critical value for statistical tests
     * @param version         version of performance tests that will be added
     * @param parser          parser for performance tests result files
     * @param tolerance       tolerance for performance tests
     * @throws PerfEvalInvalidConfigException if configuration is invalid
     */
    public PerfEvalConfig(boolean gitFilePresence, int maxTestCount, double maxCIWidth, double critValue, String version, MeasurementParser parser, double tolerance) throws PerfEvalInvalidConfigException {
        this.gitFilePresence = gitFilePresence;
        this.maxTestCount = maxTestCount;
        this.maxCIWidth = maxCIWidth;
        this.critValue = critValue;
        this.version = version;
        this.measurementParser = parser;
        this.tolerance = tolerance;
        if (critValue <= 0 || critValue >= 1) {
            throw new PerfEvalInvalidConfigException();
        }
        if (maxTestCount <= 0) {
            throw new PerfEvalInvalidConfigException();
        }
        if (maxCIWidth <= 0 || maxCIWidth >= 1) {
            throw new PerfEvalInvalidConfigException();
        }
        if (version == null) {
            throw new PerfEvalInvalidConfigException();
        }
        if (measurementParser == null) {
            throw new PerfEvalInvalidConfigException();
        }
        if (tolerance <= 0 || tolerance >= 1) {
            throw new PerfEvalInvalidConfigException();
        }
    }

    /**
     * If true, the project where PerfEval is initialized in is git repository.
     */
    private final boolean gitFilePresence;
    /**
     * Maximal time for one performance test.
     */
    private final int maxTestCount;
    /**
     * Maximal width of confidence interval (relative to average).
     */
    private final double maxCIWidth;
    /**
     * Critical value for statistical tests.
     */
    private final double critValue;
    /**
     * Version of performance tests that will be added.
     */
    private final String version;
    /**
     * Parser for performance tests result files.
     */
    private final MeasurementParser measurementParser;
    /**
     * Tolerance for performance tests.
     */
    private final double tolerance;

    /**
     * Getter for gitFilePresence.
     *
     * @return gitFilePresence
     */
    public boolean hasGitFilePresence() {
        return gitFilePresence;
    }

    /**
     * Getter for maxTimeOnTest.
     *
     * @return maxTimeOnTest
     */
    public int getMaxTestCount() {
        return maxTestCount;
    }

    /**
     * Getter for maxCIWidth.
     *
     * @return maxCIWidth
     */
    public double getMaxCIWidth() {
        return maxCIWidth;
    }

    /**
     * Getter for critValue.
     *
     * @return critValue
     */
    public double getCritValue() {
        return critValue;
    }

    /**
     * Getter for version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Getter for parser.
     *
     * @return parser
     */
    public MeasurementParser getMeasurementParser() {
        return measurementParser;
    }

    /**
     * Getter for tolerance.
     *
     * @return tolerance
     */
    public double getTolerance() {
        return tolerance;
    }
}
