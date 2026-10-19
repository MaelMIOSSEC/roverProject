package Commands;

import Classes.Robot;
import Interfaces.Command;

/**
 * Commande qui désactive l'enregistrement des commandes du robot.
 */
public class StopMCommand implements Command {
    /**
     * Désactive l'enregistrement des commandes exécutées.
     *
     * @param robot robot dont l'enregistrement est désactivé
     */
    @Override
    public void execute(Robot robot) { robot.setRecording(false); }
}
