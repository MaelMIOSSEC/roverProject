package Model;

import java.util.Objects;

/**
 * Coordonnées entières d'une case dans le terrain.
 */
public class Position {
    /** Coordonnée horizontale. */
    private int x;
    /** Coordonnée verticale. */
    private int y;

    /**
     * Crée une position à partir de ses coordonnées.
     *
     * @param x coordonnée horizontale
     * @param y coordonnée verticale
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** @return coordonnée horizontale */
    public int getX() { return x; }
    /**
     * Modifie la coordonnée horizontale.
     *
     * @param x nouvelle coordonnée horizontale
     */
    public void setX(int x) { this.x = x; }

    /** @return coordonnée verticale */
    public int getY() { return y; }
    /**
     * Modifie la coordonnée verticale.
     *
     * @param y nouvelle coordonnée verticale
     */
    public void setY(int y) { this.y = y; }

    /**
     * Compare cette position à un autre objet.
     *
     * @param o objet à comparer
     * @return {@code true} si l'objet est une position de mêmes coordonnées
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    /**
     * Calcule le code de hachage à partir des coordonnées.
     *
     * @return code de hachage de cette position
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
