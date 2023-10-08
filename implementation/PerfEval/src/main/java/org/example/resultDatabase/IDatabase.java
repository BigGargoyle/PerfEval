package org.example.resultDatabase;

import org.example.perfevalInit.PerfEvalConfig;

import java.nio.file.Path;

/**
 * Interface defining behaviour of the benchmark test results database
 */
public interface IDatabase {
    /**
     * @param n how many results should be returned
     * @return n most recently added benchmark test results file paths from the database
     */
    DatabaseItem[] getLastNResults(int n) throws DatabaseException;

    /**
     * @param filePath name/path to a file that has to added to the database
     */
    void addFile(Path filePath, Path gitFilePath, PerfEvalConfig config) throws DatabaseException;

    /**
     * @param dirPath name/path to a dir that benchmark results have to be added to the database from
     * @return true - if all additions was successful, false - otherwise
     */
    boolean addFilesFromDir(Path dirPath, Path gitFilePath, PerfEvalConfig config) throws DatabaseException;
}