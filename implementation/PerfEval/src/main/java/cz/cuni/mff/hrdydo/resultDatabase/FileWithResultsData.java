package cz.cuni.mff.hrdydo.resultDatabase;

import java.util.Date;
import java.util.Objects;


/**
 * Container of data about file that is a result of some benchmark test
 *
 * @param path path to the file
 * @param dateOfCreation date of creation of the file
 * @param version version to which the file belongs to
 */
public record FileWithResultsData(String path, Date dateOfCreation, FileVersionCharacteristic version) {
}
