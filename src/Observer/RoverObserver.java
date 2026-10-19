package Observer;

import Model.Board;
import Model.Robot;

public interface RoverObserver {
    void update(Robot robot, Board board);
}
