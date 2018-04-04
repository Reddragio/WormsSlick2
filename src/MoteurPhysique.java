import org.newdawn.slick.SlickException;

import java.util.ArrayList;

public class MoteurPhysique {
    //Moteur physique du jeu
    protected final static double realBlockSize = 0.0125;//Taille réelle des blocks, 0.0125m ou 12,5cm
    protected final static double realPixelSize = 0.0025;//Taille réelle des pixels, 0.0025m ou 2,5cm
    protected final static double facteurEchelle = 0.05;//facteur d'échelle
    protected final static double g = 9.81;//Intensité de pesanteur terrestres
    //Coefficient de rebond/reflexion sur les parois:
    protected double coeffReflectionVerticaleX;
    protected double coeffReflectionVerticaleY;
    protected double coeffReflectionHorizontaleX;//1.0/20.0
    protected double coeffReflectionHorizontaleY;//1.0/12.5
    protected double masse;//exprimée impérativement en kg
    protected double x;//en m
    protected double y;
    protected double vitesse_x;//en m/s
    protected double vitesse_y;
    protected double acceleration_x;//en m/s²
    protected double acceleration_y;
    protected ArrayList<Force> forces;//Attention ! Les forces doivent être projetées sur un axe Y orienté vers le bas.
    protected double forceGravite;//Force associée à la gravité
    protected int terrain[][];//Pointeur vers le tableau bidimensionnel de la physique
    protected int blockSize;//Taille des blocks (5 pixels en pratique)
    protected int hitBoxHauteur;//Dimensions de la hitbox
    protected int hitBoxLargeur;
    protected int blocIntraversables[];//Tableau contenant les blocs intraversables par l'entité
    protected boolean speedLimitation;//Indique si une limitation de la vitesse de la sécurité
    protected boolean physicSecurity;//Indique si la vitesse doit être limitée afin d'assurer une physic exact au block près

    //Special rocket
    protected boolean contactDetectedThisTime;//Indique si un contact a été detecté au dernier calcul de la physique

    public MoteurPhysique(int terrain[][], int blockSize, int hitBoxHauteur,int hitBoxLargeur,int blocIntraversables[],double coeffReflectionVerticaleX,double coeffReflectionVerticaleY,double coeffReflectionHorizontaleX,double coeffReflectionHorizontaleY,double masse, int xPixel, int yPixel,boolean physicSecurity){
        //Constructeur du moteur physique
        //Il comprend beaucoup d'arguments afin de pouvoir adapter au mieux le moteur physique à chaque entité
        //(Un Worms n'interargit pas exactement de la même manière avec le décor qu'un rocket :) !)

        this.terrain = terrain;
        this.blockSize = blockSize;
        this.hitBoxHauteur = hitBoxHauteur;
        this.hitBoxLargeur = hitBoxLargeur;
        this.blocIntraversables = blocIntraversables;
        this.masse = masse;
        this.x = convertPixelToReal(xPixel);
        this.y = convertPixelToReal(yPixel);
        vitesse_x = 0;//vitesses nulles par défaut
        vitesse_y = 0;
        speedLimitation = false;
        forces = new ArrayList<Force>();
        this.coeffReflectionVerticaleX = coeffReflectionVerticaleX;
        this.coeffReflectionVerticaleY = coeffReflectionVerticaleY;
        this.coeffReflectionHorizontaleX = coeffReflectionHorizontaleX;
        this.coeffReflectionHorizontaleY = coeffReflectionHorizontaleY;
        this.physicSecurity = physicSecurity;

        //Special rocket
        contactDetectedThisTime = false;
    }

