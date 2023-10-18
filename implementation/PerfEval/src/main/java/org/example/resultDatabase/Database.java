package org.example.resultDatabase;

import java.nio.file.Path;

/**
 * Interface defining behaviour of the benchmark test results database
 */
public interface Database {
    /**
     * @param n how many results should be returned
     * @return n most recently added benchmark test results file paths from the database
     */
    String[] getLastNVersions(int n) throws DatabaseException;
    FileWithResultsData[] getLastNResults(int n) throws DatabaseException;
    FileWithResultsData[] getResultsOfVersion(String version) throws DatabaseException;

    void addFile(Path filePath, String version, String tag) throws DatabaseException;

    void addFilesFromDir(Path dirPath, String version, String tag) throws DatabaseException;
}