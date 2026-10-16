package Commands;

import Classes.Robot;
import Interfaces.Command;

public class StopMCommand implements Command {

    @Override
    public void execute(Robot robot) { robot.setRecording(false); }

    @Override
    public Command getInverse() { return null; }
}
