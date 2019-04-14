# Rapport Projet PC2R 2018-2019 <!-- omit in toc -->

*Auteurs : Nathan GERDAY / Pierre GOMEZ*

# Sommaire <!-- omit in toc -->

- [Introduction](#introduction)
  - [Choix des langages](#choix-des-langages)
- [Manuel](#manuel)
  - [Installation](#installation)
    - [Serveur](#serveur)
    - [Client](#client)
  - [Utilisation](#utilisation)
    - [Démarrer le serveur](#d%C3%A9marrer-le-serveur)
    - [Démarrer le client](#d%C3%A9marrer-le-client)
    - [Comment jouer ?](#comment-jouer)
- [Description du projet](#description-du-projet)
  - [Travail réalisé / Extensions](#travail-r%C3%A9alis%C3%A9--extensions)
  - [Serveur](#serveur-1)
  - [Client](#client-1)
- [Point pertinants](#point-pertinants)
  - [Synchronisation](#synchronisation)
  - [Gestion de la session courante](#gestion-de-la-session-courante)
  - [Tickrates](#tickrates)
  - [Compatibilité Protocoles avec extensions](#compatibilit%C3%A9-protocoles-avec-extensions)
  - [Pool de threads](#pool-de-threads)

# Introduction

## Choix des langages

- Le serveur est codé en **Java**. Nous n'utilisons pas de librairie particulière en dehors de celle fourni directement dans le langage.
- Le client est codé en **Python 3**. Toute l'interface graphique est gérée avec le module **Pygame**.

# Manuel

## Installation

### Serveur

Nous utilisons la version de Java 1.8. Le serveur devrait cependant fonctionner avec toutes les versions récentes de Java.  

Nous avons utilisons un fichier *build.xml* afin de pouvoir compiler tout le serveur avec Ant. Il suffit pour cela d'utiliser la commande :  
```
ant compile
```

### Client

Nous utilisons Python 3 dans la version 3.6.7.  
Les versions plus récentés fonctionnent également.
Les versions plus anciennes de Python 3 devrait fonctionner pour la majorité d'entres elles bien que nous ne les ayons pas testé.

Il faut également installer la librairie Pygame. Le plus simple est de passer par l'installeur de module pour Python qui s'appelle Pip.

Installation de pip:  
```sh
apt-get install python3-pip
```

Installation de Pygame:  
```sh
python3 -m pip install -U pygame --user
```

## Utilisation

### Démarrer le serveur

Une fois le serveur compilé avec `ant compile`, il suffit de le lancer avec la commande :   
```
java -cp bin/ server.Serveur
```

Le serveur va ensuite va tourner sur le port indiqué et gerer les connexions jusqu'à ce qu'on l'arrête avec une interruption **Ctrl+C**.


### Démarrer le client

Pour lancer un client depuis la racine du projet, on éxécute la commande :
```
python3 client/client.py
```

Chaque exécution de la commande lancera un nouveau client.



### Comment jouer ?

Le port par défaut est 45678.
Pour le modifier sur le serveur, il faut aller dans le fichier `src/constants/Constants.java`.
Pour le client, on peut modifier le port ainsi que l'addresse hôte dans le fichier `client/const.py`.

Avant tout, on démarre le serveur.  

Ensuite, lorsque l'on lance un client, un premier écran permet d'entrer une nom d'utilisateur. Une fois le nom entré, on peut appuyer sur **Entrée** pour se connecter automatiquement au serveur.  

Si on est le premier client, on arrive alors dans une phase d'attente pendant laquelle on ne peut pas faire d'action autre que de parler dans le chat.

Une fois la partie commencée, on arrive sur un écran avec des obstacles (les ronds blancs), un objectif (le rond jaune), le vaisseau du joueur (en gris) et les vaisseaux des autres joueurs (en rouge) s'il y en a.

Le but, par défaut, est d'être le premier à récupérer 3 objectifs. Dans ce mode, les objectifs sont partagés par tout les joueurs.

Les actions possibles sont:
- Tourner dans le sens antihoraire => Touche **Q** ou **Flèche gauche**
- Tourner dans le sens horaire => Touche **D** ou **Flèche droite**
- Donner une impulsion => **Barre espace**
- Tirer => Touche **E**
- Quitter => **Echap**
- Commencer à écrire dans le chat et envoyer un message => **Entrée** 
- Arreter d'écrire dans le chat => **Echap**

Pour envoyer un message privé, il faut écrire un message de la forme :  
`/w <username> <message>`

Toutes les informations ainsi que messages du chat s'affichent dans un log en bas à gauche de la fenêtre. Les messages disparaissent après un certain temps.

Les scores de chaque joueurs sont affichés en haut à droite de l'écran triés selon le score.

On peut attaquer à tout moment et si un tir touche un adversaire, il s'arrête instantanément.

On peut également lancer une partie en mode Course. Pour cela, pendant la phase d'attente (au lancement de la session ou après qu'un joueur ait gagné la partie), il faut qu'un joueur écrive dans le chat : `/race`.

En mode Course, on voit alors 2 objectifs:
- L'objectif courant (en jaune clair), qui correpond à l'objectif qu'il faut récupérer actuellement.
- L'objectif suivant (en jaune foncé), qui correspond à la position du prochain objectif une fois que le courant aura été récupéré.

# Description du projet

## Travail réalisé /  Extensions

Nous avons réalisé 3 extensions en plus des parties A, B et C du sujet.  
Tout est fonctionnel et, bien que nous n'ayons pas testé nous-même, le serveur et le client respectent le protocole et devrait être donc compatible avec d'autres. Lors de l'ajout d'extensions, nous avons veillé à conserver la compatibilié avec un client / serveur n'incluant pas ces extensions.

Le seul point sur lequel nous avons dû faire un choix et qui pourra donc créer un problème de compatibilité est au niveau de la gestion des coordonnées. Nous avons fait le choix d'avoir des données "abstraites" côté serveur et c'est ensuite au client de les convertir en coordonnées réelles en fonction de la taille de sa fenêtre. Côté serveur, nous avons des coordonnées entre -1 et 1, avec le point (0,0) au centre, le point (-1, -1) en haut à gauche et le point (1, 1) en bas à droite. Tout le protocole utilise donc ces données abstraites. Cela nous permettrait en théorie d'avoir des tailles de fenêtre variable pour chaque client et de toujours garder des coordonnées cohérentes.

## Serveur

## Client


# Point pertinants

## Synchronisation

TODO Object servant de lock
TODO Ordre toujours respecté dans les lock

## Gestion de la session courante

TODO Thread de lancement après délai + Thread d'appelle de tick

## Tickrates

TODO Scaling + Collision => retour position précedente

## Compatibilité Protocoles avec extensions

TODO NEWCOM2 / TICK2

## Pool de threads

TODO Executors



```java
public Connexion(Socket client_soc, Session session){
    this.client = client_soc;
    this.session = session;
    try {
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintStream(client.getOutputStream()); 
    }catch (IOException e){
        try {client.close();} catch (IOException e1){}
        System.err.println(e.getMessage());
        return;
    }
}
```

```python
try:
    data = self.socket.recv(8192, socket.MSG_DONTWAIT)
except BlockingIOError:
    return

commands = data.decode().split("\n")
commands = [cmd.split("/") for cmd in commands]
```

