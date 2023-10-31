package cz.cuni.mff.hrdydo.resultDatabase;

import java.nio.file.Path;
import java.util.Date;

/**
 * Interface defining behaviour of the benchmark test results database
 */
public interface Database {
    /**
     * @param n how many results should be returned
     * @return n most recently added benchmark test results file paths from the database
     */
    FileVersionCharacteristic[] getLastNVersions(int n) throws DatabaseException;
    FileWithResultsData[] getLastNResults(int n) throws DatabaseException;
    FileWithResultsData[] getResultsOfVersion(FileVersionCharacteristic version) throws DatabaseException;

    void addFile(Path filePath, FileVersionCharacteristic version) throws DatabaseException;

    void addFilesFromDir(Path dirPath, FileVersionCharacteristic version) throws DatabaseException;

    FileVersionCharacteristic findOlderNeighbourVersion(FileVersionCharacteristic version) throws DatabaseException;
    FileVersionCharacteristic findNewerNeighbourVersion(FileVersionCharacteristic version) throws DatabaseException;
    FileVersionCharacteristic[] findVersionsOfPattern(FileVersionCharacteristic pattern) throws DatabaseException;
    FileVersionCharacteristic[] findVersionsNewerThan(Date date) throws DatabaseException;
    FileVersionCharacteristic findClosestVersionToDate(Date date) throws DatabaseException;
}