import org.newdawn.slick.SlickException;

import java.awt.*;
import java.util.ArrayList;

public class Worms {
    protected org.newdawn.slick.Color couleur;
    protected String name;
    protected int life;
    protected int x; //Les coordonnées x,y correspondent au coin en bas à gauche du Worms
    protected int y;
    protected int terrain[][];
    protected int blockSize;
    protected boolean isMoving; //Servira à savoir si un Worms est dans sa phase de déplacement
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
    protected org.newdawn.slick.Image skinLeft;
    protected org.newdawn.slick.Image skinRight;

    //protected int compteurTest;

    public Worms(int t, String n,int[][] terrain,int blockSize,boolean[] changementPrint,int x,int y) throws SlickException { //t=0 ou 1, pour savoir quelle équipe
        if(t==0) couleur=org.newdawn.slick.Color.blue;
        else couleur=org.newdawn.slick.Color.red;
        name=n;
        life=200;
        this.terrain = terrain;
        this.blockSize = blockSize;
        isMoving = false;
        this.changementPrint =  changementPrint;
        this.x = x;
        this.y = y;
        physic = new MoteurPhysique(terrain,blockSize,hitBoxHauteur,hitBoxLargeur,blocIntraversables,masse,x,y);
        Force forceGravite = new Force(0,9.81);
        physic.addForce(forceGravite);
        orientation = 1;
        skinLeft = new org.newdawn.slick.Image("images/skin_worms_left.png");
        skinRight = new org.newdawn.slick.Image("images/skin_worms_right.png");
    }

    public void applyPhysic(int delta){
        physic.applyForces(delta);
        x = physic.getPixelCoordX();
        y = physic.getPixelCoordY();
        onFloorUpdate();
    }

    public void modifierVie(int hp){
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

		//Début du moteur physique !
        boolean mouvPossible = true;
        ArrayList<Block> BlockEnContact = physic.getContactBlock(tempx,tempy);
        for(Block bContact:BlockEnContact){
            if(physic.isIntraversable(bContact)){//Si le Worms est en contact avec un bloc ...
                mouvPossible = false;//alors le mouvement est impossible
            }
        }
        if(mouvPossible){//Si mouvement possible, alors on met à jour les coordonnées RÉELLES du Worms
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
                ArrayList<Block> BlockEnContact2 = physic.getContactBlock(tempx,tempy);
                boolean mouvPossible2 = true;
                
                //Ce qui suit est une boucle de sécurité
                //Elle vérifie que l'escalade ne met pas le Worms à cheval sur des blocks,
                //ce qui serait physiquement impossible
                for(Block bContact:BlockEnContact2){
                    if(physic.isIntraversable(bContact)){
                        mouvPossible2 = false;
                    }
                }
                if(mouvPossible2){//Si tout va bien, alors on met à jour les coordonnées RÉELLES du Worms
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

    public void draw(org.newdawn.slick.Graphics g){
		//Dessine le Worms à l'écran
        
        //g.setColor(couleur);
        //g.fillRect(x,y-hitBoxHauteur+1,hitBoxLargeur,hitBoxHauteur);

        if(orientation==0){
            skinLeft.draw(x,y-hitBoxHauteur+1);
        }
        else{
            skinRight.draw(x,y-hitBoxHauteur+1);
        }
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
        physic.set_vitesse_X(vitesse_x);
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
        System.out.println(physic.getContactBlock(x,y+1));
    }
}
