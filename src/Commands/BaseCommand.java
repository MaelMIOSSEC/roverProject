package Commands;

import Classes.Position;
import Classes.Robot;
import Exceptions.PositionInvalidException;
import Interfaces.Command;

/**
 * Commande qui place le robot aux coordonnées indiquées.
 *
 * @param x coordonnée horizontale de destination
 * @param y coordonnée verticale de destination
 */
public record BaseCommand(int x, int y) implements Command {

    /**
     * Vérifie la position actuelle du robot, puis le place à la destination configurée.
     *
     * @param robot robot auquel appliquer la commande
     * @throws PositionInvalidException si la position actuelle du robot est invalide
     * @throws Exceptions.ImpossibleToPoseAtThisLocationException si la destination est hors du terrain
     */
    @Override
    public void execute(Robot robot) {
        if (robot.isPositionValid(robot.getPosition(), robot.getBoard()))
            throw new PositionInvalidException("The position you are in or trying to move into is invalid.");

        robot.setPosition(new Position(this.x, this.y));
    }
}
