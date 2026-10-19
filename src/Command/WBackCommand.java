package Command;

import Model.Robot;

/**
 * Commande qui annule les commandes enregistrées du robot.
 */
public class WBackCommand implements Command {
    /**
     * Demande au robot d'exécuter les inverses de son historique.
     *
     * @param robot robot dont le parcours doit être annulé
     */
    @Override
    public void execute(Robot robot) { robot.goBack(); }
}
