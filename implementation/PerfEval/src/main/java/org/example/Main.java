package org.example;

import java.nio.file.Path;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.example.globalVariables.ExitCode;
import org.example.perfevalInit.InitCommand;
import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.perfevalInit.PerfEvalInvalidConfigException;

public class Main {
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


    public static final String perfevalDir = ".performance";
    public static final String gitIgnoreFileName = ".gitignore";
    public static final String helpFileName = "help.txt";
    public static final String iniFileName = "config.ini";
    public static final String DatabaseFileName = "test_results.db";
    public static final String DatabaseCacheFileName = "test_results_cache.db";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    public static final String forceFlag = "--force";

    public static void main(String[] args) {
        ExitCode exitCode;
        ICommand command = getCommand(args);
        try {
            if(command==null) exitCode=ExitCode.invalidArguments;
            else exitCode = command.execute();

        }catch (PerfEvalCommandFailedException e){
            System.err.println(e.toString());
            exitCode = e.exitCode;
        }
        exitCode.exit();
    }

    private static ICommand getCommand(String[] args) {
        OptionParser parser = CreateParser();
        OptionSet options = parser.parse(args);
        Path iniFilePath = Path.of(args[0]).resolve(perfevalDir).resolve(iniFileName);
        PerfEvalConfig config = null;
        try {
            config = InitCommand.getConfig(iniFilePath);
        } catch (PerfEvalInvalidConfigException e) {
            //TODO: zlepšit
            return null;
        }

        for(var arg : options.nonOptionArguments()){
            if(arg==initCommand) return setupInitCommand(args, options, config);
            if(arg==evaluateCommand) return setupEvaluateCommand(args, options, iniFilePath);
            if(arg==indexNewCommand) return setupIndexNewCommand(args, options, iniFilePath);
            if(arg==indexAllCommand) return setupIndexAllCommand(args, options, iniFilePath);
            if(arg==undecidedCommand) return setupUndecidedCommand(args, options, iniFilePath);
        }
        return null;
    }

    private static ICommand setupUndecidedCommand(String[] args, OptionSet options, Path config) {
        return null;
    }

    private static ICommand setupIndexAllCommand(String[] args, OptionSet options, Path config) {
        return null;
    }

    private static ICommand setupIndexNewCommand(String[] args, OptionSet options, Path config) {
        return null;
    }

    private static ICommand setupEvaluateCommand(String[] args, OptionSet options, Path config) {
        return null;
    }

    private static ICommand setupInitCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path workingDir = Path.of(args[0]);
        Path perfevalDirPath = workingDir.resolve(perfevalDir);
        Path gitIgnorePath = perfevalDirPath.resolve(gitIgnoreFileName);
        Path iniFilePath = perfevalDirPath.resolve(iniFileName);
        Path helpFilePath = perfevalDirPath.resolve(helpFileName);
        //TODO: dodat
        String helpFileContent = "";
        Path[] emptyFiles = new Path[]{perfevalDirPath.resolve(DatabaseFileName), perfevalDirPath.resolve(DatabaseCacheFileName)};
        Path[] gitIgnoredFiles = new Path[] {
                iniFilePath,
                helpFilePath,
                emptyFiles[0],
                emptyFiles[1]
        };
        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath ,helpFilePath, helpFileContent,
                emptyFiles, gitIgnoredFiles, config, options.has(forceFlag));
    }

    private static OptionParser CreateParser(){
        OptionParser parser = new OptionParser();

        // Define options with arguments
        ArgumentAcceptingOptionSpec<String> filterOption = parser.accepts("filter")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Filter option with a parameter");
        ArgumentAcceptingOptionSpec<String> maxTimeOption = parser.accepts("max-time")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Max time option with a duration parameter");

        // Define flags (options without arguments)
        parser.accepts("help", "Print help message");
        parser.accepts("graphical", "Enable graphical mode");
        parser.accepts("json-output", "Enable JSON output");
        parser.accepts(forceFlag, "Force the operation");
        return parser;
    }

}