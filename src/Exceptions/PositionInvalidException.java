package Exceptions;

/**
 * Signale qu'une position est invalide pour l'opération demandée.
 */
public class PositionInvalidException extends RuntimeException {
    /**
     * Crée l'exception avec un message explicatif.
     *
     * @param message détail de l'erreur
     */
    public PositionInvalidException(String message) {
        super(message);
    }
}
