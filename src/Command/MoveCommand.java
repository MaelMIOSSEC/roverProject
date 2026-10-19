package Command;

import Model.Robot;

import java.util.List;

/**
 * Commande qui déplace le robot d'un nombre de cases donné.
 *
 * @param distance distance à parcourir selon la direction actuelle
 */
public record MoveCommand(int distance) implements Command {

    /**
     * Exécute le déplacement configuré.
     *
     * @param robot robot à déplacer
     */
    @Override
    public void execute(Robot robot) {
        robot.move(distance);
    }

    /**
     * Crée la commande qui parcourt la même distance dans le sens opposé.
     *
     * @return commande de déplacement inverse
     */
    @Override
    public Command getInverse() {
        return new MoveCommand(-(this.distance));
    }

    /**
     * Fusionne cette commande avec des déplacements en additionnant leurs distances.
     *
     * @param commandsList commandes candidates à la fusion
     * @return déplacement total, ou {@code null} si la liste est vide, incompatible ou de somme nulle
     */
    @Override
    public Command mergeWith(List<Command> commandsList) {
        boolean allAreMoves = commandsList.stream()
                .allMatch(MoveCommand.class::isInstance);

        if (commandsList.isEmpty() || !allAreMoves) return null;

        int newDistance = this.distance;

        for (Command command : commandsList) {
            if (command instanceof MoveCommand movecommand) newDistance += movecommand.distance();
        }

        if (newDistance == 0) return null;

        return new MoveCommand(newDistance);
    }
}
