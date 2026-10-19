package Observer;

import Model.Board;
import Model.Robot;

import java.util.ArrayList;
import java.util.List;

public class RoverSubject {
    List<RoverObserver> observerCollection;

    public RoverSubject() {
        this.observerCollection = new ArrayList<RoverObserver>();
    }

    public void attachObserver(RoverObserver observer) {
        if (observer != null) {
            this.observerCollection.add(observer);
        }
    }

    public void detachObserver(RoverObserver observer) {
        if (observer != null && !this.observerCollection.isEmpty()) {
            this.observerCollection.remove(observer);
        }
    }

    public void notifyObserver(Robot robot, Board board) {
        for (RoverObserver observer : this.observerCollection) {
            observer.update(robot, board);
        }
    }
}
