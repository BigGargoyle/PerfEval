package org.example;

import org.example.MeasurementFactory.UniversalTimeUnit;

import java.util.concurrent.TimeUnit;

public class GlobalVars {
    public static final String commentSign = "#";
    public static final String critValueSign = "P";
    public static final String maxCIWidthSign = "C";
    public static final String maxTimeOnTestSign = "T";
    public static final String ColumnDelimiter = "\t";
    public static final double defaultStatisticCritValue = 0.05;
    public static final double defaultMaxCIWidth = 0.1;
    public static final UniversalTimeUnit defaultMaxTimeOnTest = new UniversalTimeUnit(1,TimeUnit.HOURS);

    public static final String perfevalDir = ".performance";
    public static final String helpFileName = "help.txt";
    public static final String IniFileName = "config.ini";

    // commands
    public static final String initCommand = "init";
    public static final String evaluateCommand = "evaluate";
    public static final String indexNewCommand = "index-new-result";
    public static final String indexAllCommand = "index-all-results";
    public static final String undecidedCommand = "list-undecided";

    // flags
    public static final String helpFlag = "--help";
    public static final String graphicalFlag = "--graphical";
    public static final String jsonOutputFlag = "--json-output";

    // possible params
    public static final String maxTimeForTestParameter = "--max-time";

    // exit codes
    public static final int atLeastOneWorseResultExitCode = 1;
    public static final int invalidArgumentsExitCode = 2;
    public static final int perfevalNotInitializedExitCode = 3;

    private GlobalVars(){}
}
