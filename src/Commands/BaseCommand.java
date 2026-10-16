package Commands;

import Classes.Position;
import Classes.Robot;
import Exceptions.PositionInvalidException;
import Interfaces.Command;

public record BaseCommand(int x, int y) implements Command {

    @Override
    public void execute(Robot robot) {
        if (!robot.isPositionValid(robot.getPosition(), robot.getBoard()))
            throw new PositionInvalidException("The position you are in or trying to move into is invalid.");

        robot.setPosition(new Position(this.x, this.y));
    }
}