    public void applyForces(double delta){
        //Applique à l'objet l'ensemble des forces auquel il est soumis, y compris la gravité

        double newx = x;
        double newy = y;

        //Les accelerations sur les axes x et y sont égales à la somme des forces sur ces axes divisés par la masse
        //Comme l'énonce la deuxième loi de Newton
        acceleration_x = 0;
        acceleration_y = 0;
        for(Force f:forces){
            acceleration_x += f.getForceX();
            acceleration_y += f.getForceY();
        }
        acceleration_x /= masse;
        acceleration_y /= masse;

        delta *= 0.001; //Conversion des ms en s

        //Les vitesses et les accelerations sont en m/s et m/s²
        //Il faut donc les multiplier par le temps que dure la frame en s
        vitesse_x += acceleration_x * delta;
        vitesse_y += acceleration_y * delta;
        newx += vitesse_x * delta;
        newy += vitesse_y * delta;

        //Une fois le déplacement calculé, il faut vérifier que le mouvement est possible
        //et appliquer les éventuels rebonds sur les parois

        int xPixelOld = convertRealToPixel(x);
        int yPixelOld = convertRealToPixel(y);
        int xPixel = convertRealToPixel(newx);
        int yPixel = convertRealToPixel(newy);

        speedLimitation = false;
        if(physicSecurity){
            //Si l'option est activée, on limite la vitesse à un max de 5 pixels/frame
            //(Vous noterez donc que plus le nombre de fps est elevé, plus le moteur physique sera à même
            // de supporter des hautes vitesses tout en assurant une physique précise au block près)
            xPixel = limiteSpeed(xPixelOld,xPixel);
            yPixel = limiteSpeed(yPixelOld,yPixel);
        }

        //On récupère l'ensemble des blocks actuellement superposés avec le Worms
        ArrayList<Block> BlockEnContact = getContactBlock(xPixel,yPixel);

        //On cherche sur la grille des blocs les coordonnées du haut, du bas, de la gauche et de la droite de l'objet consideré
        Block blocBas = blockEquivalent(xPixel,yPixel);
        int yGrilleBas = blocBas.y;
        Block blocHaut = blockEquivalent(xPixel,yPixel-hitBoxHauteur+1);
        int yGrilleHaut = blocHaut.y;
        Block blocGauche = blockEquivalent(xPixel,yPixel);
        int xGrilleGauche = blocGauche.x;
        Block blocDroite = blockEquivalent(xPixel+hitBoxLargeur-1,yPixel);
        int xGrilleDroite = blocDroite.x;

        boolean one_change_x = true;
        boolean one_change_y = true;
        boolean physicCorrection = false;

        //Gestion des collisions (rebond à l'image de la reflexion en optique)
        for(Block bContact:BlockEnContact){
            if((bContact.y == yGrilleBas || bContact.y == yGrilleHaut)&& one_change_y){
                //Contact avec une paroi horizontale
                vitesse_x = vitesse_x*coeffReflectionHorizontaleX;
                vitesse_y = -vitesse_y*coeffReflectionHorizontaleY;
                one_change_y = false;
                if(bContact.x != xGrilleGauche && bContact.x != xGrilleDroite){
                    if(bContact.y == yGrilleBas){
                        yPixel = yGrilleBas*blockSize-1;
                        physicCorrection = true;
                    }
                    else{
                        yPixel = (yGrilleBas+1)*blockSize-1;
                        physicCorrection = true;
                    }
                }

            }
            else if((bContact.x == xGrilleGauche || bContact.x == xGrilleDroite)&& one_change_x){
                //Contact avec une paroi verticale
                vitesse_x = -vitesse_x*coeffReflectionVerticaleX;
                vitesse_y = vitesse_y*coeffReflectionVerticaleY;
                one_change_x = false;
                if(bContact.y != yGrilleBas && bContact.y != yGrilleHaut){
                    if(bContact.x == xGrilleGauche){
                        xPixel = (xGrilleGauche+1)*blockSize;
                        physicCorrection = true;
                    }
                    else{
                        xPixel = xGrilleGauche*blockSize;
                        physicCorrection = true;
                    }
                }
            }

            //Special rocket
            contactDetectedThisTime = false;
            if(!BlockEnContact.isEmpty()){
                contactDetectedThisTime = true;
            }
        }

        //On verifie que la physique n'a pas donné un résultat absurde:
        BlockEnContact = getContactBlock(xPixel,yPixel);

        if(BlockEnContact.isEmpty()){//Si tout va bien, alors on met à jour les coordonnées RÉELLES de l'objet
            if(speedLimitation || physicCorrection){
                x = convertPixelToReal(xPixel);
                y = convertPixelToReal(yPixel);
            }
            else{
                x = newx;
                y = newy;
            }
        }
    }

