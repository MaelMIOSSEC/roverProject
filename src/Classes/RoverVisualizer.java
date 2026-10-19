package Classes;

import nicellipse.component.NiEllipse;
import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Fenêtre Swing permettant de saisir des commandes et de visualiser le rover sur son terrain.
 */
public final class RoverVisualizer extends JFrame {
    /** Nombre de cases sur chaque côté du terrain affiché. */
    private static final int BOARD_SIZE = 20;
    /** Taille d'une case affichée en pixels. */
    private static final int CELL_SIZE = 18;
    /** Couleur de fond des cases sans objet. */
    private static final Color EMPTY_CELL = new Color(232, 238, 244);
    /** Couleur des lignes de la grille. */
    private static final Color GRID_LINE = new Color(195, 205, 215);
    /** Couleur du marqueur représentant le rover. */
    private static final Color ROVER_COLOR = new Color(38, 166, 125);

    /** Espace graphique représentant le terrain. */
    private final NiSpace boardView = new NiSpace("Terrain", new Dimension(390, 390));
    /** Zone contenant le script de commandes à exécuter. */
    private final JTextArea scriptArea = new JTextArea(
            "base 3 3\ndir EAST\nmov 5\ndir NORTH\nmov 4\ndrop");
    /** Étiquette de retour d'état affichée sous les boutons. */
    private final JLabel status = new JLabel(" ");
    /** Modèle du terrain actuellement visualisé. */
    private Board board;
    /** Robot actuellement visualisé. */
    private Robot robot;

    /**
     * Construit et initialise la fenêtre de visualisation.
     */
    public RoverVisualizer() {
        super("Visualiseur du rover");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createContent());
        createWorld();
        refreshView();
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Crée le panneau principal, la carte, la zone de script et les commandes de l'interface.
     *
     * @return panneau contenant l'interface complète
     */
    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        JPanel mapPanel = new JPanel(new BorderLayout(0, 8));
        mapPanel.add(new JLabel("Terrain (20 × 20)"), BorderLayout.NORTH);
        boardView.setPreferredSize(new Dimension(390, 390));
        boardView.setBackground(new Color(248, 250, 252));
        mapPanel.add(boardView, BorderLayout.CENTER);
        content.add(mapPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(0, 8));
        controls.setPreferredSize(new Dimension(300, 390));
        controls.add(new JLabel("Script de commandes"), BorderLayout.NORTH);

        scriptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        scriptArea.setTabSize(2);
        scriptArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scrollPane = new JScrollPane(scriptArea);
        controls.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        JButton runButton = new JButton("Exécuter");
        runButton.addActionListener(event -> executeScript());
        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(event -> resetWorld());
        actions.add(runButton);
        actions.add(resetButton);

        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.add(actions, BorderLayout.NORTH);
        footer.add(status, BorderLayout.SOUTH);
        controls.add(footer, BorderLayout.SOUTH);
        content.add(controls, BorderLayout.EAST);
        return content;
    }

    /**
     * Initialise un terrain vide de taille {@value #BOARD_SIZE} et son robot.
     */
    private void createWorld() {
        Map<Position, Ground> grounds = new HashMap<>();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                grounds.put(new Position(x, y), new Ground());
            }
        }
        board = new Board(BOARD_SIZE, BOARD_SIZE, "blue", grounds);
        robot = new Robot(board);
    }

    /**
     * Exécute le script saisi puis affiche le résultat ou le message d'erreur.
     */
    private void executeScript() {
        try {
            robot.execute(scriptArea.getText());
            status.setForeground(new Color(38, 125, 91));
            status.setText("Position : " + robot.getPosition().getX() + ", "
                    + robot.getPosition().getY() + "  |  " + robot.getDirection());
        } catch (RuntimeException exception) {
            status.setForeground(new Color(180, 45, 45));
            status.setText("Erreur : " + exception.getMessage());
        }
        refreshView();
    }

    /**
     * Crée un nouveau terrain et efface le retour d'état précédent.
     */
    private void resetWorld() {
        createWorld();
        status.setForeground(Color.DARK_GRAY);
        status.setText("Terrain réinitialisé.");
        refreshView();
    }

    /**
     * Redessine la grille, les objets déposés et la position du rover.
     */
    private void refreshView() {
        boardView.removeAll();
        boardView.setLayout(null);

        int boardPixels = BOARD_SIZE * CELL_SIZE;
        int offset = (390 - boardPixels) / 2;
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                int cellX = offset + x * CELL_SIZE;
                int cellY = offset + (BOARD_SIZE - 1 - y) * CELL_SIZE;

                NiRectangle cell = new NiRectangle();
                cell.setBounds(cellX, cellY, CELL_SIZE, CELL_SIZE);
                cell.setBackground(EMPTY_CELL);
                cell.setBorder(BorderFactory.createLineBorder(GRID_LINE));
                boardView.add(cell);

                Object occupant = board.getAt(new Position(x, y));
                if (occupant != null && occupant != robot && !(occupant instanceof Ground)) {
                    NiEllipse marker = new NiEllipse();
                    marker.setBounds(cellX + 5, cellY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    marker.setBackground(new Color(226, 157, 57));
                    marker.setWithBorder(false);
                    boardView.add(marker);
                }
            }
        }

        Position position = robot.getPosition();
        NiEllipse rover = new NiEllipse();
        rover.setBounds(offset + position.getX() * CELL_SIZE + 3,
                offset + (BOARD_SIZE - 1 - position.getY()) * CELL_SIZE + 3,
                CELL_SIZE - 6, CELL_SIZE - 6);
        rover.setBackground(ROVER_COLOR);
        rover.setWithBorder(false);
        boardView.add(rover);
        boardView.setComponentZOrder(rover, 0);
        boardView.revalidate();
        boardView.repaint();
    }
}
