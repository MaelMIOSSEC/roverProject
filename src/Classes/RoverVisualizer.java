package Classes;

import Enumerators.Direction;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RoverVisualizer extends JFrame {
    private static final int BOARD_SIZE = 20;
    private static final Color BACKGROUND = new Color(12, 18, 32);
    private static final Color PANEL = new Color(20, 29, 46);
    private static final Color MUTED = new Color(145, 161, 183);
    private static final Color ACCENT = new Color(49, 214, 160);

    private final JTextArea scriptArea = new JTextArea("base 3 3\ndir EAST\nmov 5\ndir NORTH\nmov 4\ndrop\ndir WEST\nmov 2");
    private final JTextArea activityArea = new JTextArea();
    private final JLabel positionLabel = new JLabel();
    private final JLabel directionLabel = new JLabel();
    private final JLabel historyLabel = new JLabel();
    private final JLabel recordingLabel = new JLabel();
    private final JLabel messageLabel = new JLabel("Prêt pour la mission.");
    private final BoardPanel boardPanel = new BoardPanel();
    private final List<Position> trail = new ArrayList<>();

    private Board board;
    private Robot robot;
    private String loadedScript = "";
    private String[] scriptLines = new String[0];
    private int scriptIndex;

    public RoverVisualizer() {
        super("Rover — visualiseur de mission");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 760));
        setSize(1420, 900);
        setLocationRelativeTo(null);

        createWorld();
        setContentPane(createContent());
        updateDashboard();
    }

    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        root.add(createHeader(), BorderLayout.NORTH);

        JPanel workspace = new JPanel(new BorderLayout(20, 0));
        workspace.setOpaque(false);
        boardPanel.setPreferredSize(new Dimension(760, 650));
        workspace.add(boardPanel, BorderLayout.CENTER);
        workspace.add(createSidebar(), BorderLayout.EAST);
        root.add(workspace, BorderLayout.CENTER);
        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);
        JLabel title = label("ROVER  /  MISSION CONTROL", 22, Color.WHITE, Font.BOLD);
        JLabel subtitle = label("Visualisez le terrain, pilotez le rover et exécutez vos scripts.", 13, MUTED, Font.PLAIN);
        titleBlock.add(title);
        titleBlock.add(subtitle);
        header.add(titleBlock, BorderLayout.WEST);

        JPanel metrics = new JPanel(new GridLayout(1, 3, 10, 0));
        metrics.setOpaque(false);
        metrics.add(metric("POSITION", positionLabel));
        metrics.add(metric("CAP", directionLabel));
        metrics.add(metric("HISTORIQUE", historyLabel));
        header.add(metrics, BorderLayout.EAST);
        return header;
    }

    private JPanel metric(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 3));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        card.add(label(title, 10, MUTED, Font.BOLD));
        value.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        value.setForeground(Color.WHITE);
        card.add(value);
        return card;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(340, 0));
        sidebar.setLayout(new javax.swing.BoxLayout(sidebar, javax.swing.BoxLayout.Y_AXIS));

        JPanel scriptCard = card("SCRIPT DE MISSION", "Commandes exécutées dans l’ordre, une ligne à la fois.");
        scriptArea.setBackground(new Color(13, 20, 34));
        scriptArea.setForeground(new Color(220, 231, 244));
        scriptArea.setCaretColor(ACCENT);
        scriptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        scriptArea.setTabSize(2);
        scriptArea.setBorder(BorderFactory.createEmptyBorder(10, 11, 10, 11));
        scriptArea.setRows(8);
        JScrollPane scriptScroll = new JScrollPane(scriptArea);
        scriptScroll.setBorder(BorderFactory.createLineBorder(new Color(40, 54, 76)));
        scriptCard.add(scriptScroll, BorderLayout.CENTER);

        JPanel scriptButtons = new JPanel(new GridLayout(1, 2, 9, 0));
        scriptButtons.setOpaque(false);
        JButton runButton = button("▶  Exécuter", ACCENT, BACKGROUND);
        runButton.addActionListener(event -> runScript());
        JButton stepButton = button("Étape suivante", new Color(39, 52, 73), Color.WHITE);
        stepButton.addActionListener(event -> stepScript());
        scriptButtons.add(runButton);
        scriptButtons.add(stepButton);
        scriptCard.add(scriptButtons, BorderLayout.SOUTH);
        scriptCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        sidebar.add(scriptCard);
        sidebar.add(javax.swing.Box.createVerticalStrut(14));

        JPanel controlsCard = card("PILOTAGE RAPIDE", "Déplace le rover d’une case dans la direction choisie.");
        JPanel pad = new JPanel(new GridBagLayout());
        pad.setOpaque(false);
        addDirectionButton(pad, "↑", Direction.NORTH, 1, 0);
        addDirectionButton(pad, "←", Direction.WEST, 0, 1);
        addDirectionButton(pad, "→", Direction.EAST, 2, 1);
        addDirectionButton(pad, "↓", Direction.SOUTH, 1, 2);
        controlsCard.add(pad, BorderLayout.CENTER);
        controlsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        sidebar.add(controlsCard);
        sidebar.add(javax.swing.Box.createVerticalStrut(14));

        JPanel activityCard = card("JOURNAL DE BORD", "Dernières actions et erreurs de commande.");
        activityArea.setEditable(false);
        activityArea.setLineWrap(true);
        activityArea.setWrapStyleWord(true);
        activityArea.setBackground(new Color(13, 20, 34));
        activityArea.setForeground(new Color(190, 205, 222));
        activityArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        activityArea.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        JScrollPane activityScroll = new JScrollPane(activityArea);
        activityScroll.setBorder(BorderFactory.createLineBorder(new Color(40, 54, 76)));
        activityCard.add(activityScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(8, 0));
        footer.setOpaque(false);
        recordingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        footer.add(recordingLabel, BorderLayout.WEST);
        JButton resetButton = button("Réinitialiser", new Color(39, 52, 73), Color.WHITE);
        resetButton.addActionListener(event -> resetWorld());
        footer.add(resetButton, BorderLayout.EAST);
        activityCard.add(footer, BorderLayout.SOUTH);
        sidebar.add(activityCard);
        sidebar.add(javax.swing.Box.createVerticalGlue());

        return sidebar;
    }

    private JPanel card(String title, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 4));
        heading.setOpaque(false);
        heading.add(label(title, 12, Color.WHITE, Font.BOLD));
        heading.add(label(description, 11, MUTED, Font.PLAIN));
        card.add(heading, BorderLayout.NORTH);
        return card;
    }

    private void addDirectionButton(JPanel pad, String text, Direction direction, int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(4, 4, 4, 4);
        JButton moveButton = button(text, new Color(39, 52, 73), Color.WHITE);
        moveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
        moveButton.setPreferredSize(new Dimension(54, 42));
        moveButton.addActionListener(event -> executeCommand("dir " + direction.name() + "\nmov 1"));
        pad.add(moveButton, constraints);
    }

    private void runScript() {
        prepareScript();
        if (scriptIndex >= scriptLines.length) {
            scriptIndex = 0;
        }
        while (scriptIndex < scriptLines.length) {
            if (!executeScriptLine()) {
                break;
            }
        }
    }

    private void stepScript() {
        prepareScript();
        if (scriptIndex >= scriptLines.length) {
            messageLabel.setText("Le script est terminé. Modifiez-le ou réinitialisez le rover.");
            return;
        }
        executeScriptLine();
    }

    private void prepareScript() {
        String currentScript = scriptArea.getText();
        if (!currentScript.equals(loadedScript)) {
            loadedScript = currentScript;
            scriptLines = currentScript.split("\\R");
            scriptIndex = 0;
        }
    }

    private boolean executeScriptLine() {
        while (scriptIndex < scriptLines.length && scriptLines[scriptIndex].isBlank()) {
            scriptIndex++;
        }
        if (scriptIndex >= scriptLines.length) {
            messageLabel.setText("Script terminé.");
            return false;
        }

        String line = scriptLines[scriptIndex++].trim();
        return executeCommand(line);
    }

    private boolean executeCommand(String command) {
        try {
            robot.execute(command);
            Position position = robot.getPosition();
            trail.add(new Position(position.getX(), position.getY()));
            appendActivity("✓  " + command.replace('\n', ' '));
            messageLabel.setText("Commande exécutée : " + command.replace('\n', " · "));
            updateDashboard();
            return true;
        } catch (RuntimeException exception) {
            String error = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
            appendActivity("✕  " + command.replace('\n', ' ') + " — " + error);
            messageLabel.setText("Erreur : " + error);
            updateDashboard();
            return false;
        }
    }

    private void createWorld() {
        Map<Position, Ground> grounds = new HashMap<>();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                grounds.put(new Position(x, y), new Ground());
            }
        }
        board = new Board(BOARD_SIZE, BOARD_SIZE, "blue", grounds);
        robot = new Robot(board);
        trail.clear();
        trail.add(new Position(robot.getPosition().getX(), robot.getPosition().getY()));
    }

    private void resetWorld() {
        createWorld();
        scriptIndex = 0;
        activityArea.setText("");
        messageLabel.setText("Terrain réinitialisé. Prêt pour la mission.");
        updateDashboard();
    }

    private void appendActivity(String message) {
        activityArea.append(message + System.lineSeparator());
        activityArea.setCaretPosition(activityArea.getDocument().getLength());
    }

    private void updateDashboard() {
        Position position = robot.getPosition();
        positionLabel.setText(position.getX() + ", " + position.getY());
        directionLabel.setText(robot.getDirection().name());
        historyLabel.setText(robot.getCommandsList().size() + " commandes");
        recordingLabel.setText(robot.isRecording() ? "●  ENREGISTREMENT ACTIF" : "●  ENREGISTREMENT PAUSÉ");
        recordingLabel.setForeground(robot.isRecording() ? ACCENT : new Color(246, 185, 75));
        boardPanel.repaint();
    }

    private static JLabel label(String text, int size, Color color, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, style, size));
        label.setForeground(color);
        return label;
    }

    private static JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private final class BoardPanel extends JPanel {
        private BoardPanel() {
            setBackground(PANEL);
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int padding = 38;
            int cell = Math.min((getWidth() - padding * 2) / board.getWidth(),
                    (getHeight() - padding * 2) / board.getHeight());
            if (cell < 1) {
                g.dispose();
                return;
            }

            int boardWidth = cell * board.getWidth();
            int boardHeight = cell * board.getHeight();
            int left = (getWidth() - boardWidth) / 2;
            int top = (getHeight() - boardHeight) / 2;

            g.setColor(new Color(8, 13, 23));
            g.fillRoundRect(left - 3, top - 3, boardWidth + 6, boardHeight + 6, 12, 12);
            for (int y = 0; y < board.getHeight(); y++) {
                for (int x = 0; x < board.getWidth(); x++) {
                    int screenX = left + x * cell;
                    int screenY = top + (board.getHeight() - 1 - y) * cell;
                    g.setColor((x + y) % 2 == 0 ? new Color(25, 37, 55) : new Color(28, 41, 60));
                    g.fillRect(screenX, screenY, cell, cell);
                    g.setColor(new Color(42, 56, 76));
                    g.drawRect(screenX, screenY, cell, cell);

                    Object occupant = board.getAt(new Position(x, y));
                    if (occupant != null && occupant != robot && !(occupant instanceof Ground)) {
                        g.setColor(new Color(246, 185, 75));
                        int marker = Math.max(5, cell / 4);
                        g.fillOval(screenX + (cell - marker) / 2, screenY + (cell - marker) / 2, marker, marker);
                    }
                }
            }

            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            g.setColor(MUTED);
            for (int x = 0; x < board.getWidth(); x += 5) {
                String text = Integer.toString(x);
                int textWidth = g.getFontMetrics().stringWidth(text);
                g.drawString(text, left + x * cell + (cell - textWidth) / 2, top + boardHeight + 17);
            }
            for (int y = 0; y < board.getHeight(); y += 5) {
                String text = Integer.toString(y);
                int textWidth = g.getFontMetrics().stringWidth(text);
                g.drawString(text, left - textWidth - 8, top + (board.getHeight() - 1 - y) * cell + cell / 2 + 4);
            }

            drawTrail(g, left, top, cell);
            drawRobot(g, left, top, cell);
            g.dispose();
        }

        private void drawTrail(Graphics2D g, int left, int top, int cell) {
            if (trail.size() < 2) {
                return;
            }
            g.setColor(new Color(49, 214, 160, 110));
            g.setStroke(new BasicStroke(Math.max(2, cell / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < trail.size(); i++) {
                Position from = trail.get(i - 1);
                Position to = trail.get(i);
                int x1 = left + from.getX() * cell + cell / 2;
                int y1 = top + (board.getHeight() - 1 - from.getY()) * cell + cell / 2;
                int x2 = left + to.getX() * cell + cell / 2;
                int y2 = top + (board.getHeight() - 1 - to.getY()) * cell + cell / 2;
                g.drawLine(x1, y1, x2, y2);
            }
        }

        private void drawRobot(Graphics2D g, int left, int top, int cell) {
            Position position = robot.getPosition();
            int centerX = left + position.getX() * cell + cell / 2;
            int centerY = top + (board.getHeight() - 1 - position.getY()) * cell + cell / 2;
            int diameter = Math.max(12, cell * 3 / 4);

            g.setColor(new Color(49, 214, 160, 45));
            g.fillOval(centerX - diameter / 2 - 4, centerY - diameter / 2 - 4, diameter + 8, diameter + 8);
            g.setColor(ACCENT);
            g.fillOval(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter);

            Graphics2D heading = (Graphics2D) g.create();
            double angle = switch (robot.getDirection()) {
                case NORTH -> 0;
                case EAST -> Math.PI / 2;
                case SOUTH -> Math.PI;
                case WEST -> -Math.PI / 2;
            };
            heading.rotate(angle, centerX, centerY);
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(centerX, centerY - diameter * 0.34);
            arrow.lineTo(centerX + diameter * 0.22, centerY + diameter * 0.18);
            arrow.lineTo(centerX, centerY + diameter * 0.08);
            arrow.lineTo(centerX - diameter * 0.22, centerY + diameter * 0.18);
            arrow.closePath();
            heading.setColor(new Color(12, 35, 34));
            heading.fill(arrow);
            heading.dispose();
        }
    }
}
