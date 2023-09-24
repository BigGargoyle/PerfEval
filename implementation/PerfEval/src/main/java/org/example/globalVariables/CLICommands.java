package org.example.globalVariables;

public class CLICommands {
    // commands
    /**
     * Command for PerfEval system initialization. It creates .performance directory inside current directory.
     */
    public static final String initCommand = "init";
    /**
     * Command for evaluating last two measurement results.
     */
    public static final String evaluateCommand = "evaluate";
    /**
     * Command for inserting a new measurement (single file specified as a next argument) result inside directory.
     */
    public static final String indexNewCommand = "index-new-result";
    /**
     * Command for inserting all files inside this directory (specified as a next argument) and its subdirectories as new measurement result files.
     */
    public static final String indexAllCommand = "index-all-results";
    /**
     * Command for listing all test names that has too few measurements for a statistical test.
     */
    public static final String undecidedCommand = "list-undecided";
    /**
     * Command for changing configuration of PerfEval system.
     */
    public static final String configCommand = "config";
}
