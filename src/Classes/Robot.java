package Classes;

import Enumerators.Direction;
import Exceptions.DirectionDoesNotExistException;
import Exceptions.PositionInvalidException;
import Interfaces.Command;

import java.util.List;

public class Robot {
    private Position position;
    private Direction direction;
    private Board board;

    private CommandParser commandParser;

    public Robot(Board board) {
        this.position = new Position(0, 0);
        this.direction = Direction.EAST;
        this.board = board;

        this.commandParser = new CommandParser();
    }

    public Position getPosition() { return position; }
    public void setPosition(Position newPosition) {
        if (this.board != null) {
            if (this.position != null) {
                this.board.removeRobot(this.position, this);
            }

            this.position = newPosition;
            this.board.putAt(newPosition, this, this);
        } else {
            this.position = newPosition;
        }
    }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public Board getBoard() { return board; }
    public void setBoard(Board board) { this.board = board; }

    public boolean isPositionValid(Position position, Board board) {
        return position.getX() >= 0 && position.getX() < board.getWidth() &&
                position.getY() >= 0 && position.getY() < board.getHeight();
    }

    public void move(int distance) {
        if (!isPositionValid(this.position, this.board) || this.position == null)
            throw new PositionInvalidException("The position you are in or trying to move into is invalid.");

        Position newPosition = switch (this.direction) {
            case NORTH -> new Position(this.position.getX(), this.position.getY() + distance);
            case SOUTH -> new Position(this.position.getX(), this.position.getY() - distance);
            case EAST -> new Position(this.position.getX() + distance, this.position.getY());
            case WEST -> new Position(this.position.getX() - distance, this.position.getY());
            default -> throw new DirectionDoesNotExistException("The chosen direction does not exist, moving is impossible");
        };

        this.setPosition(newPosition);
    }

    public void execute(String script) {
        List<Command> commands = this.commandParser.parseScript(script);
        for (Command command: commands) {
            command.execute(this);
        }
    }
}
