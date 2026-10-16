package Commands;

import Classes.Robot;
import Interfaces.Command;

public class ReplayCommand implements Command {
    @Override
    public void execute(Robot robot) { robot.replay(); }
}
