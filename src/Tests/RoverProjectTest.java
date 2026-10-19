package Tests;

import Command.*;
import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du terrain, du robot, du parseur et des commandes.
 */
public class RoverProjectTest {

    /** Terrain utilisé par les tests. */
    private Board board;
    /** Robot associé au terrain de test. */
    private Robot robot;
    /** Parseur utilisé pour tester les scripts. */
    private CommandParser parser;

    /**
     * Initialise un terrain de 100 cases par 100 et les objets de test associés.
     */
    @BeforeEach
    public void setUp() {
        Map<Position, Ground> grounds = new HashMap<>();
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                grounds.put(new Position(x, y), new Ground());
            }
        }
        board = new Board(100, 100, "blue", grounds);
        robot = new Robot(board);
        parser = new CommandParser();
    }

    /** Vérifie que le robot se déplace et que l'occupation du terrain suit sa position. */
    @Test
    public void firstTest() {
        robot.setPosition(new Position(4, 1));
        robot.move(10);

        assertEquals(new Position(14, 1), robot.getPosition());
        assertSame(robot, board.getAt(new Position(14, 1)));
    }

    /** Vérifie l'exécution d'un script combinant positionnement, directions et déplacements. */
    @Test
    public void testScriptEtape2() {
        String script = "base 4 1\ndir EAST\nmov 2\nmov 3\ndir NORTH\nmov 3";

        robot.execute(script);

        assertEquals(new Position(9, 4), robot.getPosition());
        assertSame(robot, board.getAt(new Position(9, 4)));
    }

    /** Vérifie séparément les commandes de positionnement, d'orientation et de déplacement. */
    @Test
    public void testCommandesScriptIndividuelles() {
        robot.execute("base 2 5");
        assertEquals(new Position(2, 5), robot.getPosition());

        robot.execute("dir WEST");
        assertEquals(Direction.WEST, robot.getDirection());

        robot.execute("mov 2");
        assertEquals(new Position(0, 5), robot.getPosition());
    }

    /** Vérifie qu'une commande inconnue est rejetée. */
    @Test
    public void testCommandeInvalide() {
        assertThrows(IllegalArgumentException.class, () -> {
            robot.execute("inconnu 10");
        });
    }

    // ==========================================
    // TESTS INDIVIDUELS DES CLASSES DE COMMANDES
    // ==========================================

    /** Vérifie le positionnement du robot par la commande dédiée. */
    @Test
    public void testBaseCommand() {
        Command command = new BaseCommand(12, 8);
        command.execute(robot);

        assertEquals(new Position(12, 8), robot.getPosition());
        assertSame(robot, board.getAt(new Position(12, 8)));
    }

    /** Vérifie le changement de direction par la commande dédiée. */
    @Test
    public void testDirectionCommand() {
        Command command = new DirectionCommand(Direction.SOUTH);
        command.execute(robot);

        assertEquals(Direction.SOUTH, robot.getDirection());
    }

    /** Vérifie le déplacement du robot par la commande dédiée. */
    @Test
    public void testMoveCommand() {
        robot.setPosition(new Position(5, 5));
        robot.setDirection(Direction.EAST);

        Command command = new MoveCommand(7);
        command.execute(robot);

        assertEquals(new Position(12, 5), robot.getPosition());
        assertSame(robot, board.getAt(new Position(12, 5)));
    }

    /** Vérifie qu'un objet est déposé sur la position courante du robot. */
    @Test
    public void testDropCommand() {
        robot.setPosition(new Position(3, 3));

        Command command = new DropCommand();
        command.execute(robot);

        assertNotNull(board.getAt(new Position(3, 3)));
        assertNotEquals(robot, board.getAt(new Position(3, 3)));
    }

    // ==========================================
    // TESTS DU PARSER DE COMMANDES
    // ==========================================

    /** Vérifie que le parseur construit les types de commandes attendus. */
    @Test
    public void testCommandParserParseLine() {
        Command baseCmd = parser.parseLine("base 10 20");
        assertInstanceOf(BaseCommand.class, baseCmd);

        Command dirCmd = parser.parseLine("dir NORTH");
        assertInstanceOf(DirectionCommand.class, dirCmd);

        Command movCmd = parser.parseLine("mov 5");
        assertInstanceOf(MoveCommand.class, movCmd);

        Command dropCmd = parser.parseLine("drop");
        assertInstanceOf(DropCommand.class, dropCmd);

        Command startMCmd = parser.parseLine("startM");
        assertInstanceOf(StartMCommand.class, startMCmd);

        Command stopMCmd = parser.parseLine("stopM");
        assertInstanceOf(StopMCommand.class, stopMCmd);

        Command resMCmd = parser.parseLine("resM");
        assertInstanceOf(ResMCommand.class, resMCmd);

        Command replayCmd = parser.parseLine("replay");
        assertInstanceOf(ReplayCommand.class, replayCmd);
    }

    // ==========================================
    // TESTS DE L'ÉTAPE 4 (MACRO & REPLAY)
    // ==========================================

    /** Vérifie que les commandes exécutées sont enregistrées dans leur ordre d'exécution. */
    @Test
    public void testEnregistrementCommandesOrdre() {
        robot.execute("dir EAST\nmov 5\ndir NORTH\nmov 3");

        assertEquals(4, robot.getCommandsList().size());
        assertInstanceOf(DirectionCommand.class, robot.getCommandsList().get(0));
        assertInstanceOf(MoveCommand.class, robot.getCommandsList().get(1));
        assertInstanceOf(DirectionCommand.class, robot.getCommandsList().get(2));
        assertInstanceOf(MoveCommand.class, robot.getCommandsList().get(3));
    }

    /** Vérifie que la désactivation de l'enregistrement empêche les ajouts ultérieurs. */
    @Test
    public void testStopMEmpecheEnregistrement() {
        robot.execute("mov 5\nstopM\nmov 3");

        assertEquals(1, robot.getCommandsList().size());
        assertEquals(new Position(8, 0), robot.getPosition());
    }

    /** Vérifie que la commande de réinitialisation vide l'historique. */
    @Test
    public void testResMVideHistorique() {
        robot.execute("mov 5\ndir NORTH\nmov 2");
        assertFalse(robot.getCommandsList().isEmpty());

        robot.execute("resM");
        assertTrue(robot.getCommandsList().isEmpty());
    }

    /** Vérifie que les commandes de contrôle de l'historique ne sont pas enregistrées. */
    @Test
    public void testNonEnregistrementCommandesControle() {
        robot.execute("startM\nmov 5\nstopM\nresM\nreplay");

        assertTrue(robot.getCommandsList().isEmpty());
    }

    /** Vérifie que l'historique peut être rejoué depuis une nouvelle position. */
    @Test
    public void testReplayParcours() {
        robot.setPosition(new Position(4, 1));

        robot.execute("dir EAST\nmov 2\nmov 3\ndir NORTH\nmov 3");
        assertEquals(new Position(9, 4), robot.getPosition());

        robot.setPosition(new Position(5, 1));
        robot.setDirection(Direction.EAST);

        robot.replay();

        assertEquals(new Position(10, 4), robot.getPosition());
        assertSame(robot, board.getAt(new Position(10, 4)));
    }

    /** Vérifie que le retour arrière annule le parcours enregistré. */
    @Test
    public void testWBackCommand() {
        robot.execute("mov 5\ndir NORTH\nmov 3");
        assertEquals(new Position(5, 3), robot.getPosition());
        assertEquals(Direction.NORTH, robot.getDirection());

        robot.execute("wback");

        assertEquals(new Position(0, 0), robot.getPosition());
    }

    // ==========================================
    // TESTS DE L'ÉTAPE 6 (OPTIMISATION DE PATH)
    // ==========================================

    /** Vérifie la fusion de deux déplacements consécutifs. */
    @Test
    public void testDeuxDeplacementsSuccessifsFusionnes() {
        List<Command> original = parser.parseScript("mov 4\nmov 6");
        List<Command> optimise = robot.optimizePath(original);

        assertEquals(1, optimise.size());
        assertInstanceOf(MoveCommand.class, optimise.get(0));
        assertEquals(10, ((MoveCommand) optimise.get(0)).distance());
    }

    /** Vérifie que plusieurs changements de direction consécutifs gardent le dernier. */
    @Test
    public void testDeuxChangementsDirectionSuccessifsFusionnes() {
        List<Command> original = parser.parseScript("dir NORTH\ndir WEST");
        List<Command> optimise = robot.optimizePath(original);

        assertEquals(1, optimise.size());
        assertInstanceOf(DirectionCommand.class, optimise.get(0));
        assertEquals(Direction.WEST, ((DirectionCommand) optimise.get(0)).getDirection());
    }

    /** Vérifie que des commandes de classes différentes ne sont pas fusionnées. */
    @Test
    public void testCommandesIncompatiblesRestentDistinctes() {
        List<Command> original = parser.parseScript("mov 5\ndir NORTH\ndrop");
        List<Command> optimise = robot.optimizePath(original);

        assertEquals(3, optimise.size());
        assertInstanceOf(MoveCommand.class, optimise.get(0));
        assertInstanceOf(DirectionCommand.class, optimise.get(1));
        assertInstanceOf(DropCommand.class, optimise.get(2));
    }

    /** Vérifie la fusion d'une séquence de plusieurs déplacements. */
    @Test
    public void testSequenceTroisDeplacementsOuPlusOptimisee() {
        List<Command> original = parser.parseScript("mov 2\nmov 3\nmov 5\nmov -1");
        List<Command> optimise = robot.optimizePath(original);

        assertEquals(1, optimise.size());
        assertInstanceOf(MoveCommand.class, optimise.get(0));
        assertEquals(9, ((MoveCommand) optimise.get(0)).distance());
    }
}