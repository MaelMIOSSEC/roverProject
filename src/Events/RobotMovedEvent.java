package Events;

import Interfaces.RoverListener;

import EventHandler.AbstractEvent;

import java.io.Serial;

public class RobotMovedEvent extends AbstractEvent {
    @Serial
    private static final long serialVersionUID = 1209936372774821519L;

    public RobotMovedEvent(Object source) {
        super(source);
    }

    @Override
    public void sendTo(Object target) {
        ((RoverListener)target).parameterChanged(this);
    }
}
