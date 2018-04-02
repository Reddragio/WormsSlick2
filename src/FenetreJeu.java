import org.newdawn.slick.*;
import javax.swing.*;
import java.io.IOException;

public class FenetreJeu extends BasicGame{
    protected GameContainer container;
    protected int[][] terrain;//Tableau bidimensionnel decrivant la map du jeu
    protected int blockSize;//Taille des blocks, en pratique 5 pixels
    protected int hauteur;//Hauteur de la fenetre (en pixels)
    protected int largeur;//Largeur de la fenetre (en pixels)
    protected int hauteurBlock;//Hauteur de la fenetre (en block de 5*5 pixels)
    protected int largeurBlock;//Largeur de la fenetre (en block de 5*5 pixels)
    protected Timer mt;
    protected int vitesseDep; //Cette variable correspond à l'intervalle entre deux entrées claviers pour déplacer
    //le Worms. On peut donc l'associer en quelque sorte à la vitesse de déplacement.
    protected long antiRepeatTime;
    protected Worms[] joueurs;//Tableau contenant tous les Worms
    protected boolean[] changementPrint; //Permet de rafraichir l'écran uniquement si il y a eu un changement
    protected boolean isMovingLeft;//Indique si le Worms jouant actuellement avance vers la gauche ou non
    protected boolean isMovingRight;//Indique si le Worms jouant actuellement avance vers la droite ou non
    protected boolean augmentationAngleVisee;//Indique si le joueur est en train de vouloir augmenter l'angle de visée
    protected boolean diminutionAngleVisee;//Indique si le joueur est en train de vouloir diminuer l'angle de visée
    protected long tempEcoule;//Associé à lastTempEcoule, cette variable sert à fixer une vitesse de déplacement
    protected long lastTempEcoule;
    protected BigImage sky;//Image de l'arrière plan
    protected BigImage big_ground;//Image du premier plan
    protected BigImage sea;//Image de la mer
    protected BigImage physicFilter;//Filtre permettant de visualiser la physique des blocks en mode développeur (ou "Cheat Mode")
    protected int texture_size;
    protected int hauteur_draw_texture;
    protected int largeur_draw_texture;
    protected Music[] battlePlayList;//Tableau contenant toutes les musiques jouées durant la partie
    protected Music musiqueActuelle;//Musique actuellement jouée
    protected int musicIndex;//Indice de la musique actuellement jouée
    protected boolean musicActivation;//Indique si la musique est activée ou non
    protected int stackedEnter; //permet de gerer le double saut
    protected long lastTime; //permet de gerer le double saut

    protected Projectile projectileActuel;//Contient le projectile actuellement en train de se déplacer (durant la phase de tir)
    protected long timerExplosionProjectile;//Sert à temporiser l'explosion, pour les grenades notamment

    //Variables indiquant la phase de jeu:
    protected boolean phaseChoixPuissance;
    protected boolean phaseProjectile;
    protected boolean phaseInventaire;
    protected boolean phaseTeleporteur;

    protected long timerChoixPuissance;//Sert à mesurer le temps d'accumulation de puissance
    protected long chronoChoixPuissance;//Temps d'accumulation de puissance maximal
    protected double pourcentage;
    protected boolean enterRelache;//Indique si la touche entrée a été relachée
    protected String[][] tabNomCoul;//Tableau bidimensionnel contenant le nom des Worms lors de l'initialisation
    protected GestionTerrain monde; //permet de connaitre le terrain tel qu'il a été généré (avant les explosions)
    protected GestionTours gestionTours;//Classe assurant le jeu tour par tour
    protected Teleporteur drawTeleporteur;//Instance du teleporteur servant à l'affichage graphique de l'objet
    protected Image mouseCursor;//Image du curseur personnalisé

    //Explosion
    protected Animation aExplosion;
    protected boolean isExplosion;
    protected float tempsExplo;
    protected float timerExplo;

    //Fonctions développeurs (autrement appellés "Cheat Mode")
    //-->accessible en cliquant sur C. Ces fonctions nous ont permis de débugguer le jeu
    protected int rayonExplosion;//Rayon de l'explosion actuellement choisi. Reglable avec la molette
    protected boolean visualiserExplosion;//Indique si l'utilisateur souhaite visualiser ou non le rayon de l'explosion par un cercle rouge
    protected Input input;//Instance des entrées claviers/souris. Sert dans les faits à recuperer les coordonnées x et y de la souris
    protected boolean antiExplosion;//Permet de mettre des blocs au lieu d'en détruire
    protected final static int blocIndestructibles[] = {2,3};//Blocs indestructibles. En l'occurence, l'eau et les blocs invisibles sur les bords de la map
    protected boolean experimentalVisee;//Servait à acceder à la phase de visée durant le dévellopement
    protected boolean cheatMode;//Indique si le cheat mode est activé ou non
    protected int lastDelta;//Permet de synchroniser le deltaT entre la fonction render et update

