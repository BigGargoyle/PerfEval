package org.example.resultDatabase;

import org.example.measurementFactory.IMeasurementParser;
import org.example.measurementFactory.ParserFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

/**
 * Implementation of IDatabase with no caching, everytime something is searched in the database then the whole database is gone through
 */
public class DumbDatabase implements IDatabase {

    static final String DatabaseItemIdentifier = "R";
    static final String DatabaseColumnSeparator = "\t";
    static final String DatabaseFileName = ".performance/test_results.db";
    static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

    @Override
    public String[] getLastNResults(int n) {
        PriorityQueue<DatabaseItem> maxHeap = new PriorityQueue<>(Comparator.comparing(DatabaseItem::dateOfCreation));
        try (BufferedReader reader = new BufferedReader(new FileReader(DatabaseFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DatabaseItem item = parseDatabaseLine(line);
                if (item == null) continue;
                if (maxHeap.size() <= n)
                    maxHeap.add(item);
                else if (item.dateOfCreation().compareTo(maxHeap.peek().dateOfCreation()) > 0) {
                    maxHeap.poll();
                    maxHeap.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String[] result = new String[n];
        for (int i = 0; i < result.length; i++) {
            if (maxHeap.size() > 0)
                result[i] = maxHeap.poll().path();
            else
                break;
        }

        return result;
    }

    @Override
    public boolean addFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists())
            return false;


        Path path = Paths.get(fileName);
        Date date;
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            date = new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(hasUnknownTestFormat(path.toString())){
            System.err.println("Unknown file format");
            return false;
        }

        String version = resolveVersion();
        DatabaseItem item = new DatabaseItem(path.toString(), date, version);


        try {
            FileWriter database = new FileWriter(DatabaseFileName, true);
            database.append(databaseItemToString(item)).append(System.lineSeparator());
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static boolean hasUnknownTestFormat(String filePath){
        IMeasurementParser parser = ParserFactory.recognizeParserFactory(filePath);
        return parser == null;
    }

    @Override
    public boolean addFilesFromDir(String dirName) {
        return addFilesFromDirDFS(new File(dirName));
        // return false;
    }

    /**
     *
     * @param fileOrDir path to a file that is meant to be added to database or to a directory inside which there are files (benchmark results) searched
     * @return true - if adding was always successful, false - otherwise
     */
    boolean addFilesFromDirDFS(File fileOrDir) {

        //File file = new File(dirName);

        if (Files.isDirectory(fileOrDir.toPath())) {
            File[] files = fileOrDir.listFiles();
            if (files == null) return false;
            boolean result = true;
            for (var fileInDir : files) {
                result = result & addFilesFromDirDFS(fileInDir);
            }
            return result;
        }
        if (Files.isRegularFile(fileOrDir.toPath())) {
            return addFile(fileOrDir.toPath().toString());
        }
        return true;
    }

    /**
     *
     * @param line line from a database file
     * @return an instance of a DatabaseItem which was created from the line
     */
    DatabaseItem parseDatabaseLine(String line) {
        String[] splittedLine = line.split("\t");
        if (splittedLine.length >= 4 && splittedLine[0].compareTo(DatabaseItemIdentifier) != 0)
            return null;
        Date date;
        try {
            date = DateFormat.parse(splittedLine[2]);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return new DatabaseItem(splittedLine[1], date, splittedLine[3]);
    }

    /**
     *
     * @param databaseItem creating a database file line from an instance of DatabaseItem
     * @return database file line
     */
    String databaseItemToString(DatabaseItem databaseItem) {
        String result = DatabaseItemIdentifier + DatabaseColumnSeparator;
        result += databaseItem.path() + DatabaseColumnSeparator;
        result += DateFormat.format(databaseItem.dateOfCreation()) + DatabaseColumnSeparator;
        result += databaseItem.version();
        return result;
    }

    /**
     *
     * @return version of software to which the new database file belongs to
     */
    String resolveVersion() {
        // TODO: implementation of git Version
        String version = "1234";

        return version;
    }

}