    public Block blockEquivalent(int xd,int yd){
        //Permet de savoir dans quel case se trouve un point donnée
        //Cette classe est fondamental pour la physique:
        //Elle permet de déplacer l'objet sur la grille réelle bien que la physique se base
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
        //--> Renvoit la liste de tous les blocks actuellement superposés
        //avec l'objet
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

    public int getPixelCoordX(){
        return convertRealToPixel(x);
    }

    public int getPixelCoordY(){
        return convertRealToPixel(y);
    }

    public double convertPixelToReal(int pixel){
        //Permet de passer de l'écran à l'espace réel
        return pixel*realPixelSize;
    }

    public int convertRealToPixel(double real){
        //Permet de passer de l'espace réel à l'écran
        return (int)(real/realPixelSize+0.000001);//L'ajout de 10^-6 sert à corriger un bug de cast de Java
    }

    public int limiteSpeed(int oldCoord,int newCoord){
        //Permet de limiter la vitesse à la taille des blocks, c'est à dire à 5 pixels/frame maximum

        if(newCoord-oldCoord>blockSize){
            newCoord = oldCoord + blockSize;
            speedLimitation = true;
        }
        else if(newCoord-oldCoord<-blockSize){
            newCoord = oldCoord - blockSize;
            speedLimitation = true;
        }
        return newCoord;
    }

    public void addForce(Force f){
        //Permet d'ajouter une nouvelle force s'appliquant à l'entité
        forces.add(f);
    }

    public void removeForce(Force f){
        //Permet de retirer une force qui était appliquée à l'entité
        forces.remove(f);
    }

    public void setVitesse_y(double vitesse_y) {
        //Permet de définir la vitesse selon l'axe y
        this.vitesse_y = vitesse_y;
    }

    public void setVitesse_x(double vitesse_x) {
        //Permet de définir la vitesse selon l'axe x
        this.vitesse_x = vitesse_x;
    }

    public void setX(int xPixel){
        //Permet de définir la coordonnée x de l'entité
        x = convertPixelToReal(xPixel);
    }

    public void setY(int yPixel){
        //Permet de définir la coordonnée y de l'entité
        y = convertPixelToReal(yPixel);
    }

    public void drawHitBox(org.newdawn.slick.Graphics g) throws SlickException{
        //Dessine la hitbox de l'entité à l'écran
        //(fonction utilisée en mode dévellopeur pour dessiner la hitbox des Worms et des projectiles)
        g.drawRect(convertRealToPixel(x),convertRealToPixel(y)-hitBoxHauteur,hitBoxLargeur,hitBoxHauteur);
    }

    public void set_vitesse_x(int xVitessePixel){
        vitesse_x = convertPixelToReal(xVitessePixel);
    }

    public void set_vitesse_y(int yVitessePixel){
        vitesse_y = convertPixelToReal(yVitessePixel);
    }

    public double getVitesse_x() {
        return vitesse_x;
    }

    public double getVitesse_y() {
        return vitesse_y;
    }

    public double getAcceleration_x(){return acceleration_x;}

    public double getAcceleration_y(){return acceleration_y;}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getXpixel() {
        //Retourne la coordonnée x en pixels
        return convertRealToPixel(x);
    }

    public double getYpixel() {
        //Retourne la coordonnée y en pixels
        return convertRealToPixel(y);
    }

    public boolean contactDetected(){
        //Indique si un contact a été detecté lors du dernier calcul de physique
        return contactDetectedThisTime;
    }

}
