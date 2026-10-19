package View;

import nicellipse.component.NiSpace;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * Vue Nicellipse de saisie et d'exécution du script de commandes.
 */
public final class RoverScriptView extends NiSpace {
    private final JTextArea scriptArea = new JTextArea(
            "base 3 3\ndir EAST\nmov 5\ndir NORTH\nmov 4\ndrop"
    );
    private final JLabel status = new JLabel(" ");
    private final JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 8, 8));
    private final JButton mapButton = new JButton("Voir la grille");
    private Runnable executeAction = () -> { };
    private Runnable resetAction = () -> { };
    private Runnable startReplayAction = () -> { };
    private Runnable stopReplayAction = () -> { };
    private Runnable playReplayAction = () -> { };
    private Runnable resetHistoryAction = () -> { };
    private Runnable wBackAction = () -> { };

    public RoverScriptView(Dimension viewSize) {
        super("Script de commandes", viewSize);
        setPreferredSize(viewSize);
        setBackground(new Color(248, 250, 252));

        JLabel title = new JLabel("Script de commandes");
        title.setBounds(24, 18, 500, 24);
        add(title);

        scriptArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        scriptArea.setTabSize(2);
        scriptArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scrollPane = new JScrollPane(scriptArea);
        scrollPane.setBounds(24, 52, 750, 360);
        add(scrollPane);

        buttonPanel.setOpaque(false);
        buttonPanel.setBounds(24, 426, 750, 140);
        add(buttonPanel);

        JButton runButton = new JButton("Exécuter");
        runButton.addActionListener(event -> executeAction.run());
        buttonPanel.add(runButton);

        JButton resetButton = new JButton("Réinitialiser");
        resetButton.addActionListener(event -> resetAction.run());
        buttonPanel.add(resetButton);

        buttonPanel.add(mapButton);

        JButton startButton = new JButton("Lancer l'enregistrement");
        startButton.addActionListener(event -> startReplayAction.run());
        buttonPanel.add(startButton);

        JButton stopButton = new JButton("Arrêter l'enregistrement");
        stopButton.addActionListener(event -> stopReplayAction.run());
        buttonPanel.add(stopButton);

        JButton playButton = new JButton("Jouer l'enregistrement");
        playButton.addActionListener(event -> playReplayAction.run());
        buttonPanel.add(playButton);

        JButton wBackButton = new JButton("Jouer l'historique inversé");
        wBackButton.addActionListener(event -> wBackAction.run());
        buttonPanel.add(wBackButton);

        JButton resetHistoryButton = new JButton("Effacer l'historique");
        resetHistoryButton.addActionListener(event -> resetHistoryAction.run());
        buttonPanel.add(resetHistoryButton);
        buttonPanel.add(new JPanel());

        status.setBounds(24, 580, 750, 24);
        add(status);
    }

    public String getScript() {
        return scriptArea.getText();
    }

    public void setExecuteAction(Runnable action) {
        executeAction = action;
    }

    public void setResetAction(Runnable action) {
        resetAction = action;
    }

    public void setStartReplayAction(Runnable action) { startReplayAction = action; }

    public void setStopReplayAction(Runnable action) { stopReplayAction = action; }

    public void setPlayReplayAction(Runnable action) { playReplayAction = action; }

    public void setResetHistoryAction(Runnable action) { resetHistoryAction = action; }

    public void setWBackAction(Runnable action) { wBackAction = action; }

    public void setMapAction(Runnable action) {
        mapButton.addActionListener(event -> action.run());
    }

    public void showStatus(String text, Color color) {
        status.setForeground(color);
        status.setText(text);
    }
}
