package Commands;

import Classes.Robot;
import Interfaces.Command;

public class ResMCommand implements Command {

    @Override
    public void execute(Robot robot) { robot.getCommandsList().clear(); }

    @Override
    public Command getInverse() { return null; }
}
