package Commands;

import Classes.Robot;
import Interfaces.Command;

public class DropCommand implements Command {
    @Override
    public void execute(Robot robot) {
        robot.getBoard().putAt(robot.getPosition(), new Object(), robot);
    }
}
