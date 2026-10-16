package Tests;

import Classes.*;
import Commands.*;
import Enumerators.Direction;
import Interfaces.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RoverProjectTest {

    private Board board;
    private Robot robot;
    private CommandParser parser;

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

    @Test
    public void firstTest() {
        robot.setPosition(new Position(4, 1));
        robot.move(10);

        assertEquals(new Position(14, 1), robot.getPosition());
        assertSame(robot, board.getAt(new Position(14, 1)));
    }

    @Test
    public void testScriptEtape2() {
        String script = "base 4 1\ndir EAST\nmov 2\nmov 3\ndir NORTH\nmov 3";

        robot.execute(script);

        assertEquals(new Position(9, 4), robot.getPosition());
        assertSame(robot, board.getAt(new Position(9, 4)));
    }

    @Test
    public void testCommandesScriptIndividuelles() {
        robot.execute("base 2 5");
        assertEquals(new Position(2, 5), robot.getPosition());

        robot.execute("dir WEST");
        assertEquals(Direction.WEST, robot.getDirection());

        robot.execute("mov 2");
        assertEquals(new Position(0, 5), robot.getPosition());
    }

    @Test
    public void testCommandeInvalide() {
        assertThrows(IllegalArgumentException.class, () -> {
            robot.execute("inconnu 10");
        });
    }

    // ==========================================
    // TESTS INDIVIDUELS DES CLASSES DE COMMANDES
    // ==========================================

    @Test
    public void testBaseCommand() {
        Command command = new BaseCommand(12, 8);
        command.execute(robot);

        assertEquals(new Position(12, 8), robot.getPosition());
        assertSame(robot, board.getAt(new Position(12, 8)));
    }

    @Test
    public void testDirectionCommand() {
        Command command = new DirectionCommand(Direction.SOUTH);
        command.execute(robot);

        assertEquals(Direction.SOUTH, robot.getDirection());
    }

    @Test
    public void testMoveCommand() {
        robot.setPosition(new Position(5, 5));
        robot.setDirection(Direction.EAST);

        Command command = new MoveCommand(7);
        command.execute(robot);

        assertEquals(new Position(12, 5), robot.getPosition());
        assertSame(robot, board.getAt(new Position(12, 5)));
    }

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

    @Test
    public void testEnregistrementCommandesOrdre() {
        robot.execute("dir EAST\nmov 5\ndir NORTH\nmov 3");

        assertEquals(4, robot.getCommandsList().size());
        assertInstanceOf(DirectionCommand.class, robot.getCommandsList().get(0));
        assertInstanceOf(MoveCommand.class, robot.getCommandsList().get(1));
        assertInstanceOf(DirectionCommand.class, robot.getCommandsList().get(2));
        assertInstanceOf(MoveCommand.class, robot.getCommandsList().get(3));
    }

    @Test
    public void testStopMEmpecheEnregistrement() {
        robot.execute("mov 5\nstopM\nmov 3");

        assertEquals(1, robot.getCommandsList().size());
        assertEquals(new Position(8, 0), robot.getPosition());
    }

    @Test
    public void testResMVideHistorique() {
        robot.execute("mov 5\ndir NORTH\nmov 2");
        assertFalse(robot.getCommandsList().isEmpty());

        robot.execute("resM");
        assertTrue(robot.getCommandsList().isEmpty());
    }

    @Test
    public void testNonEnregistrementCommandesControle() {
        robot.execute("startM\nmov 5\nstopM\nresM\nreplay");

        assertTrue(robot.getCommandsList().isEmpty());
    }

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

    @Test
    public void testWBackCommand() {
        robot.execute("mov 5\ndir NORTH\nmov 3");
        assertEquals(new Position(5, 3), robot.getPosition());
        assertEquals(Direction.NORTH, robot.getDirection());

        robot.execute("wback");

        assertEquals(new Position(0, 0), robot.getPosition());
    }
}