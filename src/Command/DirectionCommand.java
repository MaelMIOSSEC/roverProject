package Command;

import Model.Robot;
import Model.Direction;

import java.util.List;

/**
 * Commande qui oriente le robot dans une direction donnée.
 */
public class DirectionCommand implements Command {
    /** Direction que la commande doit appliquer. */
    private final Direction direction;
    /** Direction du robot avant la dernière exécution, utilisée pour l'inversion. */
    private Direction previousDirection;

    /**
     * Crée une commande de changement de direction.
     *
     * @param direction direction à appliquer
     */
    public DirectionCommand(Direction direction) { this.direction = direction; }

    /** @return direction configurée pour cette commande */
    public Direction getDirection() { return direction; }

    /**
     * Mémorise la direction précédente puis applique la direction configurée.
     *
     * @param robot robot à orienter
     */
    @Override
    public void execute(Robot robot) {
        this.previousDirection = robot.getDirection();
        robot.setDirection(this.direction);
    }

    /**
     * Crée une commande restaurant la direction enregistrée lors de l'exécution précédente.
     *
     * @return commande inverse, ou {@code null} si cette commande n'a pas encore été exécutée
     */
    @Override
    public Command getInverse() {
        if (this.previousDirection == null) return null;

        return new DirectionCommand(this.previousDirection);
    }

    /**
     * Regroupe des commandes de direction en conservant la dernière direction demandée.
     *
     * @param commandsList commandes candidates à la fusion
     * @return commande fusionnée, ou {@code null} si la liste est vide ou incompatible
     */
    @Override
    public Command mergeWith(List<Command> commandsList) {
        if (commandsList == null || commandsList.isEmpty()) return null;

        boolean allAreDirections = commandsList.stream()
                .allMatch(DirectionCommand.class::isInstance);

        if (!allAreDirections) return null;

        DirectionCommand lastDirection = (DirectionCommand) commandsList.get(commandsList.size() - 1);
        return new DirectionCommand(lastDirection.getDirection());
    }
}
