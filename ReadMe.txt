#Description
Cette archive contient le projet "Worms" réalisé par CHARNAY Jacques, BERTRAND Paul, BERNARD Maxime, BOURSAUD Thomas.
Notre projet consiste à recréer le jeu « Worms ». Ce jeu est un jeu de type tour par tour en 2D ou des vers de terre, les « Worms », s’affrontent en équipe sur un terrain entouré d’eau.
Dans ce jeu, plusieurs Worms s’affrontent sur une carte grâce à des armes variées (lance-roquettes, grenade, fusils, etc ...). Le but est de battre tous les joueurs de l’équipe adverse.

Conseil : Lisez le tutoriel dans le dossier Rendu avant de lancer le jeu!

#Instruction de lancement:
    --> Windows :
        * Double-cliquer sur Launch_windows.bat
        Ou
        * Ouvrir une fenêtre de commande (terminal) dans le dossier où se trouve le fichier .jar et taper :
                "java -Djava.library.path=lib/natives -Xmx2048m -jar WormsSlick2.jar"

    --> Linux :
        * Ouvrir une fenêtre de commande (terminal) dans le dossier où se trouve le fichier .jar et taper :
                "java -Djava.library.path=lib/natives -Xmx2048m -jar WormsSlick2.jar"

#Contenu de l'archive:
    --> Description dosier par dossier
        - "fonts" contient la police utilisée dans le programme;
        - "images" contient toutes les images utilisées dans notre projet;
        - "lib" contient les différentes librairies utilisées pour le projet, la librairie Slick2D notamment;
        - "music" contient les musiques utilisées;
        - "Rendu" contient le rendu final du projet ainsi qu'un tutoriel précisant les différentes touches du jeu;
        - "src" est le dossier principal du projet, il contient toutes les classes necessaires à l'execution du programme.
    --> Fichiers à la racine de l'archive:
        - Le fichier "ReadMe.txt";
        - L'executable .jar "WormsSlick2.jar" permattant de lancer le programme;
        - "Launch_windows.bat" qui permet de lancer le jeu sur Windows;


Bon jeu !