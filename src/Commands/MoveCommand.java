package Commands;

import Classes.Robot;
import Interfaces.Command;

import java.util.List;

public record MoveCommand(int distance) implements Command {

    @Override
    public void execute(Robot robot) {
        robot.move(distance);
    }

    @Override
    public Command getInverse() {
        return new MoveCommand(-(this.distance));
    }

    @Override
    public Command mergeWith(List<Command> commandsList) {
        boolean allAreMoves = commandsList.stream()
                .allMatch(MoveCommand.class::isInstance);

        if (commandsList.isEmpty() || !allAreMoves) return null;

        int newDistance = this.distance;

        for (Command command : commandsList) {
            if (command instanceof MoveCommand movecommand) {
                newDistance += movecommand.distance();
            }
        }

        if (newDistance == 0) return null;

        return new MoveCommand(newDistance);
    }
}
