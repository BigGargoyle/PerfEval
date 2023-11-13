package cz.cuni.mff.d3s.perfeval.resultDatabase;

import java.util.Date;


/**
 * Container of data about file that is a result of some benchmark test
 *
 * @param path path to the file
 * @param dateOfCreation date of creation of the file
 * @param version version to which the file belongs to
 */
public record FileWithResultsData(String path, Date dateOfCreation, ProjectVersion version) {
}
