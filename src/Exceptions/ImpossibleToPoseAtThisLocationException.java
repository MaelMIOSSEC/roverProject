package Exceptions;

/**
 * Signale qu'un élément ne peut pas être placé à la position demandée.
 */
public class ImpossibleToPoseAtThisLocationException extends RuntimeException {
    /**
     * Crée l'exception avec un message explicatif.
     *
     * @param message détail de l'erreur
     */
    public ImpossibleToPoseAtThisLocationException(String message) {
        super(message);
    }
}
