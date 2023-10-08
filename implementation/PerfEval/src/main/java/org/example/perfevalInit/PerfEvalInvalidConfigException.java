package org.example.perfevalInit;

public class PerfEvalInvalidConfigException extends Exception{
    static String message = "Config file has invalid data.";

    @Override
    public String toString() {
        return getCause().toString() + System.lineSeparator() + message;
    }
}
