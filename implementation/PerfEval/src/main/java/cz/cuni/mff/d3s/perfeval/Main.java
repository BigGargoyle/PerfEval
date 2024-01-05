package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.command.Command;
import cz.cuni.mff.d3s.perfeval.command.Parser;
import cz.cuni.mff.d3s.perfeval.command.ParserException;
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
        Command command = null;
        try {
            command = Parser.getCommand(args);
        } catch (ParserException e) {
            System.err.println(e.getMessage()); //NOPMD - suppressed SystemPrintln - only used for error messages, there is no other way to print them
            e.exitCode.exit();
        }
        try {
            if (command == null) {
                exitCode = ExitCode.invalidArguments;
            } else {
                exitCode = command.execute();
            }

        } catch (PerfEvalCommandFailedException e) {
            System.err.println(e.getMessage()); //NOPMD - suppressed SystemPrintln - only used for error messages, there is no other way to print them
            exitCode = e.exitCode;
        }
        exitCode.exit();
    }
}
