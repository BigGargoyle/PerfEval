package cz.cuni.mff.d3s.perfeval.resultDatabase;

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
    ProjectVersion[] getLastNVersions(int n) throws DatabaseException;
    FileWithResultsData[] getLastNResults(int n) throws DatabaseException;
    FileWithResultsData[] getResultsOfVersion(ProjectVersion version) throws DatabaseException;

    void addFile(Path filePath, ProjectVersion version) throws DatabaseException;

    void addFilesFromDir(Path dirPath, ProjectVersion version) throws DatabaseException;

    ProjectVersion findOlderNeighbourVersion(ProjectVersion version) throws DatabaseException;
    ProjectVersion findNewerNeighbourVersion(ProjectVersion version) throws DatabaseException;
    ProjectVersion[] findVersionsOfPattern(ProjectVersion pattern) throws DatabaseException;
    ProjectVersion[] findVersionsNewerThan(Date date) throws DatabaseException;
    ProjectVersion findClosestVersionToDate(Date date) throws DatabaseException;
}