package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.printers.FileInfoPrinter;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;

public class ListResultsCommand implements Command{

    private final Database database;

    public ListResultsCommand(Database database){
        this.database = database;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            FileWithResultsData[] results = database.getAllResults();
            FileInfoPrinter printer = new FileInfoPrinter();
            printer.print(results, System.out);
        } catch (DatabaseException e) {
            throw new PerfEvalCommandFailedException(ExitCode.databaseError);
        }
        return ExitCode.OK;
    }
}
