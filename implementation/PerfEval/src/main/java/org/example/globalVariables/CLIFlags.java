package org.example.globalVariables;

/**
 * Flags typed inside command line as arguments after Perfeval command
 */
public class CLIFlags {

    // flags
    /**
     * Help message will be printed to standard output.
     */
    public static final String helpFlag = "--help";
    /**
     * Only for use with evaluate command. The website with graphical output will be created.
     */
    public static final String graphicalFlag = "--graphical";
    /**
     * Only for use with evaluate command. The output in JSON format will be printed.
     */
    public static final String jsonOutputFlag = "--json-output";

    // possible params
    /**
     * Only for use with evaluate command. Results will be printed with respect to a filtered value.
     */
    public static final String filterParam = "--filter";
    /**
     * Only for use with evaluate and list-undecided command. It gives an information of maximum time on the one performance test to the system.
     */
    public static final String maxTimeForTestParameter = "--max-time";
}
