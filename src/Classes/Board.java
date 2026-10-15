package Classes;

import Exceptions.ImpossibleToPoseAtThisLocationException;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private int width;
    private int height;
    private String color;
    private final Map<Position, Ground> grounds;
    private final Map<Position, Object> occupants;

    public Board(int width, int height, String color, Map<Position, Ground> grounds) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.grounds = grounds;
        this.occupants = new HashMap<Position, Object>();
    }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Object getAt(Position position) {
        if (this.occupants.containsKey(position)) {
            return this.occupants.get(position);
        }

        return this.grounds.get(position);
    }

    public void putAt(Position position, Object element, Robot robot) {
        if (!robot.isPositionValid(position, this)) {
            throw new ImpossibleToPoseAtThisLocationException("The item cannot be placed outside the field. Action impossible.");
        }

        this.occupants.put(position, element);
    }

    public void removeRobot(Position position, Robot robot) {
        if (this.occupants.get(position) == robot) {
            this.occupants.remove(position);
        }
    }
}
