package org.example;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;

public class GlobalVars {
    public static final String commentSign = "#";
    public static final String critValueSign = "P";
    public static final String maxCIWidthSign = "C";
    public static final String maxTimeOnTestSign = "T";
    public static final String gitSign = "G";
    public static final String versionSign = "V";
    public static final String DatabaseItemIdentifier = "R";
    public static final String ColumnDelimiter = "\t";
    public static final double defaultStatisticCritValue = 0.05;
    public static final double defaultMaxCIWidth = 0.1;
    public static final UniversalTimeUnit defaultMaxTimeOnTest = new UniversalTimeUnit(1, TimeUnit.HOURS);

    public static final String TrueString = "TRUE";
    public static final String FalseString = "FALSE";
    public static final String UnknownVersion = "UNKNOWN VERSION";

    public static final String gitFileDir = ".";
    public static final String gitFileName = ".git";
    public static final String gitIgnoreFileName = ".performance/.gitignore";
    public static final String perfevalDir = ".performance";
    public static final String helpFileName = "help.txt";
    public static final String IniFileName = ".performance/config.ini";
    public static final String DatabaseFileName = ".performance/test_results.db";
    public static final String DatabaseCacheFileName = ".performance/test_results_cache.db";

    // commands
    public static final String initCommand = "init";
    public static final String evaluateCommand = "evaluate";
    public static final String indexNewCommand = "index-new-result";
    public static final String indexAllCommand = "index-all-results";
    public static final String undecidedCommand = "list-undecided";
    public static final String configCommand = "config";

    // flags
    public static final String helpFlag = "--help";
    public static final String graphicalFlag = "--graphical";
    public static final String jsonOutputFlag = "--json-output";
    public static final String forceFlag = "--force";
    public static final String filterParam = "--filter";

    // possible params
    // TODO: accept this param
    public static final String maxTimeForTestParameter = "--max-time";

    // exit codes
    public static final int OKExitCode = 0;
    public static final int atLeastOneWorseResultExitCode = 1;
    public static final int invalidArgumentsExitCode = 2;
    public static final int perfevalNotInitializedExitCode = 3;
    public static final int evaluationFailedExitCode = 4;
    public static final int databaseErrorExitCode = 5;

    private GlobalVars() {}
}