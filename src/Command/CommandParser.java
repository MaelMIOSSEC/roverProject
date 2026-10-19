package Command;

import Model.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transforme les lignes d'un script en commandes exécutables.
 */
public class CommandParser {
    /** Fabriques associant les mots-clés du script à leurs commandes. */
    Map<String, CommandFactory> factories = new HashMap<String, CommandFactory>();

    /**
     * Initialise les fabriques de toutes les commandes reconnues par le parseur.
     */
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

    /**
     * Analyse une ligne et construit la commande correspondante.
     *
     * @param line ligne contenant un mot-clé et ses éventuels arguments
     * @return la commande analysée, ou {@code null} si la ligne est vide
     * @throws IllegalArgumentException si le mot-clé de commande est inconnu
     * @throws NumberFormatException si un argument numérique n'est pas un entier valide
     * @throws IllegalArgumentException si une direction fournie n'est pas reconnue
     */
    public Command parseLine(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) return null;

        String[] args = trimmedLine.split("\\s+");
        String commandName = args[0];

        CommandFactory factory = this.factories.get(commandName);

        if (factory != null) return factory.create(args);
        else throw new IllegalArgumentException("Commande inconnue : " + commandName);
    }

    /**
     * Analyse toutes les lignes d'un script et ignore les lignes vides.
     *
     * @param script texte du script à analyser
     * @return les commandes du script, dans leur ordre d'apparition
     * @throws IllegalArgumentException si une ligne contient une commande inconnue
     * @throws NumberFormatException si un argument numérique n'est pas un entier valide
     */
    public List<Command> parseScript(String script) {
        List<Command> commands = new ArrayList<>();

        for (String line : script.split("\r?\n")) {
            Command command = parseLine(line);

            if (command != null) commands.add(command);
        }

        return commands;
    }
}
