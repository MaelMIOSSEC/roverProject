package Exceptions;

public class PositionInvalidException extends RuntimeException {
    public PositionInvalidException(String message) {
        super(message);
    }
}
