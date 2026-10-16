package Interfaces;

import Classes.Robot;

public interface Command {
    void execute(Robot robot);

    Command getInverse();
}
