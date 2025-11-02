# Softwear Engineering Project

## À propos

Différentes implémentations du BitPacking pour la compression de suite d'integers.
(Par Alexis Le Forestier)

## Table des matières

- [À propos](#à-propos)
- [Installation](#installation)
- [Utilisation](#utilisation)

## Prérequis

- **Java JDK 17 ou supérieur** - Environnement d'exécution Java  
  [Télécharger Oracle JDK](https://www.oracle.com/java/technologies/downloads/) ou [OpenJDK](https://openjdk.org/)

## Installation

```bash
# Cloner le dépôt
git clone https://github.com/Alexislfdb/m1-se-project.git

# Se déplacer dans le dossier du projet
cd m1-se-project/src

# Compiler le programme
javac Main.java

# Executer le programme
java Main
```

## Utilisation

Après avoir lancé le programme, celui-ci va vous demander dans quel fichier se trouve votre suite d'integers, et placera cette suite dans originalArray.

Voir les exemples mis en commentaires entre la ligne 33 et 57 pour savoir comment utiliser les méthodes.

ATTENTION : la méthod get renvoie le int numéro i de la suite de int, PAS le int l'index i.
Donc si nous voulons le premier int de la suite, celui à l'index 0, on fait get(1)
