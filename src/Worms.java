import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Input;
import java.io.InputStream;
import org.newdawn.slick.util.ResourceLoader;
import java.util.*;

import java.awt.*;
import java.util.ArrayList;

public class Worms {
    protected org.newdawn.slick.Color couleur;//Couleur du Worms; Caracterise l'équipe auquel il appartient
    protected String name;
    protected String Couleur;//Couleur du Worms, sous forme de String
    protected double life;//Points de vie du Worms (200 en début de partie)
    protected int x; //Les coordonnées x,y correspondent au coin en bas à gauche du Worms
    protected int y;
    protected int terrain[][];//Pointeur vers le tableau bidimensionnel de la physique
    protected int blockSize;//Taille des blocks (5 pixels dans les faits)
    protected boolean isOnFloor; //Pour savoir si un worm est au sol ou non
    protected boolean[] changementPrint;
    protected int orientation;//Orientation du Worms (0 pour gauche, 1 pour droite)
    protected MoteurPhysique physic;//Instance du moteur physique dédiée au Worms
    protected final static double masse = 0.01;//Masse du Worms, 0.01 kg ou 10 g
    protected final static double g = 9.81;//Intensité de pesanteur
    protected final static int hitBoxHauteur = 40;//Dimensions de la hitbox des Worms
    protected final static int hitBoxLargeur = 20;
    protected final static int blocIntraversables[] = {1,3};//Blocks intraversables, en l'occurence les blocks du décor et les blocks invisibles sur le bord de la map
    protected final static double facteurEchelle = 0.05;
    protected final static int climbAbility = 2; //Nombre de bloc que le Worms est capable d'escalader
    protected final static double degatChuteMaximal = 100;//Degat de chute maximal applicable
    protected final static double vitesseYChuteLimite = 100;//Vitesse correspondant à ces dégats maximaux
    protected org.newdawn.slick.Image skinLeft;//Image du Worms orienté vers la gauche
    protected org.newdawn.slick.Image skinRight;//Image du Worms orienté vers la droite
    protected org.newdawn.slick.Image skinGrave;//Image de la tombe du Worms, affichée lorsqu'il est mort
    protected Weapon armeActuelle;//Arme actuellement utilisée par le Worms
    protected double angleVisee;//Entre 0 et 180°
    protected double pasVisee;//Pas de la visée, doit permettre un compromis entre précision et vitesse pour visée
    protected Inventaire inventaire;//Inventaire du Worms, caracterise le nombre d'arme de chaque categorie qu'il possède
    protected long lastTimeDegatChute;//Empeche le Worms de subir des dégats de chute à la chaine dans un court instant

    protected HashMap<String, Color> dico;//Dictionnaire faisant correspondre à une couleur sous forme de String son équivalent sous forme d'objet Color
    protected TrueTypeFont font2;//Police d'écriture utiliser pour l'affichage du nom du Worms et de sa vie

    //Booléens servant à enchainer les phases de jeu
    protected boolean isMoving; //Servira à savoir si un Worms est dans sa phase de déplacement
    protected boolean isAiming; //est en train de viser
    protected boolean isPlaying;//Sert à savoir si c'est le tour du Worms ou non
    protected boolean aDejaJoue;
    protected boolean alive;//Sert à savoir si le Worms est en vie ou non
    protected int ordre;

    //Affichage de la perte de pvs
    protected long timerMessage;//Variables servant à temporiser l'affichage
    protected long tempsMessage;
    protected String messageAffiche;//Variable contenant le message affiché, en l'occurence ici le nombre de pvs perdus
    protected boolean printingMessage;//Variable indiquant qu'une perte de pvs doit actuellement être montrée
    protected org.newdawn.slick.Color couleurMessage;//Couleur d'affichage, en l'occurence du rouge pour la perte de pvs
    protected int xDegat;//Coordonnées x et y de l'emplacement où ont été subis les dégâts
    protected int yDegat;

