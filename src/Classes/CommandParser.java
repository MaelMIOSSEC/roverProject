package Classes;

import Commands.*;
import Enumerators.Direction;
import Interfaces.Command;
import Interfaces.CommandFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {
    Map<String, CommandFactory> factories = new HashMap<String, CommandFactory>();

    public CommandParser() {
        this.factories.put("base", args -> new BaseCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
        this.factories.put("dir", args -> new DirectionCommand(Direction.valueOf(args[1])));
        this.factories.put("mov", args -> new MoveCommand(Integer.parseInt(args[1])));
        this.factories.put("drop", args -> new DropCommand());
        this.factories.put("startM", args -> new StartMCommand());
        this.factories.put("stopM", args -> new StopMCommand());
        this.factories.put("resM", args -> new ResMCommand());
        this.factories.put("replay", args -> new ReplayCommand());
        this.factories.put("wback", args -> new WBackCommand());
    }

    public Command parseLine(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return null;
        }

        String[] args = trimmedLine.split("\\s+");
        String commandName = args[0];

        CommandFactory factory = this.factories.get(commandName);
        if (factory != null) {
            return factory.create(args);
        } else {
            throw new IllegalArgumentException("Commande inconnue : " + commandName);
        }
    }

    public List<Command> parseScript(String script) {
        List<Command> commands = new ArrayList<>();

        for (String line : script.split("\r?\n")) {
            Command command = parseLine(line);

            if (command != null) {
                commands.add(command);
            }
        }

        return commands;
    }
}
