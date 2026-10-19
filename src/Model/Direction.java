package Model;

/**
 * Directions cardinales possibles pour le déplacement du robot.
 */
public enum Direction {
    /** Nord, vers les coordonnées verticales croissantes. */
    NORTH,
    /** Sud, vers les coordonnées verticales décroissantes. */
    SOUTH,
    /** Est, vers les coordonnées horizontales croissantes. */
    EAST,
    /** Ouest, vers les coordonnées horizontales décroissantes. */
    WEST;

    /**
     * Renvoie la direction opposée à celle-ci.
     *
     * @return direction cardinale opposée
     */
    public Direction getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }
}
