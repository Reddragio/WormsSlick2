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
    protected org.newdawn.slick.Color couleur;
    protected String name;
    protected String Couleur;
    protected double life;
    protected int x; //Les coordonnées x,y correspondent au coin en bas à gauche du Worms
    protected int y;
    protected int terrain[][];
    protected int blockSize;
    protected boolean isOnFloor; //Pour savoir si un worm est au sol ou non
    protected boolean[] changementPrint;
    protected int orientation;
    protected MoteurPhysique physic;
    protected final static double masse = 0.01;
    protected final static double g = 9.81;
    protected final static int hitBoxHauteur = 40;
    protected final static int hitBoxLargeur = 20;
    protected final static int blocIntraversables[] = {1,3};
    protected final static double facteurEchelle = 0.05;
    protected final static int climbAbility = 2; //Nombre de bloc que le Worms est capable d'escalader
    protected final static double degatChuteMaximal = 100;
    protected final static double vitesseYChuteLimite = 100;
    protected org.newdawn.slick.Image skinLeft;
    protected org.newdawn.slick.Image skinRight;
    protected org.newdawn.slick.Image skinGrave;
    protected Weapon armeActuelle;
    protected double angleVisee;//Entre 0 et 180°
    protected double pasVisee;
    protected Inventaire inventaire;
    protected long lastTimeDegatChute;

    protected HashMap<String, Color> dico;
    protected TrueTypeFont font2;

    //Booléens servant à enchainer les phases de jeu
    protected boolean isMoving; //Servira à savoir si un Worms est dans sa phase de déplacement
    protected boolean isAiming; //est en train de viser
    protected boolean isPlaying;

    public Worms(int t, String c, String n,int[][] terrain,int blockSize,boolean[] changementPrint,int x,int y) throws SlickException { //t=0 ou 1, pour savoir quelle équipe
        if(t==0) couleur=org.newdawn.slick.Color.blue;
        else couleur=org.newdawn.slick.Color.red;

        name=n;
        Couleur=c;
        life=200;
        this.terrain = terrain;
        this.blockSize = blockSize;
        isMoving = false;
        this.changementPrint =  changementPrint;
        this.x = x;
        this.y = y;
        physic = new MoteurPhysique(terrain,blockSize,hitBoxHauteur,hitBoxLargeur,blocIntraversables,1.0/12.5,1.0/20.0,1.0/20.0,1.0/12.5,masse,x,y);
        Force forceGravite = new Force(0,g/200);
        physic.addForce(forceGravite);
        orientation = 1;
        pasVisee = 0.5;
        angleVisee = 0;
        isPlaying = false;
        skinLeft = new org.newdawn.slick.Image("images/Worm"+c+"_left.png");
        skinRight = new org.newdawn.slick.Image("images/Worm"+c+"_right.png");
        skinGrave = new org.newdawn.slick.Image("/images/GraveStone.png");

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
    }

    public void applyPhysic(int delta){
        physic.applyForces(delta);
        x = physic.getPixelCoordX();
        y = physic.getPixelCoordY();
        onFloorUpdate();
        applyDegatChute();
    }

    public void modifierVie(double hp){
		//Ajoute/eleve des points de vie au Worms
        life+=hp;
    }

    public void deplacer(int direction){
		//Deplace le Worms à gauche ou à droite
        int tempx = x;
        int tempy = y;
        if(direction==0){
            tempx -= 1;//Si on va à gauche
        }
        else if(direction==1){
            tempx += 1;//Si on va à droite
        }
        orientation = direction;//On garde l'orientation en mémoire, pour afficher le Worms dans la bonne direction ainsi que
        //pour sauter dans la bonne direction

		//Début de la gestion de la physique !
        ArrayList<Block> BlockEnContact = physic.getContactBlock(tempx,tempy);

        if(BlockEnContact.isEmpty()){//Si mouvement possible, alors on met à jour les coordonnées RÉELLES du Worms
            x = tempx;
            y = tempy;
            physic.setX(x);
            physic.setY(y);
        }
        else{//Si le mouvement est impossible, on regarde si le Worms n'a pas tenté d'escalader un block
            Block BlocBasWorms = physic.blockEquivalent(tempx,tempy);
            int yGrilleBasWorms = BlocBasWorms.y;
            //compteurTest++;
            //System.out.println(compteurTest);
            //System.out.println("Worms x="+BlocBasWorms.x+" y="+BlocBasWorms.y);
            boolean sameYforAll = true;
            int max_diff = 1;
            //La boucle suivante sert à regarder si tous les blocs en contact sont escaladables
            //(sachant que la capacité d'escalade du Worms est défini par la variable ClimbAbility)
            for(Block bContact:BlockEnContact){
                if(bContact.y < yGrilleBasWorms - climbAbility +1){//detecte un bloc non escaladable
                    sameYforAll = false;
                }
                else{
                    int test = yGrilleBasWorms - bContact.y + 1;
                    if(test>max_diff){
                        max_diff = test;
                    }
                }
            }
            if(sameYforAll){//Si tous les blocs sont escaladables, alors on escalade !
                tempy -= (int)(blockSize*max_diff);//Pour se faire, on diminue la coordonnée y

                //Ce qui suit est une sécurité
                //Elle vérifie que l'escalade ne met pas le Worms à cheval sur des blocks,
                //ce qui serait physiquement impossible
                BlockEnContact = physic.getContactBlock(tempx,tempy);
                if(BlockEnContact.isEmpty()){//Si tout va bien, alors on met à jour les coordonnées RÉELLES du Worms
					//Le Worms vient d'escalader un block !
                    x = tempx;
                    y = tempy;
                    physic.setX(x);
                    physic.setY(y);
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
        set_x(x);
        set_y(y);
    }

    public void set_x(int x){
		//Permet de définir la coordonnée x
		//A n'utiliser que pour la phase d'experimentation
        physic.setX(x);
    }

    public void set_y(int y){
		//Permet de définir la coordonnée y
        physic.setY(y);
    }

    public void set_vitesse_x(int vitesse_x){
        physic.set_vitesse_x(vitesse_x);
    }

    public void set_vitesse_y(int vitesse_y){
        physic.set_vitesse_y(vitesse_y);
    }

    public int get_orientation(){
		//Retourne l'orientation du Worms
		// 0 = Gauche et 1 = droite
        return orientation;
    }

    public void onFloorUpdate(){
        if(physic.getContactBlock(x,y+1).isEmpty()) isOnFloor=false;
        else isOnFloor=true;
    }

    public boolean nearFloor(){
        return physic.getContactBlock(x,y).isEmpty() && !(physic.getContactBlock(x,y+6).isEmpty());
    }

    public void drawVisee(){
        armeActuelle.drawVisee(angleVisee);
    }

    public void initVisee(){
        angleVisee = 0;
        updateViseeOrientation();
    }

    public void updateViseeOrientation(){
        armeActuelle.init(x,y,hitBoxLargeur,hitBoxHauteur,orientation);
    }

    public void augmenterAngle(){
        angleVisee -= pasVisee;
        if(angleVisee<=-90){
            angleVisee = -89;
        }
    }

    public void diminuerAngle(){
        angleVisee += pasVisee;
        if(angleVisee>=90){
            angleVisee = 89;
        }

    }

    public void applyDegatChute(){
        if(nearFloor() && System.currentTimeMillis() - lastTimeDegatChute >= 1000){
            double vitesseY,degatChute;
            vitesseY = physic.getVitesse_y() * 12;//*12 parce que les valeurs de vitesse ont été étudié en faisant
            //tourner le jeu à 10 fps au lieu de 120 (les vitesses étaient 12 fois plus grandes)
            if(vitesseY>=20){
                lastTimeDegatChute = System.currentTimeMillis();
                if(vitesseY>vitesseYChuteLimite){
                    vitesseY = vitesseYChuteLimite;
                }
                degatChute = (vitesseY/vitesseYChuteLimite)*degatChuteMaximal;
                modifierVie(-degatChute);
            }
        }
    }

    public void setWeapon(Weapon armeTemp){
        armeActuelle = armeTemp;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public Weapon getArmeActuelle() {
        return armeActuelle;
    }

    public int getX (){return x;}

    public int getY (){return y;}

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void drawInventaire(Input input){
        inventaire.draw(input);
    }

    public boolean interactInventaire(Input input){
        return inventaire.interact(input,this);
    }

    public static int getHitBoxLargeur() {
        return hitBoxLargeur;
    }

    public static int getHitBoxHauteur() {
        return hitBoxHauteur;
    }
}
