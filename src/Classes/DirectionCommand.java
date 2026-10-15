package Classes;

import Enumerators.Direction;
import Interfaces.Command;

public class DirectionCommand implements Command {
    private final Direction direction;

    public DirectionCommand(Direction direction) { this.direction = direction; }

    public Direction getDirection() { return direction; }

    @Override
    public void execute(Robot robot) {
        robot.setDirection(this.direction);
    }
}
