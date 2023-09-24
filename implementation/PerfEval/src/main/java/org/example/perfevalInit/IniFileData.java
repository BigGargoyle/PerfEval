package org.example.perfevalInit;

import org.example.globalVariables.DBFlags;
import org.example.globalVariables.DefaultIniValues;
import org.example.globalVariables.FileNames;
import org.example.globalVariables.StringConstants;
import org.example.measurementFactory.UniversalTimeUnit;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Class representing data from PerfEval system ini file.
 */
public class IniFileData {
    public boolean validConfig;
    public boolean gitFilePresence;
    public UniversalTimeUnit maxTimeOnTest;
    public double maxCIWidth;
    public double critValue;
    public String version;

    public IniFileData(boolean readFromFile) {
        if (readFromFile) {
            readDataFromIniFile();
        } else {
            setDefaultData();
        }
    }

    private void readDataFromIniFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FileNames.workingDirectory + "/" + FileNames.IniFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splittedLine = line.split(DBFlags.ColumnDelimiter);
                if (splittedLine.length < 2) continue;
                switch (splittedLine[0]) {
                    case DBFlags.gitSign -> gitFilePresence = splittedLine[1].compareTo(StringConstants.TrueString) == 0;
                    case DBFlags.critValueSign -> {
                        try {
                            critValue = Double.parseDouble(splittedLine[1]);
                            if (critValue >= 1 || critValue <= 0) {
                                validConfig = false;
                                return;
                            }
                        } catch (NumberFormatException e) {
                            validConfig = false;
                            return;
                        }
                    }
                    case DBFlags.maxCIWidthSign -> {
                        try {
                            maxCIWidth = Double.parseDouble(splittedLine[1]);
                            if (maxCIWidth >= 1 || maxCIWidth <= 0) {
                                validConfig = false;
                                return;
                            }
                        } catch (NumberFormatException e) {
                            validConfig = false;
                            return;
                        }
                    }
                    case DBFlags.maxTimeOnTestSign -> {
                        try {
                            long nanoseconds = Long.parseLong(splittedLine[1]);
                            if (nanoseconds < 0) {
                                validConfig = false;
                                return;
                            }
                            maxTimeOnTest = new UniversalTimeUnit(nanoseconds, TimeUnit.NANOSECONDS);
                        } catch (NumberFormatException e) {
                            validConfig = false;
                            return;
                        }
                    }
                    case DBFlags.versionSign -> version = splittedLine[1];
                }
            }
        } catch (IOException e) {
            validConfig = false;
            return;
        }
        if (maxTimeOnTest == null || maxTimeOnTest.getNanoSeconds() < 0) validConfig = false;
        else if (version == null) validConfig = false;
        else if (!(critValue > 0 && critValue < 1)) validConfig = false;
        else validConfig = maxCIWidth > 0 && maxCIWidth < 1;
    }

    private void setDefaultData() {
        validConfig = true;
        gitFilePresence = (new File(FileNames.workingDirectory +"/"+FileNames.gitFileName)).exists();
        maxTimeOnTest = DefaultIniValues.defaultMaxTimeOnTest;
        maxCIWidth = DefaultIniValues.defaultMaxCIWidth;
        critValue = DefaultIniValues.defaultStatisticCritValue;
        version = StringConstants.UnknownVersion;
    }

    public static boolean createNewIniFile(IniFileData iniFileData) {
        boolean result;
        if (!iniFileData.validConfig)
            return false;
        File file = new File(FileNames.workingDirectory, FileNames.IniFileName);
        try {
            if (file.exists())
                if (!file.delete())
                    return false;
            if (!file.createNewFile()) {
                return false;
            }
            result = writeIniFileContent(iniFileData, file);

        } catch (IOException | SecurityException e) {
            return false;
        }
        return result;
    }

    private static boolean writeIniFileContent(IniFileData fileData, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(DBFlags.critValueSign + DBFlags.ColumnDelimiter + fileData.critValue);
            writer.newLine();
            writer.write(DBFlags.maxCIWidthSign + DBFlags.ColumnDelimiter + fileData.maxCIWidth);
            writer.newLine();
            writer.write(DBFlags.maxTimeOnTestSign + DBFlags.ColumnDelimiter + fileData.maxTimeOnTest.getNanoSeconds());
            writer.newLine();
            if (fileData.gitFilePresence)
                writer.write(DBFlags.gitSign + DBFlags.ColumnDelimiter + StringConstants.TrueString);
            else
                writer.write(DBFlags.gitSign + DBFlags.ColumnDelimiter + StringConstants.FalseString);
            writer.newLine();
            writer.write(DBFlags.versionSign + DBFlags.ColumnDelimiter + fileData.version);
            writer.newLine();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
