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
     * Default value for minTestCount.
     */
    private static final int DEFAULT_MIN_TEST_COUNT = 5;

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
        return new PerfEvalConfig(DEFAULT_GIT_PRESENCE, DEFAULT_MIN_TEST_COUNT, DEFAULT_MAX_TEST_COUNT, DEFAULT_MAX_CI_WIDTH, DEFAULT_CRIT_VALUE, DEFAULT_PARSER, DEFAULT_TOLERANCE);
    }

    /**
     * Constructor for PerfEvalConfig.
     *
     * @param gitFilePresence if true, the project where PerfEval is initialized in is git repository
     * @param maxTestCount    maximal count of test that is possible to do
     * @param maxCIWidth      maximal width of confidence interval
     * @param critValue       critical value for statistical tests
     * @param parser          parser for performance tests result files
     * @param tolerance       tolerance for performance tests
     * @throws PerfEvalInvalidConfigException if configuration is invalid
     */
    public PerfEvalConfig(boolean gitFilePresence, int minTestCount, int maxTestCount, double maxCIWidth, double critValue, MeasurementParser parser, double tolerance) throws PerfEvalInvalidConfigException {
        this.gitFilePresence = gitFilePresence;
        this.minTestCount = minTestCount;
        this.maxTestCount = maxTestCount;
        this.maxCIWidth = maxCIWidth;
        this.critValue = critValue;
        this.measurementParser = parser;
        this.tolerance = tolerance;
        if (critValue <= 0 || critValue >= 1) {
            throw new PerfEvalInvalidConfigException("Critical value for statistical tests must be in (0, 1).");
        }
        if(minTestCount < 0) {
            throw new PerfEvalInvalidConfigException("Minimal count of tests must be non-negative.");
        }
        if(minTestCount > maxTestCount) {
            throw new PerfEvalInvalidConfigException("Minimal count of tests must be less than maximal count of tests.");
        }
        if (maxTestCount <= 0) {
            throw new PerfEvalInvalidConfigException("Maximal count of tests must be positive.");
        }
        if (maxCIWidth <= 0 || maxCIWidth >= 1) {
            throw new PerfEvalInvalidConfigException("Maximal width of confidence interval must be in (0, 1).");
        }
        if (measurementParser == null) {
            throw new PerfEvalInvalidConfigException("Parser must be set.");
        }
        if (tolerance <= 0 || tolerance >= 1) {
            throw new PerfEvalInvalidConfigException("Tolerance must be in (0, 1).");
        }
    }

    /**
     * If true, the project where PerfEval is initialized in is git repository.
     */
    private final boolean gitFilePresence;
    /**
     * Minimal time for one performance test.
     */
    private final int minTestCount;
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
     * Parser for performance tests result files.
     * It is not final because it can be set later (init command).
     */
    private MeasurementParser measurementParser;
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
     * Getter for parser.
     *
     * @return parser
     */
    public MeasurementParser getMeasurementParser() {
        return measurementParser;
    }

    /**
     * Setter for parser.
     *
     * @param parser new parser
     */
    public void setMeasurementParser(MeasurementParser parser) throws PerfEvalInvalidConfigException {
        if(parser == null) {
            throw new PerfEvalInvalidConfigException("Unknown parser name.");
        }
        this.measurementParser = parser;
    }

    /**
     * Getter for tolerance.
     *
     * @return tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Getter for minTestCount.
     * @return minTestCount
     */
    public int getMinTestCount() {
        return minTestCount;
    }
}
