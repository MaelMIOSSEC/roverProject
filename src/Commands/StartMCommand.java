package Commands;

import Classes.Robot;
import Interfaces.Command;

/**
 * Commande qui active l'enregistrement des commandes du robot.
 */
public class StartMCommand implements Command {
    /**
     * Active l'enregistrement des commandes exécutées.
     *
     * @param robot robot dont l'enregistrement est activé
     */
    @Override
    public void execute(Robot robot) { robot.setRecording(true); }
}
