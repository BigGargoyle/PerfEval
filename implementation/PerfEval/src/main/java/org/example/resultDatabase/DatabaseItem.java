package org.example.resultDatabase;

import org.example.globalVariables.DBFlags;

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
public record DatabaseItem(String path, Date dateOfCreation, String version) {
    static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

    public String toDatabaseString() {
        String result = DBFlags.DatabaseItemIdentifier + DBFlags.ColumnDelimiter;
        result += path + DBFlags.ColumnDelimiter;
        result += DateFormat.format(dateOfCreation) + DBFlags.ColumnDelimiter;
        result += version;
        return result;
    }

    public static DatabaseItem fromDatabaseString(String databaseString) throws InvalidParameterException {
        String[] splittedLine = databaseString.split(DBFlags.ColumnDelimiter);
        if (splittedLine.length >= 4 && splittedLine[0].compareTo(DBFlags.DatabaseItemIdentifier) != 0)
            throw new InvalidParameterException();
        Date date;
        try {
            date = DateFormat.parse(splittedLine[2]);
        } catch (ParseException e) {
            InvalidParameterException exception = new InvalidParameterException();
            exception.initCause(e);
            throw exception;
        }
        return new DatabaseItem(splittedLine[1], date, splittedLine[3]);
    }

    public static DatabaseItem createFromFilePath(Path filePath, String fileVersion) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
        Date date = new Date(attributes.creationTime().toMillis());
        return new DatabaseItem(filePath.toAbsolutePath().toString(), date, fileVersion);
    }

}
