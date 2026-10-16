package Commands;

import Classes.Robot;
import Interfaces.Command;

public class WBackCommand implements Command {

    @Override
    public void execute(Robot robot) {
        robot.goBack();
    }

    @Override
    public Command getInverse() {
        return null;
    }
}
