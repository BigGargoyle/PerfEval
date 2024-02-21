package cz.cuni.mff.d3s.perfeval.printers;

import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import dnl.utils.text.table.TextTable;

import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Printer for file information.
 * !!! THIS IS NOT AN IMPLEMENTATION OF THE RESULT PRINTER INTERFACE !!!
 */
public class FileInfoPrinter {
    /**
     * Index of column with path.
     */
    private static final int PATH_COLUMN_INDEX = 0;
    /**
     * Index of column with date of creation.
     */
    private static final int DATE_OF_CREATION_COLUMN_INDEX = 1;
    /**
     * Index of column with date of commit.
     */
    private static final int DATE_OF_COMMIT_COLUMN_INDEX = 2;
    /**
     * Index of column with version.
     */
    private static final int VERSION_COLUMN_INDEX = 3;
    /**
     * Index of column with tag.
     */
    private static final int TAG_COLUMN_INDEX = 4;

    /**
     * Number of columns in the table.
     */
    private static final int COLUMN_COUNT = 5;

    /**
     * Print the results in table format.
     *
     * @param results results to be printed
     * @param out     PrintStream where the results will be printed
     */
    public void print(FileWithResultsData[] results, PrintStream out) {
        String[] columnNames = new String[COLUMN_COUNT];
        columnNames[PATH_COLUMN_INDEX] = "Path";
        columnNames[DATE_OF_CREATION_COLUMN_INDEX] = "Date of creation";
        columnNames[DATE_OF_COMMIT_COLUMN_INDEX] = "Date of commit";
        columnNames[VERSION_COLUMN_INDEX] = "Version";
        columnNames[TAG_COLUMN_INDEX] = "Tag";

        String[][] data = new String[results.length][COLUMN_COUNT];
        for (int i = 0; i < results.length; i++) {
            data[i][PATH_COLUMN_INDEX] = Path.of(results[i].path()).normalize().toString();
            data[i][DATE_OF_CREATION_COLUMN_INDEX] = results[i].dateOfCreation().toString();
            data[i][DATE_OF_COMMIT_COLUMN_INDEX] = results[i].version().dateOfCommit().toString();
            data[i][VERSION_COLUMN_INDEX] = results[i].version().commitVersionHash();
            data[i][TAG_COLUMN_INDEX] = results[i].version().tag();
        }

        TextTable textTable = new TextTable(columnNames, data);
        textTable.setAddRowNumbering(true);
        textTable.printTable(out, 0);
    }
}
