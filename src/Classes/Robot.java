package Classes;

import Commands.*;
import Enumerators.Direction;
import Exceptions.DirectionDoesNotExistException;
import Exceptions.PositionInvalidException;
import Interfaces.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Robot pouvant se déplacer sur un terrain et exécuter des commandes.
 */
public class Robot {
    /** Position actuelle du robot. */
    private Position position;
    /** Direction actuelle du robot. */
    private Direction direction;
    /** Terrain sur lequel le robot se déplace. */
    private final Board board;

    /** Parseur utilisé pour convertir les scripts en commandes. */
    private final CommandParser commandParser;
    /** Historique des commandes enregistrées. */
    private final List<Command> commandsList;
    /** Indique si les commandes exécutées sont ajoutées à l'historique. */
    private boolean isRecording;

    /**
     * Crée un robot initialement placé à l'origine et orienté vers l'est.
     *
     * @param board terrain sur lequel le robot se déplace
     */
    public Robot(Board board) {
        this.position = new Position(0, 0);
        this.direction = Direction.EAST;
        this.board = board;

        this.commandParser = new CommandParser();
        this.commandsList = new ArrayList<>();
        this.isRecording = true;
    }

    /** @return position actuelle du robot */
    public Position getPosition() { return position; }
    /**
     * Place le robot à une nouvelle position et met à jour son occupation du terrain.
     *
     * @param newPosition nouvelle position du robot
     */
    public void setPosition(Position newPosition) {
        if (this.board != null) {
            if (this.position != null) this.board.removeRobot(this.position, this);

            this.position = newPosition;
            this.board.putAt(newPosition, this, this);
        } else {
            this.position = newPosition;
        }
    }

    /** @return direction actuelle du robot */
    public Direction getDirection() { return direction; }
    /**
     * Modifie la direction du robot.
     *
     * @param direction nouvelle direction
     */
    public void setDirection(Direction direction) { this.direction = direction; }

    /** @return terrain associé au robot */
    public Board getBoard() { return board; }

    /** @return historique mutable des commandes enregistrées */
    public List<Command> getCommandsList() { return commandsList; }

    /**
     * Active ou désactive l'enregistrement des commandes exécutées.
     *
     * @param recording {@code true} pour enregistrer les commandes, {@code false} sinon
     */
    public void setRecording(boolean recording) { isRecording = recording; }

    /**
     * Détermine si une position sort des limites du terrain.
     *
     * @param position position à vérifier
     * @param board terrain dont les limites sont utilisées
     * @return {@code true} si la position est hors du terrain
     */
    public boolean isPositionValid(Position position, Board board) {
        return position.getX() < 0 || position.getX() >= board.getWidth() ||
                position.getY() < 0 || position.getY() >= board.getHeight();
    }

    /**
     * Déplace le robot selon sa direction actuelle.
     *
     * @param distance nombre de cases à parcourir, éventuellement négatif
     * @throws PositionInvalidException si la position actuelle est invalide
     * @throws ImpossibleToPoseAtThisLocationException si la destination est hors du terrain
     * @throws DirectionDoesNotExistException si la direction ne peut pas être traitée
     */
    public void move(int distance) {
        if (isPositionValid(this.position, this.board) || this.position == null)
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

    /**
     * Réexécute les commandes actuellement présentes dans l'historique.
     */
    public void replay() {
        List<Command> historyToReplay = new ArrayList<>(this.commandsList);
        for (Command command: historyToReplay) {
            command.execute(this);
        }
    }

    /**
     * Annule l'historique en exécutant les commandes inverses dans l'ordre inverse.
     */
    public void goBack() {
        List<Command> historyToReplay = new ArrayList<>(this.commandsList);
        ListIterator<Command> iterator = historyToReplay.listIterator(historyToReplay.size());

        while(iterator.hasPrevious()){
            Command command = iterator.previous().getInverse();

            if (command != null) command.execute(this);
        }
    }

    /**
     * Fusionne les commandes consécutives de même classe lorsque leur implémentation le permet.
     *
     * @param commands commandes à optimiser
     * @return nouvelle liste contenant le chemin optimisé
     */
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

                if (merged != null) optimized.add(merged);
            } else {
                optimized.add(current);
            }
        }

        return optimized;
    }

    /**
     * Analyse puis exécute chaque commande du script, en enregistrant les commandes admissibles.
     *
     * @param script script à exécuter
     * @throws IllegalArgumentException si le script contient une commande inconnue
     */
    public void execute(String script) {
        List<Command> commands = this.commandParser.parseScript(script);
        for (Command command: commands) {
            command.execute(this);

            if (this.isRecording && isRecordable(command)) this.commandsList.add(command);
        }
    }

    /**
     * Indique si une commande doit être ajoutée à l'historique du robot.
     *
     * @param command commande à examiner
     * @return {@code true} si la commande est enregistrable
     */
    public boolean isRecordable(Command command) {
        return !(command instanceof StartMCommand ||
                command instanceof StopMCommand ||
                command instanceof ResMCommand ||
                command instanceof ReplayCommand ||
                command instanceof WBackCommand);
    }
}
