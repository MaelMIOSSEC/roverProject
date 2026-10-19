package Interfaces;

import Classes.Robot;

import java.util.List;

public interface Command {
    void execute(Robot robot);

    default Command getInverse() {
        return null;
    }

    default Command mergeWith(List<Command> other) { return null; }
}
