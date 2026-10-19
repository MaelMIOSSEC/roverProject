package Command;

import Model.Robot;

/**
 * Commande qui efface l'historique des commandes du robot.
 */
public class ResMCommand implements Command {
    /**
     * Vide l'historique du robot.
     *
     * @param robot robot dont l'historique doit être effacé
     */
    @Override
    public void execute(Robot robot) { robot.getCommandsList().clear(); }
}
