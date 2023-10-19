package org.example;

import org.example.perfevalInit.PerfEvalCommandFailedException;

public class Main {

    public static void main(String[] args) {
        ExitCode exitCode;
        Command command = Parser.getCommand(args);
        try {
            if (command == null) exitCode = ExitCode.invalidArguments;
            else exitCode = command.execute();

        } catch (PerfEvalCommandFailedException e) {
            System.err.println(e.getMessage());
            exitCode = e.exitCode;
        }
        exitCode.exit();
    }
}