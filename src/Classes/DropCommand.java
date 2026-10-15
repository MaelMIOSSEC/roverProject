package Classes;

import Interfaces.Command;

import java.util.HashMap;
import java.util.Map;

public class DropCommand implements Command {
    @Override
    public void execute(Robot robot) {
        robot.getBoard().putAt(robot.getPosition(), new Object(), robot);
    }
}
