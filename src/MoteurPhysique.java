import java.util.ArrayList;

public class MoteurPhysique {
    //Moteur physique du jeu
    protected final static double realBlockSize = 0.0125;
    protected final static double realPixelSize = 0.0025;
    protected final static double facteurEchelle = 0.05;
    protected final static double g = 9.81;
    protected final static double coeffReflectionVerticaleX = 1.0/12.5;
    protected final static double coeffReflectionVerticaleY = 1.0/20.0;
    protected final static double coeffReflectionHorizontaleX = 1.0/20.0;
    protected final static double coeffReflectionHorizontaleY = 1.0/12.5;
    protected double masse;//exprimée impérativement en kg
    protected double x;//en m
    protected double y;
    protected double vitesse_x;//en m/s
    protected double vitesse_y;
    protected double acceleration_x;//en m/s²
    protected double acceleration_y;
    protected ArrayList<Force> forces;//Attention ! Les forces doivent être projetées sur un axe Y orienté vers le bas.
    protected double forceGravite;
    protected int terrain[][];
    protected int blockSize;
    protected int hitBoxHauteur;
    protected int hitBoxLargeur;
    protected int blocIntraversables[];
    protected boolean speedLimitation;

    public MoteurPhysique(int terrain[][], int blockSize, int hitBoxHauteur,int hitBoxLargeur,int blocIntraversables[],double masse, int xPixel, int yPixel){
        this.terrain = terrain;
        this.blockSize = blockSize;
        this.hitBoxHauteur = hitBoxHauteur;
        this.hitBoxLargeur = hitBoxLargeur;
        this.blocIntraversables = blocIntraversables;
        this.masse = masse;
        this.x = convertPixelToReal(xPixel);
        this.y = convertPixelToReal(yPixel);
        vitesse_x = 0;
        vitesse_y = 0;
        speedLimitation = false;
        forces = new ArrayList<Force>();
    }

    public void applyForces(double delta){
        //Applique à l'objet l'ensemble des forces auquel il est soumis, y compris la gravité

        double newx = x;
        double newy = y;

        acceleration_x = 0;
        acceleration_y = 0;
        for(Force f:forces){
            acceleration_x += f.getForceX();
            acceleration_y += f.getForceY();
        }
        acceleration_x /= masse;
        acceleration_y /= masse;

        delta *= 0.001; //Conversion des ms en s

        vitesse_x += acceleration_x * delta;
        vitesse_y += acceleration_y * delta;
        newx += vitesse_x * delta;
        newy += vitesse_y * delta;
        //System.out.println(vitesse_x);
        //System.out.println(vitesse_y);

        //Une fois le déplacement calculé, il faut vérifier que le mouvement est possible
        //et appliquer les éventuels rebonds sur les parois

        int xPixelOld = convertRealToPixel(x);
        int yPixelOld = convertRealToPixel(y);
        int xPixel = convertRealToPixel(newx);
        int yPixel = convertRealToPixel(newy);
        speedLimitation = false;
        xPixel = limiteSpeed(xPixelOld,xPixel);
        yPixel = limiteSpeed(yPixelOld,yPixel);

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
        return pixel*realPixelSize;
    }

    public int convertRealToPixel(double real){
        return (int)(real/realPixelSize+0.000001);//L'ajout de 10^-6 sert à corriger un bug de cast de Java
    }

    public int limiteSpeed(int oldCoord,int newCoord){
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
        forces.add(f);
    }

    public void setVitesse_y(double vitesse_y) {
        this.vitesse_y = vitesse_y;
    }

    public void setVitesse_x(double vitesse_x) {
        this.vitesse_x = vitesse_x;
    }

    public void setX(int xPixel){
        x = convertPixelToReal(xPixel);
        //vitesse_x = 0;
        //vitesse_y = 0;
    }

    public void setY(int yPixel){
        y = convertPixelToReal(yPixel);
        //vitesse_x = 0;
        //vitesse_y = 0;
    }

    public void set_vitesse_X(int xVitessePixel){
        vitesse_x = convertPixelToReal(xVitessePixel);
    }

    public void set_vitesse_y(int yVitessePixel){
        vitesse_y = convertPixelToReal(yVitessePixel);
    }

    public double getVitesse_x() {
        return vitesse_x;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
