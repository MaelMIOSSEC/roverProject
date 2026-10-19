package Command;

import Model.Robot;

/**
 * Commande qui dépose un nouvel objet à la position actuelle du robot.
 */
public class DropCommand implements Command {
    /**
     * Dépose un objet sur la case où se trouve le robot.
     *
     * @param robot robot qui détermine la position de dépôt
     */
    @Override
    public void execute(Robot robot) {
        robot.getBoard().putAt(robot.getPosition(), new Object(), robot);
    }
}
