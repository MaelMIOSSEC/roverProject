package Command;

/**
 * Fabrique une commande à partir des arguments analysés d'une ligne de script.
 */
public interface CommandFactory {
    /**
     * Crée la commande correspondant aux arguments fournis.
     *
     * @param args éléments de la ligne, le mot-clé de commande étant en première position
     * @return commande créée
     */
    public Command create(String[] args);
}
