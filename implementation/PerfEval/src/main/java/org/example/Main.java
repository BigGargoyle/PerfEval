package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.example.ResultDatabase.CacheDatabase;
import org.example.ResultDatabase.DumbDatabase;
import org.example.ResultDatabase.IDatabase;
import org.example.perfevalCLIEvaluator.PerfEvalEvaluator;
import org.example.perfevalInit.PerfEvalInitializer;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No arguments found");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!perfEvalSetup() && !Contains(args, GlobalVars.initCommand)) {
            System.err.println(GlobalVars.perfevalDir + " directory was not found");
            System.exit(GlobalVars.perfevalNotInitializedExitCode);
            return;
        }
        if (Contains(args, GlobalVars.helpFlag)) {
            HelpCommandHandle(args);
            return;
        }

        switch (args[0]) {
            case GlobalVars.initCommand -> InitCommandHandle(args);
            case GlobalVars.evaluateCommand -> EvaluateCommandHandle(args);
            case GlobalVars.indexNewCommand -> IndexNewCommandHandle(args);
            case GlobalVars.indexAllCommand -> IndexAllCommandHandle(args);
            case GlobalVars.undecidedCommand -> UndecidedCommandHandle(args);
            default -> {
                System.err.println("Unknown argument:" + args[0]);
                System.exit(GlobalVars.invalidArgumentsExitCode);
            }
        }
    }

    private static void InitCommandHandle(String[] args) {
        if ((new File(GlobalVars.perfevalDir, GlobalVars.IniFileName).exists()) && !Contains(args, GlobalVars.forceFlag)) {
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
        File file = new File(GlobalVars.perfevalDir);
        return file.exists() && file.isDirectory();
    }

    static boolean Contains(String[] container, String item) {
        for (String element : container) {
            if (element.compareTo(item) == 0)
                return true;
        }
        return false;
    }

    static void HelpCommandHandle(String[] args) {
        File helpFile = new File(GlobalVars.perfevalDir + "/" + GlobalVars.helpFileName);
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

    static void EvaluateCommandHandle(String[] args) {
        if (!PerfEvalEvaluator.EvaluateLastResults(InitDatabase(), args)) {
            System.err.println("Evaluation of results failed");
            System.exit(GlobalVars.evaluationFailedExitCode);
        }
    }

    static void IndexNewCommandHandle(String[] args) {
        IDatabase database = InitDatabase();
        if(args.length != 2){
            System.err.println("Unknown arguments");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!database.AddFile(args[1])) {
            System.err.println("File cannot be added");
            System.exit(GlobalVars.databaseErrorExitCode);
        }
    }

    static void IndexAllCommandHandle(String[] args) {
        IDatabase database = InitDatabase();
        if(args.length != 2){
            System.err.println("Unknown arguments");
            System.exit(GlobalVars.invalidArgumentsExitCode);
            return;
        }
        if (!database.AddFilesFromDir(args[1])) {
            System.err.println("Some of file/s in directory cannot be added");
            System.exit(GlobalVars.databaseErrorExitCode);
        }
    }

    static void UndecidedCommandHandle(String[] args) {
        if (!PerfEvalEvaluator.ListUndecidedTestResults(InitDatabase(), args)) {
            System.err.println("Evaluation of results failed");
            System.exit(GlobalVars.evaluationFailedExitCode);
        }
    }

    static IDatabase InitDatabase(){
        return new CacheDatabase();
    }

}