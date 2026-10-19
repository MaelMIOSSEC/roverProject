package Model;

import Exceptions.ImpossibleToPoseAtThisLocationException;
import Observer.RoverSubject;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente le terrain de jeu et les éléments qui occupent ses cases.
 */
public class Board extends RoverSubject {
    /** Largeur du terrain, exprimée en cases. */
    private int width;
    /** Hauteur du terrain, exprimée en cases. */
    private int height;
    /** Couleur associée au terrain. */
    private String color;
    /** Sol présent à chaque position du terrain. */
    private final Map<Position, Ground> grounds;
    /** Éléments déposés ou robots présents sur le terrain. */
    private final Map<Position, Object> occupants;

    /**
     * Crée un terrain avec ses dimensions, sa couleur et les cases de sol.
     *
     * @param width largeur du terrain en cases
     * @param height hauteur du terrain en cases
     * @param color couleur associée au terrain
     * @param grounds association entre les positions et leur sol
     */
    public Board(int width, int height, String color, Map<Position, Ground> grounds) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.grounds = grounds;
        this.occupants = new HashMap<Position, Object>();
    }

    /** @return largeur du terrain en cases */
    public int getWidth() { return width; }
    /**
     * Modifie la largeur du terrain.
     *
     * @param width nouvelle largeur en cases
     */
    public void setWidth(int width) { this.width = width; }

    /** @return hauteur du terrain en cases */
    public int getHeight() { return height; }
    /**
     * Modifie la hauteur du terrain.
     *
     * @param height nouvelle hauteur en cases
     */
    public void setHeight(int height) { this.height = height; }

    /** @return couleur associée au terrain */
    public String getColor() { return color; }
    /**
     * Modifie la couleur associée au terrain.
     *
     * @param color nouvelle couleur
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Renvoie l'élément occupant une position, ou le sol si aucun élément ne s'y trouve.
     *
     * @param position position à consulter
     * @return l'occupant de la position, le sol correspondant ou {@code null} si aucun n'est défini
     */
    public Object getAt(Position position) {
        if (this.occupants.containsKey(position)) return this.occupants.get(position);

        return this.grounds.get(position);
    }

    /**
     * Place un élément sur le terrain.
     *
     * @param position position où placer l'élément
     * @param element élément à placer
     * @param robot robot utilisé pour vérifier que la position est dans les limites du terrain
     * @throws ImpossibleToPoseAtThisLocationException si la position est hors du terrain
     */
    public void putAt(Position position, Object element, Robot robot) {
        if (robot.isPositionValid(position, this))
            throw new ImpossibleToPoseAtThisLocationException("The item cannot be placed outside the field. Action impossible.");

        this.occupants.put(position, element);

        this.notifyObserver(robot, this);
    }

    /**
     * Retire le robot indiqué de sa position s'il en est l'occupant actuel.
     *
     * @param position position à libérer
     * @param robot robot à retirer
     */
    public void removeRobot(Position position, Robot robot) {
        if (this.occupants.get(position) == robot) this.occupants.remove(position);
    }
}
