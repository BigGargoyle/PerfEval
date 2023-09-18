package org.example.perfevalInit;

import org.example.GlobalVars;
import org.example.MeasurementFactory.UniversalTimeUnit;

import java.io.*;
import java.util.concurrent.TimeUnit;

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
        try (BufferedReader reader = new BufferedReader(new FileReader(GlobalVars.workingDirectory + "/" + GlobalVars.IniFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splittedLine = line.split(GlobalVars.ColumnDelimiter);
                if (splittedLine.length < 2) continue;
                switch (splittedLine[0]) {
                    case GlobalVars.gitSign -> gitFilePresence = splittedLine[1].compareTo(GlobalVars.TrueString) == 0;
                    case GlobalVars.critValueSign -> {
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
                    case GlobalVars.maxCIWidthSign -> {
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
                    case GlobalVars.maxTimeOnTestSign -> {
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
                    case GlobalVars.versionSign -> version = splittedLine[1];
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
        gitFilePresence = (new File(GlobalVars.gitFileName)).exists();
        maxTimeOnTest = GlobalVars.defaultMaxTimeOnTest;
        maxCIWidth = GlobalVars.defaultMaxCIWidth;
        critValue = GlobalVars.defaultStatisticCritValue;
        version = GlobalVars.UnknownVersion;
    }

    public static boolean createNewIniFile(IniFileData iniFileData) {
        boolean result;
        if (!iniFileData.validConfig)
            return false;
        File file = new File(GlobalVars.workingDirectory, GlobalVars.IniFileName);
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
            writer.write(GlobalVars.critValueSign + GlobalVars.ColumnDelimiter + fileData.critValue);
            writer.newLine();
            writer.write(GlobalVars.maxCIWidthSign + GlobalVars.ColumnDelimiter + fileData.maxCIWidth);
            writer.newLine();
            writer.write(GlobalVars.maxTimeOnTestSign + GlobalVars.ColumnDelimiter + fileData.maxTimeOnTest.getNanoSeconds());
            writer.newLine();
            if (fileData.gitFilePresence)
                writer.write(GlobalVars.gitSign + GlobalVars.ColumnDelimiter + GlobalVars.TrueString);
            else
                writer.write(GlobalVars.gitSign + GlobalVars.ColumnDelimiter + GlobalVars.FalseString);
            writer.newLine();
            writer.write(GlobalVars.versionSign + GlobalVars.ColumnDelimiter + fileData.version);
            writer.newLine();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
