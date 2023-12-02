package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import joptsimple.OptionSet;

public interface CommandSetup {
    Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException;
}
