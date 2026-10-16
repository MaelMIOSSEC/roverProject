package Classes;

import Commands.*;
import Enumerators.Direction;
import Exceptions.DirectionDoesNotExistException;
import Exceptions.PositionInvalidException;
import Interfaces.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class Robot {
    private Position position;
    private Direction direction;
    private Board board;

    private final CommandParser commandParser;
    private List<Command> commandsList;
    private boolean isRecording;

    public Robot(Board board) {
        this.position = new Position(0, 0);
        this.direction = Direction.EAST;
        this.board = board;

        this.commandParser = new CommandParser();
        this.commandsList = new ArrayList<>();
        this.isRecording = true;
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

    public void setCommandsList(List<Command> commandsList) { this.commandsList = commandsList; }
    public List<Command> getCommandsList() { return commandsList; }

    public boolean isRecording() { return isRecording; }
    public void setRecording(boolean recording) { isRecording = recording; }

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

    public void replay() {
        List<Command> historyToReplay = new ArrayList<>(this.commandsList);
        for (Command command: historyToReplay) {
            command.execute(this);
        }
    }

    public void goBack() {
        List<Command> historyToReplay = new ArrayList<>(this.commandsList);
        ListIterator<Command> iterator = historyToReplay.listIterator(historyToReplay.size());

        while(iterator.hasPrevious()){
            Command command = iterator.previous().getInverse();

            if (command != null) {
                command.execute(this);
            }
        }
    }

    public List<Command> optimizePath(List<Command> commands) {
        List<Command> optimized = new ArrayList<>();

        for (Command current : commands) {
            if (optimized.isEmpty()) {
                optimized.add(current);
                continue;
            }

            Command last = optimized.get(optimized.size() - 1);

            if (last.getClass().equals(current.getClass())) {
                Command merged = last.mergeWith(List.of(current));

                optimized.remove(optimized.size() - 1);

                if (merged != null) {
                    optimized.add(merged);
                }
            } else {
                optimized.add(current);
            }
        }

        return optimized;
    }

    public void execute(String script) {
        List<Command> commands = this.commandParser.parseScript(script);
        for (Command command: commands) {
            command.execute(this);

            if (this.isRecording && isRecorable(command)) {
                this.commandsList.add(command);
            }
        }
    }

    public boolean isRecorable(Command command) {
        return !(command instanceof StartMCommand ||
                command instanceof StopMCommand ||
                command instanceof ResMCommand ||
                command instanceof ReplayCommand ||
                command instanceof WBackCommand);
    }
}
