package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.example.ResultDatabase.CacheDatabase;
import org.example.ResultDatabase.IDatabase;
import org.example.perfevalCLIEvaluator.PerfEvalEvaluator;
import org.example.perfevalConfig.ConfigManager;
import org.example.perfevalGraphicalEvaluator.GraphicalEvaluator;
import org.example.perfevalInit.PerfEvalInitializer;

public class Main {
    public static void main(String[] args) {
        if (contains(args, GlobalVars.helpFlag)) {
            helpCommandHandle();
            return;
        }
        if (args.length < 2) {
            System.err.println("No arguments found");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!(new File(args[0]).isDirectory())) {
            System.err.println(args[0] + " directory was not found");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        GlobalVars.workingDirectory = args[0];

        if (!perfEvalSetup() && !contains(args, GlobalVars.initCommand)) {
            System.err.println(GlobalVars.perfevalDir + " directory was not found");
            System.exit(GlobalVars.perfevalNotInitializedExitCode);
            return;
        }


        String command = args[1];
        switch (command) {
            case GlobalVars.initCommand -> initCommandHandle(args);
            case GlobalVars.evaluateCommand -> evaluateCommandHandle(args);
            case GlobalVars.indexNewCommand -> indexNewCommandHandle(args);
            case GlobalVars.indexAllCommand -> indexAllCommandHandle(args);
            case GlobalVars.undecidedCommand -> undecidedCommandHandle(args);
            case GlobalVars.configCommand -> configCommandHandle(args);
            default -> {
                System.err.println("Unknown argument:" + command);
                System.exit(GlobalVars.invalidArgumentsExitCode);
            }
        }
    }

    private static void initCommandHandle(String[] args) {
        if ((new File(GlobalVars.workingDirectory, GlobalVars.IniFileName).exists()) && !contains(args, GlobalVars.forceFlag)) {
            System.out.println("PerfEval already initialized.");
            return;
        }
        if (!PerfEvalInitializer.InitPerfEval()) {
            System.err.println("PerfEval cannot be initialized");
            System.exit(GlobalVars.perfevalNotInitializedExitCode);
            return;
        }
        System.out.println("PerfEval successfully initialized.");
    }

    static boolean perfEvalSetup() {
        File file = new File(GlobalVars.workingDirectory, GlobalVars.perfevalDir);
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
        File helpFile = new File(GlobalVars.workingDirectory, GlobalVars.helpFileName);
        if (!helpFile.exists() || !helpFile.isFile()) {
            System.err.println("Help file was not found in " + GlobalVars.perfevalDir + " directory");
            System.exit(GlobalVars.perfevalNotInitializedExitCode);
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
        if (contains(args, GlobalVars.graphicalFlag)) {
            if (!GraphicalEvaluator.evaluateGraphical(args)) {
                System.err.println("Graphical results cannot be evaluated");
            }
        } else if (!PerfEvalEvaluator.evaluateLastResults(initDatabase(), args)) {
            System.err.println("Evaluation of results failed");
            System.exit(GlobalVars.evaluationFailedExitCode);
        }
    }

    static void indexNewCommandHandle(String[] args) {
        IDatabase database = initDatabase();
        if (args.length != 3) {
            System.err.println("Unknown arguments");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!database.addFile(GlobalVars.workingDirectory+"/"+args[2])) {
            System.err.println("File cannot be added");
            System.exit(GlobalVars.databaseErrorExitCode);
        }
    }

    static void indexAllCommandHandle(String[] args) {
        IDatabase database = initDatabase();
        if (args.length != 3) {
            System.err.println("Unknown arguments");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!database.addFilesFromDir(GlobalVars.workingDirectory+"/"+args[2])) {
            System.err.println("Some of file/s in directory cannot be added");
            System.exit(GlobalVars.databaseErrorExitCode);
        }
    }

    static void undecidedCommandHandle(String[] args) {
        if (!PerfEvalEvaluator.listUndecidedTestResults(initDatabase(), args)) {
            System.err.println("Evaluation of results failed");
            System.exit(GlobalVars.evaluationFailedExitCode);
        }
    }

    static void configCommandHandle(String[] args) {
        if (args.length != 3) {
            System.err.println("Unknown arguments");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!ConfigManager.config(args)) {
            System.err.println("Arguments of configurations are not valid");
            System.exit(GlobalVars.invalidArgumentsExitCode);
        }
    }

    static IDatabase initDatabase() {
        return new CacheDatabase();
    }

}