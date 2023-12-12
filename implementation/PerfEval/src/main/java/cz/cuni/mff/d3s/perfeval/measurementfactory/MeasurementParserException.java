package cz.cuni.mff.d3s.perfeval.measurementfactory;

public class MeasurementParserException extends RuntimeException{
    String message;
    public MeasurementParserException(String message) {
        this.message = message;
    }

    public MeasurementParserException(String message, Throwable cause) {
        this.message = message;
        initCause(cause);
    }

    @Override
    public String toString() {
        return message;
    }
}
