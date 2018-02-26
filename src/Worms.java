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
    protected boolean[] changementPrint;
    protected int orientation;
    protected double vitesse_x;
    protected double vitesse_y;
    protected double acceleration_x;
    protected double acceleration_y;
    //public double speed_force_x; //Résultante des vitesses amenées par les forces exterieurs sur x
    //public double speed_force_y;//Résultante des vitesses amenées par les forces exterieurs sur y
    protected final static double masse = 10;
    protected final static double g = 9.81;
    protected final static int hitBoxHauteur = 40;
    protected final static int hitBoxLargeur = 20;
    protected final static int blocIntraversables[] = {1};
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
        orientation = 1;
        vitesse_x = 0;
        vitesse_y = 0;
        acceleration_x = 0;
        acceleration_y = 0;
        //speed_force_x = 0;
        //speed_force_y = 0;
        //générateur aléatoire position
        skinLeft = new org.newdawn.slick.Image("images/skin_worms_left.png");
        skinRight = new org.newdawn.slick.Image("images/skin_worms_right.png");
        //compteurTest = 0;
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
        ArrayList<Block> BlockEnContact = getContactBlock(tempx,tempy);
        for(Block bContact:BlockEnContact){
            if(isIntraversable(bContact)){//Si le Worms est en contact avec un bloc ...
                mouvPossible = false;//alors le mouvement est impossible
            }
        }
        if(mouvPossible){//Si mouvement possible, alors on met à jour les coordonnées RÉELLES du Worms
            x = tempx;
            y = tempy;
            changementPrint[0] = true;
        }
        else{//Si le mouvement est impossible, on regarde si le Worms n'a pas tenté d'escalader un block
            Block BlocBasWorms = blockEquivalent(tempx,tempy);
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
                ArrayList<Block> BlockEnContact2 = getContactBlock(tempx,tempy);
                boolean mouvPossible2 = true;
                
                //Ce qui suit est une boucle de sécurité
                //Elle vérifie que l'escalade ne met pas le Worms à cheval sur des blocks,
                //ce qui serait physiquement impossible
                for(Block bContact:BlockEnContact2){
                    if(isIntraversable(bContact)){
                        mouvPossible2 = false;
                    }
                }
                if(mouvPossible2){//Si tout va bien, alors on met à jour les coordonnées RÉELLES du Worms
					//Le Worms vient d'escalader un block !
                    x = tempx;
                    y = tempy;
                    changementPrint[0] = true;
                }
            }
        }

    }

    public void applyForces(){
            //Applique aux Worms l'ensemble des forces auquel il est soumis, y compris la gravité
            int xtemp = x;
            int ytemp = y;
            acceleration_x = 0;
            acceleration_y = g;
            /*ArrayList<Block> BlockEnContact = getContactBlock(x,y-blockSize/2);
            Block BlocBasWorms = blockEquivalent(x,y-1);
            yGrilleBasWorms = BlocBasWorms.y;
            boolean surLeSol = false;
            for(Block bContact:BlockEnContact){
                if(bContact.y = yGrilleBasWorms){
                    surLeSol = true;
                }
            }
            if(surLeSol = true){
                acceleration -= g;
            }*/
            //Calcul du déplacement
            vitesse_x += acceleration_x * facteurEchelle;
            vitesse_y += acceleration_y * facteurEchelle;
            vitesse_x = limite(vitesse_x);
            vitesse_y = limite(vitesse_y);
            xtemp += (int)vitesse_x;
            ytemp += (int)vitesse_y;

			//Une fois le déplacement calculé, il faut vérifier que le mouvement est possible
			//et appliquer les éventuels rebonds sur les parois
            ArrayList<Block> BlockEnContact = getContactBlock(xtemp,ytemp);
			
			//On cherche sur la grille des blocs les coordonnées du haut, du bas, de la gauche et de la droite du Worms
            Block BlocBasWorms = blockEquivalent(xtemp,ytemp);
            int yGrilleBasWorms = BlocBasWorms.y;
            Block BlocHautWorms = blockEquivalent(xtemp,ytemp-hitBoxHauteur+1);
            int yGrilleHautWorms = BlocHautWorms.y;
            Block BlocGaucheWorms = blockEquivalent(xtemp,ytemp);
            int xGrilleGaucheWorms = BlocGaucheWorms.x;
            Block BlocDroiteWorms = blockEquivalent(xtemp+hitBoxLargeur-1,ytemp);
            int xGrilleDroiteWorms = BlocDroiteWorms.x;

            boolean one_change_x = true;
            boolean one_change_y = true;


            //Gestion des collisions (rebond à l'image de la reflexion en optique)
            for(Block bContact:BlockEnContact){
                if((bContact.y == yGrilleBasWorms || bContact.y == yGrilleHautWorms)&& bContact.x != xGrilleGaucheWorms && bContact.x != xGrilleDroiteWorms && one_change_y){
					//Contact avec une paroi horizontale
                    vitesse_x = (int)(vitesse_x/2.0);
                    vitesse_y = -(int)(vitesse_y/1.25);
                    one_change_y = false;
                    if(bContact.y == yGrilleBasWorms){
                        ytemp = yGrilleBasWorms*blockSize-1;
                    }
                    else{
                        ytemp = (yGrilleBasWorms+1)*blockSize-1;
                    }
                }
                else if((bContact.x == xGrilleGaucheWorms || bContact.x == xGrilleDroiteWorms)&& bContact.y != yGrilleBasWorms && bContact.y != yGrilleHautWorms && one_change_x){
                    //Contact avec une paroi verticale
                    vitesse_x = -(int)(vitesse_x/1.25);
                    vitesse_y = (int)(vitesse_y/2.0);
                    one_change_x = false;
                    if(bContact.x == xGrilleGaucheWorms){
                        xtemp = (xGrilleGaucheWorms+1)*blockSize;
                    }
                    else{
                        xtemp = xGrilleGaucheWorms*blockSize;
                    }
                }
            }

            /*boolean surLeSol = false;
            for(Block bContact:BlockEnContact){
                if(bContact.y == yGrilleBasWorms){
                    surLeSol = true;
                }
            }
            if(surLeSol){
                ytemp = yGrilleBasWorms*blockSize-1;
                vitesse_y = 0;
            }*/
			
			//On verifie que la physique n'a pas donné un résultat absurde:
            ArrayList<Block> BlockEnContact2 = getContactBlock(xtemp,ytemp);
            boolean mouvPossible = true;
            for(Block bContact:BlockEnContact2){
                if(isIntraversable(bContact)){
                    mouvPossible = false;
                }
            }

            if((ytemp != y || xtemp != x)&&mouvPossible){//Si tout va bien, alors on met à jour les coordonnées RÉELLES du Worms
                y = ytemp;
                x = xtemp;
                changementPrint[0] = true;
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

        //Debugage:
        /*ArrayList<Block> BlockEnContact = getContactBlock(x,y+1);
        for(Block bContact:BlockEnContact){
            g.setColor(Color.black);
            g.fillRect(bContact.x*blockSize,bContact.y*blockSize,blockSize,blockSize);
        }*/
    }

    public Block blockEquivalent(int xd,int yd){
		//Permet de savoir dans quel case se trouve un point donnée
		//Cette classe est fondamental pour la physique:
		//Elle permet de déplacer le Worms sur la grille réelle bien que la physique se base
		//sur la grille des blocks
        int XD = xd/blockSize;
        int YD = yd/blockSize;
        Block equiv = new Block(XD,YD,terrain[YD][XD]);
        return equiv;
    }

    public boolean isIntraversable(Block bloki){
		//Indique si un bloc est intraversable ou non
        boolean intraversable = false;
        for(int blocz:blocIntraversables){
            if(terrain[bloki.y][bloki.x] == blocz){
                intraversable = true;
            }
        }
        return intraversable;
    }

    public ArrayList<Block> getContactBlock(int tempx,int tempy){
		//Fonction fondamental pour la physique
		//--> Renvoit la liste de tous les blocks actuellement en contact
		//avec le Worms
        ArrayList<Block> templist = new ArrayList<Block>();
        for(int i=tempx;i<=tempx+hitBoxLargeur-1;i+=hitBoxLargeur-1){
            for(int j=tempy-hitBoxHauteur+1;j<=tempy;j+=blockSize){
                Block actuBlock = blockEquivalent(i,j);
                if(isIntraversable(actuBlock)){
                    templist.add(actuBlock);
                }
            }
        }
        Block actuBlock1 = blockEquivalent(tempx,tempy);
        if(isIntraversable(actuBlock1)){
            templist.add(actuBlock1);
        }
        Block actuBlock2 = blockEquivalent(tempx+hitBoxLargeur-1,tempy);
        if(isIntraversable(actuBlock2)){
            templist.add(actuBlock2);
        }
        for(int j=tempy-hitBoxHauteur+1;j<=tempy;j+=hitBoxHauteur-1){
            for(int i=tempx+blockSize-1;i<=tempx+hitBoxLargeur-1-blockSize;i+=blockSize){
                Block actuBlock = blockEquivalent(i,j);
                if(isIntraversable(actuBlock)){
                    templist.add(actuBlock);
                }
            }
        }
        return templist;
    }

    public double limite(double speed){
		//La fonction qui fache :) ...
		//Dans son implementation actuelle, le moteur physique est
		//incapable d'assurer une physique correcte avec des vitesses
		//superieurs à la taille des blocs. C'est pourquoi la vitesse
		//doit imperativement être limitée selon cette contrainte
        if(speed > blockSize){
            speed = blockSize;
        }
        else if(speed < -blockSize){
            speed = -blockSize;
        }
        return speed;
    }

    public void set_vitesse_x(double speed){
		//Définit la vitesse selon les x
        vitesse_x = speed;
    }

    public void set_vitesse_y(double speed){
		//Définit la vitesse selon les y
        vitesse_y = speed;
    }

    public void set_x(int x){
		//Permet de définir la coordonnée x
		//A n'utiliser que pour la phase d'experimentation
        this.x = x;
    }

    public void set_y(int y){
		//Permet de définir la coordonnée y
        this.y = y;
    }

    public int get_orientation(){
		//Retourne l'orientation du Worms
		// 0 = Gauche et 1 = droite
        return orientation;
    }
}
