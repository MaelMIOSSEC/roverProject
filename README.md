# roverProject

Application Java Swing permettant de visualiser et piloter le rover sur un terrain de 20 × 20 cases.

## Lancement

Exécutez `Main` depuis IntelliJ IDEA avec Java 17.

## Utilisation

Modifiez le script dans le panneau de droite, puis choisissez **Exécuter** pour lancer les commandes ou **Étape suivante** pour les parcourir une par une. Le pavé directionnel déplace le rover d’une case ; **Réinitialiser** remet le terrain et le rover à leur état initial. Le plateau affiche la position, le cap, le trajet et les objets déposés.

Commandes reconnues : `base x y`, `dir NORTH|SOUTH|EAST|WEST`, `mov distance`, `drop`, `startM`, `stopM`, `resM`, `replay` et `wback`.
