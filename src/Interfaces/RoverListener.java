package Interfaces;

import Events.RobotMovedEvent;

public interface RoverListener {
    default void parameterChanged(RobotMovedEvent e) {}
}
