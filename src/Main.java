import View.RoverVisualizer;

import javax.swing.SwingUtilities;

/**
 * Point d'entrée de l'application de visualisation du rover.
 */
public class Main {
    /**
     * Lance la fenêtre graphique sur le thread d'événements Swing.
     *
     * @param args arguments de ligne de commande, non utilisés
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoverVisualizer().setVisible(true));
    }
}