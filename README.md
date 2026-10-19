# Rover Project

Une simulation de rover en Java : écrivez un script de commandes, exécutez-le et observez le rover évoluer sur une grille.

## Aperçu

- Terrain de **20 × 20 cases**, avec coordonnées de `0` à `19`.
- Rover initialisé en `(0, 0)`, orienté vers l’est.
- Interface graphique MVC avec deux vues Nicellipse distinctes : la grille du terrain et l'éditeur de script.
- Exécution des commandes en séquence, avec affichage de la position et des éventuelles erreurs.
- Commandes pour enregistrer, rejouer et inverser un parcours.

## Démarrer

Le projet nécessite **Java 17**. Ouvrez-le dans IntelliJ IDEA, puis lancez la méthode `main` de la classe `Main`.

La vue **Terrain** affiche la grille et la position du rover. Cliquez sur **Ouvrir le script** pour accéder à l’éditeur, puis sur **Exécuter** pour lancer les commandes et revenir à la grille. **Réinitialiser** remet le rover et le terrain dans leur état initial. Les modèles (`Board`, `Robot`), les vues Nicellipse et le contrôleur sont séparés selon le pattern MVC.

## Écrire un script

Chaque ligne contient une commande. Les lignes vides sont ignorées. Exemple :

```text
base 3 3
dir EAST
mov 5
dir NORTH
mov 4
drop
```

Ce script place le rover en `(3, 3)`, le déplace vers l’est puis vers le nord, et dépose un objet sur sa dernière case.

### Commandes disponibles

| Commande | Exemple | Effet |
|---|---|---|
| `base x y` | `base 3 3` | Place le rover aux coordonnées indiquées. |
| `dir direction` | `dir NORTH` | Oriente le rover vers `NORTH`, `SOUTH`, `EAST` ou `WEST`. |
| `mov distance` | `mov 5` | Avance de la distance indiquée dans la direction actuelle. Une distance négative fait reculer le rover. |
| `drop` | `drop` | Dépose un objet sur la case actuelle. |
| `startM` | `startM` | Reprend l’enregistrement des commandes dans l’historique. |
| `stopM` | `stopM` | Suspend l’enregistrement des commandes dans l’historique. |
| `resM` | `resM` | Efface l’historique des commandes enregistrées. |
| `replay` | `replay` | Rejoue les commandes enregistrées. |
| `wback` | `wback` | Exécute les commandes inverses de l’historique, dans l’ordre inverse. |

Le rover ne peut pas sortir du terrain : une commande qui le ferait dépasser les limites produit une erreur.

## Organisation du projet

```text
src/
├── Classes/       # Rover, terrain, position, analyseur et interface graphique
├── Commands/      # Implémentations des commandes
├── Enumerators/   # Directions possibles
├── Exceptions/    # Erreurs liées aux positions et directions
├── Interfaces/    # Contrats des commandes et de leur création
├── Tests/         # Tests unitaires JUnit
└── nicellipse/    # Composants graphiques Nicellipse
```

## Tests

Les tests sont écrits avec **JUnit 5**. Depuis IntelliJ IDEA, lancez la classe `RoverProjectTest` pour vérifier le déplacement, l’analyse des scripts, l’historique et les commandes.
