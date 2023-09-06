package org.example.ResultDatabase;

import org.example.MeasurementFactory.IMeasurementParser;
import org.example.MeasurementFactory.ParserIndustry;
import org.example.PerformanceComparatorFactory.ComparatorIndustry;
import org.example.PerformanceComparatorFactory.IPerformanceComparator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Implementation of IDatabase with no caching, everytime something is searched in the database then the whole database is gone through
 */
public class DumbDatabase implements IDatabase {

    static String DatabaseItemIdentifier = "R";
    static String DatabaseColumnSeparator = "\t";
    static String DatabaseFileName = ".performance/test_results.db";
    static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

    @Override
    public String[] GetLastNResults(int n) {
        PriorityQueue<DatabaseItem> maxHeap = new PriorityQueue<>(Comparator.comparing(DatabaseItem::dateOfCreation));
        try (BufferedReader reader = new BufferedReader(new FileReader(DatabaseFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DatabaseItem item = ParseDatabaseLine(line);
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
    public boolean AddFile(String fileName) {
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

        if(HasUnknownTestFormat(path.toString())){
            System.err.println("Unknown file format");
            return false;
        }

        String version = ResolveVersion();
        DatabaseItem item = new DatabaseItem(path.toString(), date, version);


        try {
            FileWriter database = new FileWriter(DatabaseFileName, true);
            database.append(DatabaseItemToString(item)).append(System.lineSeparator());
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static boolean HasUnknownTestFormat(String filePath){
        IMeasurementParser parser = ParserIndustry.RecognizeParserFactory(filePath);
        return parser == null;
    }

    @Override
    public boolean AddFilesFromDir(String dirName) {
        return AddFilesFromDirDFS(new File(dirName));
        // return false;
    }

    /**
     *
     * @param fileOrDir path to a file that is meant to be added to database or to a directory inside which there are files (benchmark results) searched
     * @return true - if adding was always successful, false - otherwise
     */
    boolean AddFilesFromDirDFS(File fileOrDir) {

        //File file = new File(dirName);

        if (Files.isDirectory(fileOrDir.toPath())) {
            File[] files = fileOrDir.listFiles();
            if (files == null) return false;
            boolean result = true;
            for (var fileInDir : files) {
                result = result & AddFilesFromDirDFS(fileInDir);
            }
            return result;
        }
        if (Files.isRegularFile(fileOrDir.toPath())) {
            return AddFile(fileOrDir.toPath().toString());
        }
        return true;
    }

    /**
     *
     * @param line line from a database file
     * @return an instance of a DatabaseItem which was created from the line
     */
    DatabaseItem ParseDatabaseLine(String line) {
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
    String DatabaseItemToString(DatabaseItem databaseItem) {
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
    String ResolveVersion() {
        // TODO: implementation of git Version
        String version = "1234";

        return version;
    }

}