package org.example.resultDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Container of data about file that is a result of some benchmark test
 *
 * @param path path to the file
 * @param dateOfCreation date of creation of the file
 * @param version version to which the file belongs to
 */
public record FileWithResultsData(String path, Date dateOfCreation, String version) {
    static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");
    static final String DATABASE_ITEM_IDENTIFIER = "R";
    static final String COLUMN_DELIMITER = "\t";

    public String toDatabaseString() {
        String result = DATABASE_ITEM_IDENTIFIER + COLUMN_DELIMITER;
        result += path + COLUMN_DELIMITER;
        result += DateFormat.format(dateOfCreation) + COLUMN_DELIMITER;
        result += version;
        return result;
    }

    public static FileWithResultsData fromDatabaseString(String databaseString) throws InvalidParameterException {
        String[] splittedLine = databaseString.split(COLUMN_DELIMITER);
        if (splittedLine.length >= 4 && splittedLine[0].compareTo(DATABASE_ITEM_IDENTIFIER) != 0)
            throw new InvalidParameterException();
        Date date;
        try {
            date = DateFormat.parse(splittedLine[2]);
        } catch (ParseException e) {
            InvalidParameterException exception = new InvalidParameterException();
            exception.initCause(e);
            throw exception;
        }
        return new FileWithResultsData(splittedLine[1], date, splittedLine[3]);
    }

    public static FileWithResultsData createFromFilePath(Path filePath, String fileVersion) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
        Date date = new Date(attributes.creationTime().toMillis());
        return new FileWithResultsData(filePath.toAbsolutePath().toString(), date, fileVersion);
    }

}
