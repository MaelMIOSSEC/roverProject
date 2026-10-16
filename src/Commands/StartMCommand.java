package Commands;

import Classes.Robot;
import Interfaces.Command;

public class StartMCommand implements Command {

    @Override
    public void execute(Robot robot) { robot.setRecording(true); }

    @Override
    public Command getInverse() { return null; }
}
