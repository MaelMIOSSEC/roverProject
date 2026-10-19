package View;

import Model.Board;
import Model.Ground;
import Model.Position;
import Model.Robot;
import Observer.RoverObserver;
import nicellipse.component.NiEllipse;
import nicellipse.component.NiRectangle;
import nicellipse.component.NiSpace;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Vue Nicellipse de la grille de déplacement et de la position du rover.
 */
public final class RoverMapView extends NiSpace implements RoverObserver {
    private static final int CELL_SIZE = 18;
    private static final Color EMPTY_CELL = new Color(232, 238, 244);
    private static final Color GRID_LINE = new Color(195, 205, 215);
    private static final Color ROVER_COLOR = new Color(38, 166, 125);
    private static final Color DROP_COLOR = new Color(226, 157, 57);

    private final NiSpace boardView = new NiSpace("Grille", new Dimension(390, 390));
    private final JLabel status = new JLabel(" ");

    public RoverMapView(Dimension viewSize) {
        super("Terrain", viewSize);
        setPreferredSize(viewSize);
        setBackground(new Color(248, 250, 252));

        JLabel title = new JLabel("Terrain (20 × 20)");
        title.setBounds(24, 18, 500, 24);
        add(title);

        boardView.setBackground(new Color(248, 250, 252));
        boardView.setBounds(145, 52, 390, 390);
        add(boardView);

        status.setBounds(24, 457, 440, 24);
        add(status);
    }

    public void setScriptAction(Runnable action) {
        JButton button = new JButton("Ouvrir le script");
        button.setBounds(495, 457, 160, 32);
        button.addActionListener(event -> action.run());
        add(button);
    }

    public void showWorld(Board board, Robot robot) {
        boardView.removeAll();
        int offset = (boardView.getWidth() - board.getWidth() * CELL_SIZE) / 2;

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                int cellX = offset + x * CELL_SIZE;
                int cellY = offset + (board.getHeight() - 1 - y) * CELL_SIZE;
                NiRectangle cell = new NiRectangle();
                cell.setBounds(cellX, cellY, CELL_SIZE, CELL_SIZE);
                cell.setBackground(EMPTY_CELL);
                cell.setBorder(BorderFactory.createLineBorder(GRID_LINE));
                boardView.add(cell);

                Object occupant = board.getAt(new Position(x, y));
                if (occupant != null && occupant != robot && !(occupant instanceof Ground)) {
                    NiEllipse marker = new NiEllipse();
                    marker.setBounds(cellX + 5, cellY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    marker.setBackground(DROP_COLOR);
                    marker.setWithBorder(false);
                    boardView.add(marker);
                }
            }
        }

        Position position = robot.getPosition();
        NiEllipse rover = new NiEllipse();
        rover.setBounds(offset + position.getX() * CELL_SIZE + 3,
                offset + (board.getHeight() - 1 - position.getY()) * CELL_SIZE + 3,
                CELL_SIZE - 6, CELL_SIZE - 6);
        rover.setBackground(ROVER_COLOR);
        rover.setWithBorder(false);
        boardView.add(rover);
        boardView.setComponentZOrder(rover, 0);
        boardView.revalidate();
        boardView.repaint();
    }

    public void showStatus(String text, Color color) {
        status.setForeground(color);
        status.setText(text);
    }

    @Override
    public void update(Robot robot, Board board) {
        this.showWorld(board, robot);
    }
}
