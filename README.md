# PROJET-S7 : Jeu Multijoueur en Temps Réel

Bienvenue dans **PROJET-S7**, un jeu multijoueur en temps réel développé en Java. Ce projet a été conçu pour permettre aux joueurs d'interagir en réseau dans un environnement compétitif, avec des fonctionnalités telles que la gestion de joueurs, les projectiles, et un système de respawn en cas de défaite.

## 📜 Sommaire
- [Description](#description)
- [Fonctionnalités](#fonctionnalités)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Usage](#usage)
- [Architecture du projet](#architecture-du-projet)
- [Contributeurs](#contributeurs)

---

## 📝 Description

**PROJET-S7** est un projet de jeu multijoueur conçu pour être joué sur un réseau local. Chaque joueur contrôle un personnage qui peut :
- Se déplacer, tourner, et tirer des projectiles.
- Affronter d'autres joueurs pour remporter la partie.
- Respawn en cas de défaite grâce à une interface utilisateur simple.

Le projet utilise **JavaFX** pour l'interface utilisateur et un système de sockets pour la communication entre les joueurs.

---

## ✨ Fonctionnalités

1. **Multijoueur en temps réel** : Les joueurs peuvent se connecter à un serveur commun et interagir les uns avec les autres.
2. **Gestion des joueurs** : Suivi de l'état des joueurs (connecté, actif, mort).
3. **Projectiles et collisions** : Les joueurs peuvent tirer des projectiles qui interagissent avec les autres joueurs.
4. **Système de respawn** : Lorsqu'un joueur est tué, il a la possibilité de réapparaître dans la partie.
5. **Interface utilisateur (UI)** : Utilisation de JavaFX pour des fenêtres intuitives (respawn, statut, etc.).

---

## ⚙️ Prérequis

Avant de lancer le projet, assurez-vous d'avoir :
1. **Java** (version 11 ou supérieure).
2. **JavaFX** : Assurez-vous que les bibliothèques JavaFX sont disponibles sur votre système.
3. **Make** : Utilisé pour compiler et exécuter le projet.
4. **Environnement Linux/Unix** : Testé principalement sur Ubuntu.

---

## 🚀 Installation

1. Clonez le dépôt GitHub :
   ```bash
   git clone https://github.com/votre-utilisateur/projet-s7.git
   cd projet-s7

2. Installez JavaFX : 
    ```bash
    sudo apt install openjfx

3. Compilez le projet :
    ```bash
    make

4. Jouez :
    ```bash
    make ui

## 📖 Usage

    Lancez le serveur en premier.

    Démarrez les clients pour vous connecter au serveur.

    Contrôlez votre joueur à l'aide des commandes suivantes :
        Avancer : Touche Z
        Reculer : Touche S
        Tourner à gauche : Touche Q
        Tourner à droite : Touche D
        Tirer : Touche Espace

    En cas de mort :
        Une fenêtre de respawn s'ouvre.
        Cliquez sur Respawn pour réapparaître ou sur Abandonner pour quitter la partie.

## 🛠️ Architecture du projet

    ThreadHostGestionPlayer.java : Gestion des joueurs, de leur état, et des interactions.
    ThreadHostToClient.java : Communication entre le serveur et les clients.
    Carte.java : Gestion de la carte et des objets présents.
    ListePartageThread.java : Liste partagée des threads des joueurs.
    CoordFloat.java : Représentation des coordonnées utilisées pour le positionnement des joueurs et des projectiles.
    Makefile : Automatisation de la compilation et de l'exécution.

## 👨‍💻 Contributeurs

   Michel065 (https://github.com/Michel065)
   Bectaly13 (https://github.com/Bectaly13)
   goman-v (https://github.com/goman-v)

Merci d'utiliser PROJET-S7 ! 🎮 