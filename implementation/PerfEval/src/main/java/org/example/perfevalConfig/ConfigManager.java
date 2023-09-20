package org.example.perfevalConfig;

import org.example.globalVariables.FileNames;
import org.example.measurementFactory.UniversalTimeUnit;
import org.example.perfevalInit.IniFileData;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ConfigManager {
    public static final String setVersionParam = "--set-version";
    public static final String setTestMaxTimeParam = "--set-test-max-time";
    public static final String setCIWidthParam = "--set-ci-width";
    public static final String setCritValueParam = "--set-crit-value";
    public static final String checkGitParam = "--check-git";
    public static final String equalSign = "=";

    public static boolean config(String[] args) {

        if (args[2] == null) return false;

        String[] params = new String[2];
        IniFileData iniData = new IniFileData(true);
        if (args[2].contains(equalSign)) {
            String[] splittedArg = args[2].split(equalSign);
            if (splittedArg.length < 2 || splittedArg[0] == null || splittedArg[1] == null) {
                params[0] = splittedArg[0];
                params[1] = splittedArg[1];
            } else return false;
        }
        else if(args[2].compareTo(checkGitParam)==0) {
                return checkGitPresence(iniData);
        }
        else if (args.length < 4) {
            return false;
        }
        else {
            params[0] = args[1];
            params[1] = args[2];
        }


        switch (params[0]) {
            case setVersionParam -> {
                return setVersion(params, iniData);
            }
            case setTestMaxTimeParam -> {
                return setMaxTimeOnTest(params, iniData);
            }
            case setCIWidthParam -> {
                return setCIWidth(params, iniData);
            }
            case setCritValueParam -> {
                return setCritValue(params, iniData);
            }
            default -> {
                return false;
            }
        }
    }

    private static boolean setVersion(String[] params, IniFileData iniFileData) {
        if (params == null || params.length < 2 || params[1] == null) return false;
        iniFileData.version = params[1];
        return IniFileData.createNewIniFile(iniFileData);
    }

    private static boolean setCIWidth(String[] params, IniFileData iniFileData) {
        if (params == null || params.length < 2 || params[1] == null) return false;
        double CIWidth;
        try {
            CIWidth = Double.parseDouble(params[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (CIWidth <= 0 || CIWidth >= 1) return false;
        iniFileData.maxCIWidth = CIWidth;
        return IniFileData.createNewIniFile(iniFileData);
    }

    private static boolean setCritValue(String[] params, IniFileData iniFileData) {
        if (params == null || params.length < 2 || params[1] == null) return false;
        double critValue;
        try {
            critValue = Double.parseDouble(params[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (critValue <= 0 || critValue >= 1) return false;
        iniFileData.critValue = critValue;
        return IniFileData.createNewIniFile(iniFileData);
    }
    private static boolean setMaxTimeOnTest(String[] params, IniFileData iniFileData){
        if (params == null || params.length < 2 || params[1] == null) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        UniversalTimeUnit newTime;
        try{
            LocalTime time = LocalTime.parse(params[1], formatter);
            int seconds = (((time.getHour()*60)+time.getMinute())*60)+time.getSecond();
            newTime = new UniversalTimeUnit(seconds, TimeUnit.SECONDS);
        }catch (Exception e){
            return false;
        }
        iniFileData.maxTimeOnTest = newTime;
        return IniFileData.createNewIniFile(iniFileData);
    }

    private static boolean checkGitPresence(IniFileData iniFileData){
        iniFileData.gitFilePresence = new File(FileNames.workingDirectory + "/" + FileNames.gitFileName).exists();
        return IniFileData.createNewIniFile(iniFileData);
    }
}
