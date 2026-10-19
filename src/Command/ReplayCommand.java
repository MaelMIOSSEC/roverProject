package Command;

import Model.Robot;

/**
 * Commande qui rejoue l'historique enregistré du robot.
 */
public class ReplayCommand implements Command {
    /**
     * Demande au robot de réexécuter son historique courant.
     *
     * @param robot robot dont l'historique est rejoué
     */
    @Override
    public void execute(Robot robot) { robot.replay(); }
}