    //Parametrage du décor
    protected String adresseArrierePlan;
    protected String adresseGround;
    protected String adresseMap;
    protected String adresseEau;
    protected int screenHeight;//Hauteur réelle de l'écran
    protected int screenWidth;//largeur réelle de l'écran
    protected double drawScaleX;//facteur d'échelle selon les x
    protected double drawScaleY;//facteur d'échelle selon les y
    protected int xMonde;//Largeur en blocks du monde
    protected int yMonde;//Hauteur en blocks du monde

    public FenetreJeu(int s,int x,int y, String[][] tab,Map carte,int screenWidth,int screenHeight) throws SlickException, IOException {
        super("Worms Fighter Z - Slick Version");
        blockSize=s;
        tabNomCoul = tab;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        //Les cartes ont été initialement concues pour des écrans 1080p, c'est pourquoi les facteurs d'échelle sont relatifs à cette résolution:
        drawScaleX = screenWidth/1920.0;
        drawScaleY = screenHeight/1080.0;
        xMonde = x;
        yMonde = y;

        //Parametrage du décor - recuperation de la carte choisie dans le menu
        adresseArrierePlan = carte.getAdresseArrierePlan();
        adresseGround = carte.getAdresseGround();
        adresseMap = carte.getAdresseMap();
        adresseEau = carte.getAdresseEau();

        monde=new GestionTerrain();//Création de l'instance du monde
        monde.setHauteurEau(y,carte.getHauteurEau());//Définition du niveau de l'eau, propre à chaque carte

        /*monde.genererTerrain(x,y,1);
        monde.genererFaille();
        monde.genererIles();*/

        monde.genererTerrain(x,y,adresseMap,drawScaleX,drawScaleY);//Generation du tableau bidimensionnel de la physique à partir de la bitmap de la map
        monde.generateSea();//Generation des blocks propre à la mer
        monde.generateLimite(x,y);//Generation de blocks invisibles sur les bords de la map
        terrain=monde.getTerrainInitial();

        lastDelta = 0;
    }

