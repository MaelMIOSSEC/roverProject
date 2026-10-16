import Classes.RoverVisualizer;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoverVisualizer().setVisible(true));
    }
}