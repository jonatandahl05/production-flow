package se.jonatandahl.productionflow.exception;

public class DuplicateJobNumberException extends RuntimeException {
    public DuplicateJobNumberException(String message) {
        super(message);
    }
    
}
