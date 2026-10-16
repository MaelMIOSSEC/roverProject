package Commands;

import Classes.Robot;
import Enumerators.Direction;
import Interfaces.Command;

import java.util.List;

public class DirectionCommand implements Command {
    private final Direction direction;
    private Direction previousDirection;

    public DirectionCommand(Direction direction) { this.direction = direction; }

    public Direction getDirection() { return direction; }

    @Override
    public void execute(Robot robot) {
        this.previousDirection = robot.getDirection();
        robot.setDirection(this.direction);
    }

    @Override
    public Command getInverse() {
        if (this.previousDirection == null) {
            return null;
        }
        return new DirectionCommand(this.previousDirection);
    }

    @Override
    public Command mergeWith(List<Command> commandsList) {
        if (commandsList == null || commandsList.isEmpty()) {
            return null;
        }

        boolean allAreDirections = commandsList.stream()
                .allMatch(DirectionCommand.class::isInstance);

        if (!allAreDirections) return null;

        DirectionCommand lastDirection = (DirectionCommand) commandsList.get(commandsList.size() - 1);
        return new DirectionCommand(lastDirection.getDirection());
    }
}
