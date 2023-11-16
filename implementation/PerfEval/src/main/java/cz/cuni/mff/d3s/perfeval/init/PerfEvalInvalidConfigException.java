package cz.cuni.mff.d3s.perfeval.init;

public class PerfEvalInvalidConfigException extends Exception{
    static String message = "Config file has invalid data.";

    @Override
    public String toString() {
        String cause = getCause() != null ? getCause().toString() : "";
        return cause + System.lineSeparator() + message;
    }
}
