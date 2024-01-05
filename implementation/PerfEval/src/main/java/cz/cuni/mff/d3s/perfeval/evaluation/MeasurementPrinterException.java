package cz.cuni.mff.d3s.perfeval.evaluation;

public class MeasurementPrinterException extends Exception{
    String message;
    public MeasurementPrinterException(String message) {
        this.message = message;
    }
    public MeasurementPrinterException(String message, Throwable cause) {
        this.message = message;
        initCause(cause);
    }
}
