package Command;

import Model.Robot;

import java.util.List;

/**
 * Contrat d'une opération exécutable par un robot.
 */
public interface Command {
    /**
     * Exécute cette commande sur un robot.
     *
     * @param robot robot sur lequel appliquer la commande
     */
    void execute(Robot robot);

    /**
     * Fournit l'inverse de cette commande lorsqu'il est disponible.
     *
     * @return commande inverse, ou {@code null} si aucune inversion n'est définie
     */
    default Command getInverse() {
        return null;
    }

    /**
     * Tente de fusionner cette commande avec d'autres commandes compatibles.
     *
     * @param other commandes à fusionner avec celle-ci
     * @return commande fusionnée, ou {@code null} si la fusion n'est pas prise en charge
     */
    default Command mergeWith(List<Command> other) { return null; }
}
