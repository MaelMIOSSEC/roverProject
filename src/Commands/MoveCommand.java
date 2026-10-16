package Commands;

import Classes.Robot;
import Interfaces.Command;

public class MoveCommand implements Command {
    private final int distance;

    public MoveCommand(int distance) { this.distance = distance; }

    public int getDistance() { return distance; }

    @Override
    public void execute(Robot robot) { robot.move(distance); }

    @Override
    public Command getInverse() { return new MoveCommand(-(this.distance)); }
}