    public Worms(String c, String n,int[][] terrain,int blockSize,int x,int y) throws SlickException { //t=0 ou 1, pour savoir quelle équipe
        //Constructeur des Worms
        name=n;
        Couleur=c;
        life=200;//200 pvs par défaut
        alive=true;//En vie par défaut
        this.terrain = terrain;
        this.blockSize = blockSize;
        isMoving = false;
        this.x = x;
        this.y = y;
        //Création d'une instance du moteur physique propre à chaque Worms:
        //Les coeffs de reflexion sont assez elevés, ce qui permet au Worms de rebondir légerement sur les parois mais pas autant que les grenades
        physic = new MoteurPhysique(terrain,blockSize,hitBoxHauteur,hitBoxLargeur,blocIntraversables,1.0/12.5,1.0/20.0,1.0/20.0,1.0/12.5,masse,x,y,true);
        //On indique que les Worms subissent la gravité:
        Force forceGravite = new Force(0,g/200);
        physic.addForce(forceGravite);

        orientation = 1;//Par défaut, ils sont orientés vers la droite
        pasVisee = 0.5;//Le pas pour la bisée est de 0.5 degré
        angleVisee = 0;//L'angle de visée par défaut est de 0 degré, soit l'horizontale
        isPlaying = false;

        //On récupère les images gauche et droite du Worms correspondant à sa couleur
        skinLeft = new org.newdawn.slick.Image("images/Worm"+c+"_left.png");
        skinRight = new org.newdawn.slick.Image("images/Worm"+c+"_right.png");
        skinGrave = new org.newdawn.slick.Image("/images/GraveStone.png");

        //On initialise l'inventaire du Worms
        inventaire = new Inventaire(terrain[0].length*blockSize,terrain.length*blockSize);
        lastTimeDegatChute = 0;

        //Init police nom et vie
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("./fonts/WormsFont.ttf");
            Font police = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            police = police.deriveFont(18f); // set font size
            font2 = new TrueTypeFont(police, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Création du dictionnaire des couleurs
        dico = new HashMap<String, Color>();
        dico.put("Rouge",Color.red);
        dico.put("Bleu",Color.blue);
        dico.put("Noir",Color.black);
        dico.put("Blanc",Color.white);
        dico.put("Vert",Color.green);

        //Initialisation de l'affichage de la perte de pvs
        timerMessage = 0;
        tempsMessage = 0;
        messageAffiche = "";
        printingMessage = false;
        couleurMessage = org.newdawn.slick.Color.black;
        xDegat = 50;
        yDegat = 50;
    }

    public void applyPhysic(int delta,GestionTours gestionTours,GestionTerrain monde){
        //Applique les lois de la physique aux Worms

        physic.applyForces(delta);
        x = physic.getPixelCoordX();
        y = physic.getPixelCoordY();

        onFloorUpdate();//On met à jour la variable indiquant si le Worms se trouve ou non sur le sol

        applyDegatChute();//On applique d'éventuels dégâts de chute dus à un contact avec le sol à une vitesse trop élevée

        //detection mort
        if(alive && (life<=0 || isUnderwater(monde))){
            //Si le Worms a nombre de pvs inferieur ou égale à 0 ou se trouve sous l'eau (ie c'est noyé), alors ...
            alive = false;//On indique qu'il est mort
            life = -999;//On met arbitrairement sa vie à -999
            gestionTours.nouvelleMort(this);//On indique sa mort à la classe gestionTours
        }
    }

    public void modifierVie(double hp){
		//Ajoute/eleve des points de vie au Worms
        life+=hp;

        //On initialise l'affichage de la perte de pvs
        messageAffiche = String.valueOf((int)Math.abs(hp));//Le message est la valeur absolue des pvs perdus
        tempsMessage = 1500;//qui s'affichera pendant 1,5s
        printingMessage = true;
        timerMessage=0;
        xDegat = x;//A l'emplacement où le Worms a pris ces dégâts
        yDegat = y;
        if(hp<0){
            //S'affiche en rouge s'il s'agit de pvs perdus
            couleurMessage = org.newdawn.slick.Color.red;
        }
        else{
            //S'affiche en vert s'il s'agit de pvs gagnés
            //(Il est actuellement impossible de regagner des pvs dans la version actuelle, mais il y aura peut-etre des
            //caisses pour se soigner dans une version future)
            couleurMessage = org.newdawn.slick.Color.green;
        }
    }

    public void printPerteVie(int delta){
        //Fonction appellée à chaque frame permettant l'affichage des pvs perdus

        if(printingMessage){
            if(couleurMessage == org.newdawn.slick.Color.red){
                //On affiche les perdus, en décalant le message vers le haut et en le rendant plus transparant au fil du temps qui s'écoule
                font2.drawString( xDegat+hitBoxLargeur/4,(float)(yDegat-hitBoxLargeur/2-(timerMessage/((double)tempsMessage))*70),messageAffiche,new org.newdawn.slick.Color((float)1,(float)0,(float)0,(float)(1-timerMessage/((double)tempsMessage))));
            }
            else{
                font2.drawString( xDegat+hitBoxLargeur/4,(float)(yDegat-hitBoxLargeur/2-(timerMessage/((double)tempsMessage))*70),messageAffiche,new org.newdawn.slick.Color((float)0,(float)1,(float)0,(float)(1-timerMessage/((double)tempsMessage))));
            }
            timerMessage+=delta;
            if(timerMessage>=tempsMessage){
                //Une fois le temps d'affichage écoulé, on met fin à l'affichage
                printingMessage = false;
                timerMessage = 0;
            }
        }
    }

    public void deplacer(int direction){
        if (life > 0) {
            //Deplace le Worms à gauche ou à droite
            int tempx = x;
            int tempy = y;
            if (direction == 0) {
                tempx -= 1;//Si on va à gauche
            } else if (direction == 1) {
                tempx += 1;//Si on va à droite
            }
            orientation = direction;//On garde l'orientation en mémoire, pour afficher le Worms dans la bonne direction ainsi que
            //pour sauter dans la bonne direction

            //Début de la gestion de la physique !
            ArrayList<Block> BlockEnContact = physic.getContactBlock(tempx, tempy);

            if (BlockEnContact.isEmpty()) {//Si mouvement possible, alors on met à jour les coordonnées RÉELLES du Worms
                x = tempx;
                y = tempy;
                physic.setX(x);
                physic.setY(y);
            } else {//Si le mouvement est impossible, on regarde si le Worms n'a pas tenté d'escalader un block
                Block BlocBasWorms = physic.blockEquivalent(tempx, tempy);
                int yGrilleBasWorms = BlocBasWorms.y;
                //compteurTest++;
                //System.out.println(compteurTest);
                //System.out.println("Worms x="+BlocBasWorms.x+" y="+BlocBasWorms.y);
                boolean sameYforAll = true;
                int max_diff = 1;
                //La boucle suivante sert à regarder si tous les blocs en contact sont escaladables
                //(sachant que la capacité d'escalade du Worms est défini par la variable ClimbAbility)
                for (Block bContact : BlockEnContact) {
                    if (bContact.y < yGrilleBasWorms - climbAbility + 1) {//detecte un bloc non escaladable
                        sameYforAll = false;
                    } else {
                        int test = yGrilleBasWorms - bContact.y + 1;
                        if (test > max_diff) {
                            max_diff = test;
                        }
                    }
                }
                if (sameYforAll) {//Si tous les blocs sont escaladables, alors on escalade !
                    tempy -= (int) (blockSize * max_diff);//Pour se faire, on diminue la coordonnée y

                    //Ce qui suit est une sécurité
                    //Elle vérifie que l'escalade ne met pas le Worms à cheval sur des blocks,
                    //ce qui serait physiquement impossible
                    BlockEnContact = physic.getContactBlock(tempx, tempy);
                    if (BlockEnContact.isEmpty()) {//Si tout va bien, alors on met à jour les coordonnées RÉELLES du Worms
                        //Le Worms vient d'escalader un block !
                        x = tempx;
                        y = tempy;
                        physic.setX(x);
                        physic.setY(y);
                    }
                }
            }
        }
    }

    public boolean getMovingState(){
		//Indique si le Worms est en phase de déplacement ou non
        return isMoving;
    }

    public void setMovingState(boolean etat){
		//Permet de définir si le Worms est en phase de déplacement ou non
        isMoving = etat;
    }

    public boolean getAimingState() {
        //Permet de savoir si le Worms est actuellement en train de viser ou non
        return isAiming;
    }

    public void setAimingState(boolean etat){
        //Permet de définir si le Worms est en phase de déplacement ou non
        isAiming = etat;
    }

    public void draw(org.newdawn.slick.Graphics g) throws SlickException {
		//Dessine le Worms à l'écran
        
        //g.setColor(couleur);
        //g.fillRect(x,y-hitBoxHauteur+1,hitBoxLargeur,hitBoxHauteur);
        if(life>0) {
            if (orientation == 0) {
                skinLeft.draw(x, y - hitBoxHauteur + 1);
            } else {
                skinRight.draw(x, y - hitBoxHauteur + 1);
            }

            //Affichage du nom

            font2.drawString(x - (font2.getWidth(name) / 2) + 10, y - hitBoxHauteur - 30, name, dico.get(Couleur));
            font2.drawString(x - (font2.getWidth("" + (int)life) / 2) + 10, y - hitBoxHauteur - 15, "" + (int)life, dico.get(Couleur));
        } else{
            font2.drawString(x - (font2.getWidth("RIP "+name) / 2) + 10, y - hitBoxHauteur - 15, "RIP "+name, dico.get(Couleur));
            skinGrave.draw(x, y - hitBoxHauteur +5,26,40);
        }
    }

    public void setpos(int x, int y){
        //Permet de définir la position du Worms
        //Fonction servant pour la teleportation

        set_x(x);
        set_y(y);
    }

    public void set_x(int x){
		//Permet de définir la coordonnée x
		//Utile pour la teleportation

        physic.setX(x);
    }

    public void set_y(int y){
		//Permet de définir la coordonnée y
        //Utile pour la teleportation

        physic.setY(y);
    }

    public void set_vitesse_x(int vitesse_x){
        //Permet de définir la vitesse selon l'axe x
        //utilisé pour le saut

        physic.set_vitesse_x(vitesse_x);
    }

    public void set_vitesse_y(int vitesse_y){
        //Permet de définir la vitesse selon l'axe y
        //utilisé pour le saut

        physic.set_vitesse_y(vitesse_y);
    }

    public int get_orientation(){
		//Retourne l'orientation du Worms
		// 0 = Gauche et 1 = droite
        return orientation;
    }

    public void onFloorUpdate(){
        //Met à jour la variable indiquant si le Worms se trouve ou non sur le sol

        //Regarde si le Block en dessous du Worms est du vide,
        //si c'est le cas, alors le Worms est en train de tomber et par consequent n'est pas sur le sol
        //Sinon, alors le Worms est sur le sol
        if(physic.getContactBlock(x,y+1).isEmpty()) isOnFloor=false;
        else isOnFloor=true;
    }

    public boolean nearFloor(){
        //Sert à savoir si le Worms est à 5 pixels ou moins du sol

        return physic.getContactBlock(x,y).isEmpty() && !(physic.getContactBlock(x,y+6).isEmpty());
    }

    public void drawVisee(){
        //Dessine l'interface de visée

        armeActuelle.drawVisee(angleVisee);
    }

    public void initVisee(){
        //Initialise la phase de visée

        angleVisee = 0;
        updateViseeOrientation();
    }

    public void updateViseeOrientation(){
        //Sert à mettre à jour la visée lors d'un changement d'orientation

        armeActuelle.init(x,y,hitBoxLargeur,hitBoxHauteur,orientation);
    }

    public void augmenterAngle(){
        //Sert à augmenter l'angle de visée du pas de visée

        angleVisee -= pasVisee;
        if(angleVisee<=-90){
            angleVisee = -89;
        }
    }

    public void diminuerAngle(){
        //Sert à diminuer l'angle de visée du pas de visée

        angleVisee += pasVisee;
        if(angleVisee>=90){
            angleVisee = 89;
        }

    }

    public void applyDegatChute(){
        //Applique les éventuels dégâts de chûte

        if(nearFloor() && System.currentTimeMillis() - lastTimeDegatChute >= 1000){
            //Si l'on est près du sol et que le Worms ne s'est pas pris des dégats de chute les dernières 1000ms

            double vitesseY,degatChute;
            vitesseY = physic.getVitesse_y() * 12;//*12 parce que les valeurs de vitesse ont été étudié en faisant
            //tourner le jeu à 10 fps au lieu de 120 (les vitesses étaient 12 fois plus grandes)

            if(vitesseY>=20){
                //Si la vitesse est superieur au seuil pour applique des dégats de chute...
                lastTimeDegatChute = System.currentTimeMillis();//On enregistre l'instant où le Worms se prend ces dégâts
                if(vitesseY>vitesseYChuteLimite){
                    //On limite la vitesse de chute si elle est excessive
                    vitesseY = vitesseYChuteLimite;
                }
                //On calcul les dégâts de chute selon la vitesse du Worms:
                degatChute = (vitesseY/vitesseYChuteLimite)*degatChuteMaximal;
                //Enfin, on lui retire le nombre de pvs correspondants:
                modifierVie(-degatChute);
            }
        }
    }

    public boolean isUnderwater(GestionTerrain monde){
        //Indique si le Worms se trouve sous l'eau

        return y>(monde.getNiveauEau()+1)*blockSize;
    }

    public void setWeapon(Weapon armeTemp){
        //Permet de définir l'arme actuellement utilisée par le Worms

        armeActuelle = armeTemp;
    }

    public void setOrientation(int orientation) {
        //Permet de définir l'orientation du Worms (0 pour gauche, 1 pour droite)

        this.orientation = orientation;
    }

    public Weapon getArmeActuelle() {
        //Permet d'obtenir l'arme actuellement utilisée par le Worms

        return armeActuelle;
    }

    //Permet d'obtenir la coordonnée X du Worms:
    public int getX (){return x;}

    //Permet d'obtenir la coordonnée Y du Worms:
    public int getY (){return y;}

    //Permet de définir si c'est le tour du Worms ou non:
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    //Permet de savoir si c'est actuellement le tour du Worms ou non:
    public boolean isPlaying() {
        return isPlaying;
    }

    public void drawInventaire(Input input){
        //Dessine à l'écran l'inventaire du Worms
        inventaire.draw(input);
    }

    public boolean interactInventaire(Input input){
        //Permet à l'utilisateur d'interargir avec l'inventaire selon la position de sa souris
        return inventaire.interact(input,this);
    }

    public static int getHitBoxLargeur() {
        //retourne la largeur de la hitbox
        return hitBoxLargeur;
    }

    public static int getHitBoxHauteur() {
        //retourne la hauteur de la hitbox
        return hitBoxHauteur;
    }

    public boolean estEnVie(){
        //Indique si le Worms est en vie
        return life>0;
    }

    public String getCouleur() {
        //Renvoie la couleur du Worms
        return Couleur;
    }

    public boolean aDejaJoue() {
        return aDejaJoue;
    }

    public void setaDejaJoue(boolean aDejaJoue) {
        this.aDejaJoue = aDejaJoue;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getOrdre() {
        return ordre;
    }

    public String getName() {
        return name;
    }

    public boolean inside(int x1,int y1){
        //Permet de savoir si des coordonnées sont au sein de la hitbox du Worms
        //Sert notamment pour détecter la collision d'une rocket avec un Worms

        return x <= x1 && x1 <= x+hitBoxLargeur && y-hitBoxHauteur<= y1 && y1<=y;
    }

    public void setInventaire(Inventaire inventaire) {
        //Permet de redefinir l'inventaire d'un Worms

        this.inventaire = inventaire;
    }

    public void synchroniserInventaire(Worms wor){
        //Permet de définir l'inventaire d'un autre Worms comme celui du Worms actuel
        //Cette fonction sert à synchroniser l'inventaire au sein de chaque équipe

        wor.setInventaire(inventaire);
    }


}
