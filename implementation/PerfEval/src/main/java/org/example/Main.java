package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.example.GlobalVariables.CLICommands;
import org.example.GlobalVariables.CLIFlags;
import org.example.GlobalVariables.FileNames;
import org.example.ResultDatabase.CacheDatabase;
import org.example.ResultDatabase.IDatabase;
import org.example.perfevalCLIEvaluator.PerfEvalEvaluator;
import org.example.perfevalConfig.ConfigManager;
import org.example.perfevalGraphicalEvaluator.GraphicalEvaluator;
import org.example.perfevalInit.PerfEvalInitializer;
import org.example.GlobalVariables.ExitCode;

public class Main {
    public static void main(String[] args) {
        if (contains(args, CLIFlags.helpFlag)) {
            helpCommandHandle();
            return;
        }
        if (args.length < 2) {
            System.err.println("No arguments found");
            System.exit(ExitCode.invalidArguments.getExitCode());
            return;
        }
        if (!(new File(args[0]).isDirectory())) {
            System.err.println(args[0] + " directory was not found");
            System.exit(ExitCode.invalidArguments.getExitCode());
            return;
        }
        FileNames.workingDirectory = args[0];

        if (!perfEvalSetup() && !contains(args, CLICommands.initCommand)) {
            System.err.println(FileNames.perfevalDir + " directory was not found");
            System.exit(ExitCode.notInitialized.getExitCode());
            return;
        }


        String command = args[1];
        switch (command) {
            case CLICommands.initCommand -> initCommandHandle(args);
            case CLICommands.evaluateCommand -> evaluateCommandHandle(args);
            case CLICommands.indexNewCommand -> indexNewCommandHandle(args);
            case CLICommands.indexAllCommand -> indexAllCommandHandle(args);
            case CLICommands.undecidedCommand -> undecidedCommandHandle(args);
            case CLICommands.configCommand -> configCommandHandle(args);
            default -> {
                System.err.println("Unknown argument:" + command);
                System.exit(ExitCode.invalidArguments.getExitCode());
            }
        }
    }

    private static void initCommandHandle(String[] args) {
        if ((new File(FileNames.workingDirectory, FileNames.IniFileName).exists()) && !contains(args, CLIFlags.forceFlag)) {
            System.out.println("PerfEval already initialized.");
            return;
        }
        if (!PerfEvalInitializer.InitPerfEval()) {
            System.err.println("PerfEval cannot be initialized");
            System.exit(ExitCode.notInitialized.getExitCode());
            return;
        }
        System.out.println("PerfEval successfully initialized.");
    }

    static boolean perfEvalSetup() {
        File file = new File(FileNames.workingDirectory, FileNames.perfevalDir);
        return file.exists() && file.isDirectory();
    }

    static boolean contains(String[] container, String item) {
        for (String element : container) {
            if (element.compareTo(item) == 0)
                return true;
        }
        return false;
    }

    static void helpCommandHandle() {
        File helpFile = new File(FileNames.workingDirectory, FileNames.helpFileName);
        if (!helpFile.exists() || !helpFile.isFile()) {
            System.err.println("Help file was not found in " + FileNames.perfevalDir + " directory");
            System.exit(ExitCode.notInitialized.getExitCode());
            return;
        }
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(helpFile));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void evaluateCommandHandle(String[] args) {
        if (contains(args, CLIFlags.graphicalFlag)) {
            if (!GraphicalEvaluator.evaluateGraphical(args)) {
                System.err.println("Graphical results cannot be evaluated");
            }
        } else if (!PerfEvalEvaluator.evaluateLastResults(initDatabase(), args)) {
            System.err.println("Evaluation of results failed");
            System.exit(ExitCode.evaluationFailed.getExitCode());
        }
    }

    static void indexNewCommandHandle(String[] args) {
        IDatabase database = initDatabase();
        if (args.length != 3) {
            System.err.println("Unknown arguments");
            System.exit(ExitCode.invalidArguments.getExitCode());
            return;
        }
        if (!database.addFile(FileNames.workingDirectory+"/"+args[2])) {
            System.err.println("File cannot be added");
            System.exit(ExitCode.databaseError.getExitCode());
        }
    }

    static void indexAllCommandHandle(String[] args) {
        IDatabase database = initDatabase();
        if (args.length != 3) {
            System.err.println("Unknown arguments");
            System.exit(ExitCode.invalidArguments.getExitCode());
            return;
        }
        if (!database.addFilesFromDir(FileNames.workingDirectory+"/"+args[2])) {
            System.err.println("Some of file/s in directory cannot be added");
            System.exit(ExitCode.databaseError.getExitCode());
        }
    }

    static void undecidedCommandHandle(String[] args) {
        if (!PerfEvalEvaluator.listUndecidedTestResults(initDatabase(), args)) {
            System.err.println("Evaluation of results failed");
            System.exit(ExitCode.evaluationFailed.getExitCode());
        }
    }

    static void configCommandHandle(String[] args) {
        if (args.length != 3) {
            System.err.println("Unknown arguments");
            System.exit(ExitCode.invalidArguments.getExitCode());
            return;
        }
        if (!ConfigManager.config(args)) {
            System.err.println("Arguments of configurations are not valid");
            System.exit(ExitCode.invalidArguments.getExitCode());
        }
    }

    static IDatabase initDatabase() {
        return new CacheDatabase();
    }

}