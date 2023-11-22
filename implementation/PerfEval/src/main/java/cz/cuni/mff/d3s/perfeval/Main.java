package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;

/**
 * Main class of the PerfEval system.
 */
public class Main {

    /**
     * Main method of the PerfEval system.
     * It assembles the command from the arguments and executes it.
     *
     * @param args Arguments of the program.
     */
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