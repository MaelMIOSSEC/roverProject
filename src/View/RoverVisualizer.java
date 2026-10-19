package View;

import Controllers.RoverController;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Fenêtre qui assemble les vues et le contrôleur de l'application.
 */
public final class RoverVisualizer extends JFrame {
    private static final Dimension VIEW_SIZE = new Dimension(800, 640);

    public RoverVisualizer() {
        super("Visualiseur du rover");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        RoverMapView mapView = new RoverMapView(VIEW_SIZE);
        RoverScriptView scriptView = new RoverScriptView(VIEW_SIZE);
        JPanel views = new JPanel(new CardLayout());
        views.add(mapView, "map");
        views.add(scriptView, "script");
        views.setPreferredSize(VIEW_SIZE);

        CardLayout layout = (CardLayout) views.getLayout();
        new RoverController(mapView, scriptView, view -> layout.show(views, view));
        setContentPane(views);
        pack();
        setLocationRelativeTo(null);
    }
}
