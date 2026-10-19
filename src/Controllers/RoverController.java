package Controllers;

import Command.ResMCommand;
import Command.StartMCommand;
import Command.StopMCommand;
import Command.WBackCommand;
import Model.Board;
import Model.Ground;
import Model.Position;
import Model.Robot;
import View.RoverMapView;
import View.RoverScriptView;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Contrôleur qui relie les vues aux modèles Board et Robot.
 */
public final class RoverController {
    private static final int BOARD_SIZE = 20;

    private final RoverMapView mapView;
    private final RoverScriptView scriptView;
    private final Consumer<String> showView;
    private Board board;
    private Robot robot;

    public RoverController(RoverMapView mapView, RoverScriptView scriptView,
                           Consumer<String> showView) {
        this.mapView = mapView;
        this.scriptView = scriptView;
        this.showView = showView;
        mapView.setScriptAction(this::showScript);
        scriptView.setMapAction(this::showMap);
        scriptView.setExecuteAction(this::executeScript);
        scriptView.setResetAction(this::resetWorld);
        scriptView.setStartReplayAction(this::startRecording);
        scriptView.setStopReplayAction(this::stopRecording);
        scriptView.setPlayReplayAction(this::playReplay);
        scriptView.setResetHistoryAction(this::resetHistory);
        scriptView.setWBackAction(this::wBack);
        createWorld();
        updateMap();
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

        robot.attachObserver(mapView);
    }

    private void executeScript() {
        try {
            robot.execute(scriptView.getScript());
            String result = "Position : " + robot.getPosition().getX() + ", "
                    + robot.getPosition().getY() + "  |  " + robot.getDirection();
            setStatus(result, new Color(38, 125, 91));
        } catch (RuntimeException exception) {
            setStatus("Erreur : " + exception.getMessage(), new Color(180, 45, 45));
        }
        showMap();
    }

    private void resetWorld() {
        createWorld();
        setStatus("Terrain réinitialisé.", Color.DARK_GRAY);
        updateMap();
    }

    private void startRecording() {
        new StartMCommand().execute(robot);
        setStatus("Enregistrement de l'historique activé.", new Color(38, 125, 91));
    }

    private void stopRecording() {
        new StopMCommand().execute(robot);
        setStatus("Enregistrement de l'historique désactivé.", Color.DARK_GRAY);
    }

    private void resetHistory() {
        new ResMCommand().execute(robot);
        setStatus("Historique effacé.", Color.DARK_GRAY);
    }

    private void playReplay() {
        try {
            robot.replay();
            String result = "Rejeu terminé | Position : " + robot.getPosition().getX()
                    + ", " + robot.getPosition().getY() + " | Direction : " + robot.getDirection();
            setStatus(result, new Color(38, 125, 91));
        } catch (RuntimeException exception) {
            setStatus("Erreur lors du rejeu : " + exception.getMessage(), new Color(180, 45, 45));
        }
    }

    private void wBack() {
        try {
            new WBackCommand().execute(robot);
            String result = "Retour effectué | Position : " + robot.getPosition().getX()
                    + ", " + robot.getPosition().getY() + " | Direction : " + robot.getDirection();
            setStatus(result, new Color(38, 125, 91));
        } catch (RuntimeException exception) {
            setStatus("Erreur lors du retour : " + exception.getMessage(), new Color(180, 45, 45));
        }
    }

    private void setStatus(String text, Color color) {
        mapView.showStatus(text, color);
        scriptView.showStatus(text, color);
    }

    private void updateMap() {
        mapView.showWorld(board, robot);
    }

    private void showMap() {
        showView.accept("map");
    }

    private void showScript() {
        showView.accept("script");
    }
}