    public void init(GameContainer container) throws SlickException {
		//Initialisation du jeu
		//S'execute juste après le constructeur
        hauteurBlock = terrain.length;
        hauteur = hauteurBlock*blockSize;
        largeurBlock = terrain[0].length;
        largeur = largeurBlock*blockSize;

        vitesseDep = 10;//Vitesse de déplacement em ms
        antiRepeatTime = 0;

        //mt = new Timer(20,this); //L'horloge du jeu est bassé sur un timer se déclenchant toutes les 20ms
        //Cela permet donc en théorie d'avoir du 50 fps, ce qui est largement suffisant pour notre jeu
        //mt.start();

        changementPrint = new boolean[1];
        changementPrint[0] = true;

        /*joueurs = new Worms[6];
        joueurs[0] = new Worms("Rouge","1",terrain,blockSize,400,100);
        joueurs[1] = new Worms("Rouge","2",terrain,blockSize,500,100);
        joueurs[2] = new Worms("Rouge","3",terrain,blockSize,600,100);
        joueurs[3] = new Worms("Bleu","1",terrain,blockSize,700,100);
        joueurs[4] = new Worms("Bleu","2",terrain,blockSize,800,100);
        joueurs[5] = new Worms("Bleu","3",terrain,blockSize,900,100);*/

        joueurs = new Worms[6];//Creation du tableau des joueurs
        for(int i=0;i<tabNomCoul.length;i++){
            //On le remplit avec le tableau de noms fournis par le menu
            joueurs[i] = new Worms(tabNomCoul[i][1],tabNomCoul[i][0],terrain,blockSize,100+i*50,100);
        }
        //On synchronise ensuite les inventaires au sein de chaque équipe (afin que l'inventaire soit partagé au sein d'une même équipe)
        joueurs[0].synchroniserInventaire(joueurs[1]);
        joueurs[0].synchroniserInventaire(joueurs[2]);
        joueurs[3].synchroniserInventaire(joueurs[4]);
        joueurs[3].synchroniserInventaire(joueurs[5]);

        stackedEnter=0;//Variable servant à la réalisation des doubles sauts

        //Création d'une instance de la gestion des tours
        gestionTours = new GestionTours(joueurs,this,largeur,hauteur);

        this.container = container;
        isMovingLeft = false;
        isMovingRight = false;
        augmentationAngleVisee = false;
        diminutionAngleVisee = false;
        tempEcoule = 0;
        lastTempEcoule = 0;

        //Initialisation du décor
        sky = new BigImage(adresseArrierePlan);
        big_ground = new BigImage(adresseGround);
        sea = new BigImage(adresseEau);//"images/sea_dark_large.png"

        //Initialisation de la playlist
        battlePlayList = new Music[4];
        battlePlayList[0] = new Music("music/battle1.ogg");
        battlePlayList[1] = new Music("music/battle2.ogg");
        battlePlayList[2] = new Music("music/battle3.ogg");
        battlePlayList[3] = new Music("music/battle4.ogg");
        musiqueActuelle = battlePlayList[0];
        musicIndex = 0;
        musicActivation = true;

        //Initialisation des variables propre aux phases (toutes sur false au début)
        phaseProjectile = false;
        phaseChoixPuissance = false;
        chronoChoixPuissance = 1500;
        enterRelache = false;
        phaseInventaire = false;

        //Initialisation du curseur
        mouseCursor = new Image("images/mouseCursor3.png");

        //SpriteSheet explosion
        Image Sprite = new Image("./images/SpriteSheetExplosion.png");
        int spritelong = 130;
        SpriteSheet sExplosion = new SpriteSheet(Sprite, spritelong,spritelong);
        tempsExplo = 1000;
        timerExplo = 0;
        aExplosion = new Animation(sExplosion, (int)tempsExplo/10);
        isExplosion=false;

        //Experimental - Initialisation des variables propres au mode développeur ("Cheat Mode")
        rayonExplosion = 40;
        visualiserExplosion = false;
        input = container.getInput();
        antiExplosion = false;
        experimentalVisee = false;
        cheatMode = false;
        physicFilter = new BigImage("images/physicFilter.png");

        //Spawn des Worms sur la map
        try {
            spawnWorm();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Initialisation de l'instance du teleporteur nécessaire à son dessin
        phaseTeleporteur = false;
        drawTeleporteur = new Teleporteur(42);

        this.container = container;

        //La souris est cachée afin qu'on puisse l'afficher ou non à notre guise selon les phases
        container.setMouseGrabbed(true);

        //Creation d'instances factices pour charger les images en mémoire
        Rocket init1 = new Rocket(terrain,blockSize,joueurs[0]);
        GrenadeProjectile init2 = new GrenadeProjectile(terrain,blockSize,joueurs[0]);
        HolyGrenadeProjectile init3 = new HolyGrenadeProjectile(terrain,blockSize,joueurs[0]);
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
		//Rendu graphique du jeu !
		//S'execute un maximum de fois par secondes, afin de maximiser les fps
        //L'ordre dans lesquels on effectue les affichages a une grande importance:
        //Il se fait dans l'ordre de ce qui se trouvera en arrière plan jusqu'à ce qui se trouvera au premier plan
		
        changementPrint[0] = false;

        //Affichage de l'arriere plan:
        sky.draw(0,0,(float)drawScaleX);

        //Affichage du premier plan:
        //La complexité de la fonction demeure dans son optimisation
        //En effet, pour maximiser les performances, l'algorithme cherche à dessiner les portions horizontales les plus
        //grandes possibles
        //(Par le passé, nous avions eu des problèmes de performances car les blocks de 5*5 pixels étaient dessinés un à un)
        int iFirst = 0,jFirst = 0,iLast = 0,jLast = 0;
        boolean first = true;
        for(int i=0;i<hauteurBlock;i++){
            for(int j=0;j<largeurBlock;j++){
                if(terrain[i][j]==1){
                    if(first){
                        first = false;
                        iFirst = i;
                        jFirst = j;
                        //On garde en mémoire les coordonnées du premier blocks de la ligne ...
                    }
                    iLast = i;
                    jLast = j;
                    //...et aussi ceux du dernier rencontré
                }
                if((terrain[i][j]!=1 || j == largeurBlock-1)&&!first){
                    //Si on finit de rencontrer des blocks à dessiner ou si l'on atteint la fin de la ligne, alors on dessine:
                    big_ground.draw(jFirst*blockSize,iFirst*blockSize,(jLast+1)*blockSize,(iLast+1)*blockSize,jFirst*blockSize*(float)(1/drawScaleX),iFirst*blockSize*(float)(1/drawScaleY),(jLast+1)*blockSize*(float)(1/drawScaleX),(iLast+1)*blockSize*(float)(1/drawScaleY));
                    first = true;
                }
            }
        }

        /*for(int i=0;i<terrain.length;i++){
            for(int j=0;j<terrain[0].length;j++){
                if(terrain[i][j]==0){
                    g.setColor(Color.cyan);
                    g.fillRect(blockSize*j,blockSize*i,blockSize,blockSize);
                }
                if(terrain[i][j]==1){
                    ground.draw(blockSize*j,blockSize*i);
                }
                if(terrain[i][j]==2){
                    g.setColor(Color.blue);
                    g.fillRect(blockSize*j,blockSize*i,blockSize,blockSize);
                }

            }
        }*/

        //Affichage des Worms
        g.setColor(Color.green);
        for(Worms wor: joueurs){
            wor.draw(g);
            if(cheatMode){
                wor.physic.drawHitBox(g);
            }
        }

        //Affichage de l'animation de l'explosion
        if(isExplosion){
            aExplosion.draw((float)(projectileActuel.getx()-65),(float)(projectileActuel.gety()-65));
        }

        //Affichage des dégats
        for(Worms wor:joueurs){
            wor.printPerteVie(lastDelta);
        }

        //Affichage du projectile (durant la phase de tir)
        g.setColor(Color.red);
        if(phaseProjectile){
            projectileActuel.draw(g);
            if(cheatMode){
                projectileActuel.physic.drawHitBox(g);
            }
        }

        //Affichage de la mer
        sea.draw(0,monde.getNiveauEau()*blockSize);

        //Affichage de la physique des blocs en mode développeur
        if(cheatMode){
            iFirst = 0;
            jFirst = 0;
            iLast = 0;
            jLast = 0;
            first = true;
            int a=2;
            for(int i=0;i<hauteurBlock;i++){
                for(int j=0;j<largeurBlock;j++){
                    if(terrain[i][j]==1){
                        if(first){
                            first = false;
                            iFirst = i;
                            jFirst = j;
                            a=42;//Parce que la réponse à la vie et à tout le reste (Easter eggs ! Bravo d'être parvenu jusqu'ici ^^)
                            //On garde en mémoire les coordonnées du premier blocks de la ligne ...
                        }
                        iLast = i;
                        jLast = j;
                        //...et aussi ceux du dernier rencontré
                    }
                    if((terrain[i][j]!=1 || j == largeurBlock-1)&&!first){
                        //Si on finit de rencontrer des blocks à dessiner ou si l'on atteint la fin de la ligne, alors on dessine:
                        physicFilter.draw(jFirst*blockSize,iFirst*blockSize,(jLast+1)*blockSize,(iLast+1)*blockSize,jFirst*blockSize*(float)(1/drawScaleX),iFirst*blockSize*(float)(1/drawScaleY),(jLast+1)*blockSize*(float)(1/drawScaleX),(iLast+1)*blockSize*(float)(1/drawScaleY));
                        first = true;
                    }
                }
            }
        }

        for(Worms wor: joueurs){
            if(wor.getAimingState()){
                //Affichage de la visée
                wor.drawVisee();
                if(phaseChoixPuissance && timerChoixPuissance<= chronoChoixPuissance){
                    //Affichage du cone de puissance
                    wor.armeActuelle.drawConePuissance((((double)timerChoixPuissance)/((double)chronoChoixPuissance))*100.0);
                }
            }
            if(wor.isPlaying()){
                if(phaseInventaire){
                    //Affichage de l'inventaire
                    wor.drawInventaire(input);
                }
            }
        }

        //Affichage du rayon de l'explosion par un cercle rouge (dans le "cheat mode")
        if(visualiserExplosion){
            g.setColor(Color.red);
            g.drawOval((float)(input.getMouseX()-rayonExplosion),(float)(input.getMouseY()-rayonExplosion),(float)(2*rayonExplosion),(float)(2*rayonExplosion));
        }

        //Affichage du curseur de la souris:
        //On récupere les coordonnées
        int xSouris = input.getMouseX();
        int ySouris = input.getMouseY();
        if(phaseTeleporteur){
            //Si le joueur souhaite se teleporter, alors on affiche le viseur de la teleportation
            if(0<=xSouris && xSouris < largeur - 20 && 40 <= ySouris && ySouris < hauteur &&(((gestionTours.getActualWorms()).physic.getContactBlock(xSouris,ySouris)).isEmpty())){
                drawTeleporteur.drawTeleporteur(xSouris,ySouris);
            }
            else{
                drawTeleporteur.drawTeleporteurRed(xSouris,ySouris);
            }
        }
        else if(phaseInventaire || cheatMode){
            //Mais si l'on est simplement dans l'inventaire ou dans le "cheat mode", alors on affiche le curseur classique
            mouseCursor.draw(xSouris-10,ySouris);
        }

        //Affichage du timer en haut à droite de la fenêtre
        gestionTours.printTime();
        //Affichage des messages rythmant la partie (ex: "tour de bidule", "machin est mort", etc...)
        gestionTours.printMessage(lastDelta);

        //Affichage de données sur la physique
        if(cheatMode){
            gestionTours.printData(lastDelta);
        }

        //Affichage de l'écran de fin !
        gestionTours.printEnd();
        //Enfaite, lorsqu'il s'affiche, la partie continue en quelque sorte en arrière plan. Mais on ne peut plus le voir !
    }

    public void update(GameContainer container, int delta) throws SlickException {
		//Met à jour la logique du jeu !
		//la boucle s'execute à la meme fréquence que la boucle render, théoriquement à 120Hz

        lastDelta = delta;//Synchronisation du delta avec la fonction render

        //Mise à jour de la gestion du tour par tour
        //Dans les faits, cela permet par exemple de gerer les timers limitant le tour d'un Worms à 30+20s
        gestionTours.updateLogic(delta);

        //Application de la physique aux Worms
        for(Worms wor: joueurs){
            wor.applyPhysic(delta,gestionTours,monde);
        }

        //Phase du choix de la puissance
        if(phaseChoixPuissance){
            timerChoixPuissance += delta;
             if(timerChoixPuissance>=chronoChoixPuissance || enterRelache){
                 //Si l'on appuie depuis trop longtemps ou si l'on a relaché la touche entrée, alors ...
                phaseChoixPuissance = false;
                 for(Worms wor: joueurs){
                     //On cherche le Worms qui était en train de viser
                     if(wor.getAimingState()){
                         //Une fois qu'on l'a trouvé, on indique qu'il ne vise plus
                         wor.setAimingState(false);
                         try {
                             //On récupère le projectile qu'il tirait
                             projectileActuel = (wor.getArmeActuelle()).generateProjectile(terrain,blockSize,wor);
                         } catch (SlickException e) {
                         }

                         //On calcul la puissance avec laquel il tirait
                         double pourcentagePuissanceTir;
                         if(timerChoixPuissance<=chronoChoixPuissance){
                             pourcentagePuissanceTir = (((double)timerChoixPuissance)/((double)chronoChoixPuissance))*100.0;
                         }
                         else{
                             pourcentagePuissanceTir = 100;
                         }

                         //Enfin, on tire le projectile !
                         projectileActuel.specialLaunch(wor.getArmeActuelle(),pourcentagePuissanceTir);
                         timerExplosionProjectile = 0;
                         phaseProjectile = true;
                     }
                 }
             }
        }

        //Phase projectile - concretement lorsque qu'une grenade ou une rocket est en train de déplacer
        if(phaseProjectile){
            //On applique la physique au projectile
            projectileActuel.applyPhysic(delta,joueurs);
            timerExplosionProjectile += delta;//On calcule depuis combien de temps il a été tiré
            if(timerExplosionProjectile >= projectileActuel.getChronoExplosion() || !projectileActuel.isAlive()){
                //Si le projectile a dépassé son temps pour exploser ou bien s'il a rencontré une paroi dans le cas des rockets, alors:
                //On le fait exploser !
                projectileActuel.explosion(joueurs);
                //On déclenche l'animation d'explosion:
                isExplosion=true;
                phaseProjectile = false;//Fin de la phase projectile
                gestionTours.setPhase(3);//On passe à la phase de visualisation de l'explosion
            }
        }

        tempEcoule += delta;
        if(tempEcoule - lastTempEcoule >= vitesseDep){
            //On peut se deplacer que tous les "vitesseDep" ms
            lastTempEcoule = tempEcoule;
            for(Worms wor: joueurs) {
                if(wor.isPlaying()){
                    //Deplacement du Worms à gauche ou à droite
                    if(isMovingLeft){
                        wor.deplacer(0);
                    }
                    else if(isMovingRight){
                        wor.deplacer(1);
                    }

                    //Augmentation ou diminution de l'angle de visée
                    if(augmentationAngleVisee){
                        wor.augmenterAngle();
                    }
                    else if(diminutionAngleVisee){
                        wor.diminuerAngle();
                    }
                }
            }
        }

        //Mise à jour de la logique propre à l'animation des explosions
        if(isExplosion){
            timerExplo+=delta;
            if(timerExplo >= tempsExplo-50){
                isExplosion=false;
                timerExplo=0;
            }

        }

        //Changement de musique
        changeMusic();
    }

    /*public static void main(String[] args) throws SlickException {
		//Main
		//A deplacer dans une classe apart à l'avenir
		
        int tailleBloc = 5;
        int blocLargeur = 300; // imperativement des multiples de 10, pour que le dessin des textures se fasse sans bug
        int blocHauteur = 200;
        AppGameContainer app = new AppGameContainer(new FenetreJeu(tailleBloc,blocLargeur,blocHauteur));
        app.setDisplayMode(blocLargeur*tailleBloc, blocHauteur*tailleBloc, false); // Mode fenêtré
        app.setVSync(false);
        app.setTargetFrameRate(120);
        app.start();
    }*/

    /*public void actionPerformed(ActionEvent e){
        if(e.getSource()==mt){

            if(changementPrint[0]==true){
                repaint();
            }
        }
    }*/

    /*public void keyPressed(KeyEvent e) {
        for(Worms wor: joueurs){
            if(wor.getMovingState() == true){
                if((System.currentTimeMillis()-antiRepeatTime) >= vitesseDep){
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        wor.deplacer(0);
                        antiRepeatTime = System.currentTimeMillis();
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                        wor.deplacer(1);
                        antiRepeatTime = System.currentTimeMillis();
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        if(wor.get_orientation()==0){
                            wor.set_vitesse_x(-5);
                        }
                        else{
                            wor.set_vitesse_x(5);
                        }
                        wor.set_vitesse_y(5);
                        antiRepeatTime = System.currentTimeMillis();
                    }
                }
            }

        }
    }*/

    public void keyPressed(int key, char c){
		//Traitement des entrées claviers (appuis spécifiquement)
		
        for(Worms wor: joueurs) {
            if (wor.getMovingState()) {
                if (Input.KEY_LEFT == key) {
                        //En appuyant sur la flèche gauche, on indique qu'on souhaite aller à gauche
                        isMovingLeft = true;
                    } else if (Input.KEY_RIGHT == key) {
                        //En appuyant sur la flèche droite, on indique qu'on souhaite aller à droite
                        isMovingRight = true;
                    } else if (Input.KEY_SPACE == key) {
                        //En appuyant sur la touche espace, on tente de sauter
                        if(wor.isOnFloor)
                            stackedEnter=0;

                        //Si on est au sol, on saute effectivement
                        if(stackedEnter==0  && wor.isOnFloor) {
                            wor.set_vitesse_y(-300);
                            if(wor.get_orientation()==0)
                                wor.set_vitesse_x(-100);
                            if(wor.get_orientation()==1)
                                wor.set_vitesse_x(100);
                            lastTime=System.currentTimeMillis();
                        }
                    stackedEnter++;

                        //Si l'on vient de sauter, alors on peut réaliser un double saut en réappuyant assez rapidement
                        if(stackedEnter>1 && System.currentTimeMillis()-lastTime<180){
                            wor.set_vitesse_y(-450);

                            if(wor.get_orientation()==0)
                                wor.set_vitesse_x(70);
                            if(wor.get_orientation()==1)
                                wor.set_vitesse_x(-70);
                            stackedEnter=0;
                        }
                        wor.onFloorUpdate();
                }
                else if(Input.KEY_ENTER == key){
                    //En appuyant sur entrée, on rentre dans l'inventaire
                    phaseInventaire = true;
                    wor.setMovingState(false);
                    gestionTours.setPhase(1);
                }
            }
            if(wor.getAimingState() && !phaseChoixPuissance){
                //Si l'on est en train de visée, alors...
                if (Input.KEY_UP == key) {
                    //On indique qu'on veut augmenter l'angle de visée en appuyant sur haut
                    augmentationAngleVisee = true;
                }
                else if(Input.KEY_DOWN == key){
                    //On indique qu'on veut diminuer l'angle de visée en appuyant sur bas
                    diminutionAngleVisee = true;
                }
                else if (Input.KEY_LEFT == key) {
                    //On peut se tourner à gauche
                    wor.setOrientation(0);
                    wor.updateViseeOrientation();
                } else if (Input.KEY_RIGHT == key) {
                    //On peut se tourner à droite
                    wor.setOrientation(1);
                    wor.updateViseeOrientation();
                }
                else if(Input.KEY_ENTER == key){
                    //On peut passer à la phase du choix de la puissance en appuyant sur entrée si on a finis de viser
                    phaseChoixPuissance = true;
                    timerChoixPuissance = 0;
                    enterRelache = false;
                    gestionTours.setPhase(2);
                }
            }
        }
        if (Input.KEY_M == key){
            //On peut désactiver/activer la musique en appuyant sur M
            musicActivation = !musicActivation;
            if(musicActivation){
                musiqueActuelle.setVolume(1);
            }
            else{
                musiqueActuelle.setVolume(0);
            }
        }
        else if(Input.KEY_B == key){
            //En mode développeur, il est possible de choisir de détruire ou poser des blocks grâce à la touche B
            antiExplosion = !antiExplosion;
        }
        else if(Input.KEY_C == key){
            // Activation/desactivation du mode développeur (ou "Cheat Mode") avec la touche C
            cheatMode = !cheatMode;
            if(cheatMode){
                gestionTours.showMessage("Cheat Mode activé !",2000,org.newdawn.slick.Color.magenta);
            }
            else{
                gestionTours.showMessage("Cheat Mode desactivé !",2000,org.newdawn.slick.Color.magenta);
                visualiserExplosion = false;
            }
        }
        /*else if(Input.KEY_V == key){
            experimentalVisee = !experimentalVisee;
            if(experimentalVisee){
                joueurs[0].setMovingState(false);
                joueurs[0].setAimingState(true);
                joueurs[0].initVisee();
            }
            else{
                joueurs[0].setMovingState(true);
                joueurs[0].setAimingState(false);
            }
        }*/
        /*else if(Input.KEY_T == key){
            joueurs[0].set_y(50);
            System.out.println(joueurs[0].physic.getPixelCoordX());
        }*/

    }

    // méthode exécutée à chaque fois qu’une touche est relâchée
    public void keyReleased(int key, char c) {
		//Traitement des entrées claviers (relachements de touches spécifiquement)
		
        if (Input.KEY_ESCAPE == key) {
            //Si l'on relache sur échap, on quitte le jeu
            container.exit();
        }
        else if (Input.KEY_LEFT == key) {
            //Si l'on relache la flèche gauche, alors on indique qu'on souhaite arrêter d'aller à gauche
            isMovingLeft = false;
        } else if (Input.KEY_RIGHT == key) {
            //Si l'on relache la flèche droite, alors on indique qu'on souhaite arrêter d'aller à droite
            isMovingRight = false;
        }
        else if (Input.KEY_UP == key) {
            //Si l'on relache la flèche haute, alors on arrête d'augmenter l'angle de visée
            augmentationAngleVisee = false;
        }
        else if(Input.KEY_DOWN == key){
            //Si l'on relache la flèche basse, alors on arrête de diminuer l'angle de visée
            diminutionAngleVisee = false;
        }
        else if(Input.KEY_ENTER == key){
            //Si l'on relache la touche entree, alors l'information est gardée en mémoire afin d'éventuellement déclencher le tir
            enterRelache = true;
        }
    }




    // méthode exécutée à chaque fois qu’une touche unicode est utilisée (donc pas CTRL, SHIFT ou ALT par exemple)
    /*public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }*/

    public void mousePressed(int button, int x, int y){
		//Traitement de la souris
		
        if(button == 0){//Clik gauche
            if(phaseInventaire){
                for(Worms wor:joueurs){
                    if(wor.isPlaying){
                        if(wor.interactInventaire(input)){
                            //Si le joueur vient de selectionner une arme dans l'inventaire
                            if(wor.getArmeActuelle() instanceof Teleporteur){
                                //Si c'est un teleporteur, alors on rentre dans la phase de teleportation
                                phaseTeleporteur = true;
                            }
                            else{
                                //Sinon, on lance la phase de visée classique
                                wor.setAimingState(true);
                                wor.initVisee();
                            }
                            //Dans tous les cas, on ferme l'inventaire
                            phaseInventaire = false;
                        }
                    }
                }
            }
            else if(phaseTeleporteur){
                //Si le joueur est en train de choisir un point de teleportation
                if(0<=x && x < largeur - 20 && 40 <= y && y < hauteur && (((gestionTours.getActualWorms()).physic.getContactBlock(x,y)).isEmpty())){
                    //S'il n'y a pas de blocks là où le joueur souhaite se teleporter ET que l'emplacement se situe bien sur la map
                    //alors le Worms est teleporté
                    (gestionTours.getActualWorms()).set_x(x);
                    (gestionTours.getActualWorms()).set_y(y);
                    drawTeleporteur.playSound();
                    phaseTeleporteur = false;//On met fin à la phase de teleportation
                    gestionTours.setPhase(3);//On passe à la phase de visualisation de l'explosion (qui sert enfaite surtout ici à laisser un petit temps
                    // au joueur pour admirer la teleportation)
                }
            }
            else if(cheatMode){
                //Avec le cheat mode activé, un appuie gauche provoque une explosion:
                experimentalExplosion(x,y,rayonExplosion);
            }
        }
        else if(button==1 && cheatMode){//Clik droit
            //Avec le cheat mode activé, un appuie droite provoque une teleportation:
            (gestionTours.getActualWorms()).set_x(x);
            (gestionTours.getActualWorms()).set_y(y);
        }
        else if(button==2 && cheatMode){//Clik molette
            //Avec le cheat mode activé, on peut decider de visualiser ou non le rayon de l'explosion en appuyant sur la molette
            visualiserExplosion = !visualiserExplosion;
        }
    }

    public void mouseWheelMoved(int change){
        //Gestion de la rotation de la molette

        //On change le rayon de l'explosion du cheat mode selon la rotation de la molette
        rayonExplosion += (change/120)*5;
        if(rayonExplosion <= 10){
            rayonExplosion = 10;
        }
    }



    public void experimentalExplosion(int xe,int ye,int rayon){
		//Explosion propre au cheat mode
        //On indique en paramètre l'emplacement et le rayon de l'explosion
		
        //Penser vérifier xe, ye dans les clous
        //Coin en haut à gauche du rectangle:
        int block_hg_x = (xe - rayon)/blockSize;
        block_hg_x = limiteInferieur(block_hg_x);
        int block_hg_y = (ye - rayon)/blockSize;
        block_hg_y = limiteInferieur(block_hg_y);
        //Coin en bas à droite du rectangle:
        int block_bd_x = (xe + rayon)/blockSize;
        block_bd_x = limiteSuperieurX(block_bd_x);
        int block_bd_y = (ye + rayon)/blockSize;
        block_bd_y = limiteSuperieurY(block_bd_y);

        //On détruit tous les blocks destructibles compris dans le rayon de l'explosion:
        double demi_block = blockSize/2.0;
        boolean destructible;
        for(int i=block_hg_y;i<=block_bd_y;i++){
            for(int j=block_hg_x;j<=block_bd_x;j++){
                if(distance(xe,ye,j*blockSize+demi_block,i*blockSize+demi_block)<=rayon){
                    destructible = true;
                    //On verifie que le block est destructible
                    for(int strong :blocIndestructibles){
                        if(terrain[i][j]==strong){
                            destructible = false;
                        }
                    }
                    if(destructible){
                        //Si il l'est, alors on declenche...
                        if(!antiExplosion){
                            //...la destruction des blocks
                            terrain[i][j] = 0;
                        }
                        else{
                            //ou la pose de blocks
                            terrain[i][j] = 1;
                        }
                    }
                }
            }
        }
    }

    public int limiteInferieur(int k){
        if(k<0){
            k = 0;
        }
        return k;
    }

    public int limiteSuperieurX(int k){
        if(k>=largeurBlock){
            k = largeurBlock-1;
        }
        return k;
    }

    public int limiteSuperieurY(int k){
        if(k>=hauteurBlock){
            k = hauteurBlock-1;
        }
        return k;
    }

    public double distance(double x1,double y1,double x2,double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public void spawnWorm() throws IOException {
        //Fonction assurant le spawn des Worms sur la map
        int spawnArea=largeur/(joueurs.length+1);
        int i=1;
        int antiBug, actualI;
        for(int j=0;j<3;j++){
            for(int k=0;k<=3;k+=3){
                int xs;
                int ys;
                antiBug = 0;
                actualI = i;
                do{
                    xs=(int) (actualI*spawnArea-Math.random()*spawnArea);
                    ys=45;
                    antiBug++;
                    System.out.println("antiBug="+antiBug);
                    if(antiBug>=25){
                        actualI = (int)(Math.random()*joueurs.length)+1;
                    }
                    while(!((joueurs[j+k].physic.getContactBlock(xs,ys)).isEmpty())&& ys<hauteur-joueurs[j+k].getHitBoxHauteur()){
                        ys++;
                    }
                    //System.out.println("xs"+xs);
                    //System.out.println("ys"+ys);
                }while(ys == hauteur-joueurs[j+k].getHitBoxHauteur() || eauVerticale(xs,ys));
                //On cherche un nouvel emplacement de spawn tant qu'il y a de l'eau à la verticale de l'emplacement actuelle

                while((joueurs[j+k].physic.getContactBlock(xs,ys)).isEmpty()){
                    ys++;
                }//On "pose" le Worms au sol

                ys--;
                joueurs[j+k].setpos(xs,ys);
                i++;
            }
        }

        //On regenere le terrain avec la même bitmap que l'initialisateur mais sans inclure l'eau cette fois
        //En effet, l'avoir sur le tableau bidimensionnel de la physique n'est utile que lors du spawn des
        //Worms
        monde.genererTerrain(xMonde,yMonde,adresseMap,drawScaleX,drawScaleY);
        monde.generateLimite(xMonde,yMonde);
        int[][] terrainBis=monde.getTerrainInitial();
        for(int k=0;k<terrainBis.length;k++){
            for(int j=0;j<terrainBis[0].length;j++){
                terrain[k][j] = terrainBis[k][j];
            }
        }
    }

    public boolean eauVerticale(int xw,int yw){
        //Permet de savoir si de l'eau se trouve directement en dessous de l'abscisse x en argument
        //int yw = 2; //Ne pas mettre 0 ou la fonction bug à cause de la limite invisible en haut de la map
        while(terrain[yw/blockSize][xw/blockSize]==0){
            yw+=blockSize;
        }
        System.out.println("eauVerticale="+terrain[yw/blockSize][xw/blockSize]);
        return terrain[yw/blockSize][xw/blockSize]==2;
    }

    public void setPhaseInventaire(boolean phaseInventaire) {
        this.phaseInventaire = phaseInventaire;
    }

    public void changeMusic(){
        //Permet de passer automatiquement à la musique suivante, en detectant que la précedente a fini d'être jouée
        if(!musiqueActuelle.playing()){
            musiqueActuelle = battlePlayList[musicIndex];
            musiqueActuelle.play();
            if(musicActivation){
                musiqueActuelle.setVolume(1);
            }
            else{
                musiqueActuelle.setVolume(0);
            }
            musicIndex++;
            if(musicIndex==battlePlayList.length){
                musicIndex=0;
            }
        }
    }

    public void setPhaseTeleporteur(boolean phaseTeleporteur) {
        this.phaseTeleporteur = phaseTeleporteur;
    }

}
