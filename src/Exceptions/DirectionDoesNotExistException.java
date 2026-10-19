package Exceptions;

/**
 * Signale qu'une direction demandée ne peut pas être utilisée.
 */
public class DirectionDoesNotExistException extends RuntimeException {
    /**
     * Crée l'exception avec un message explicatif.
     *
     * @param message détail de l'erreur
     */
    public DirectionDoesNotExistException(String message) { super(message); }
}
