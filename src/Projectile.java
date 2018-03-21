import org.newdawn.slick.Sound;

public abstract class Projectile {
    protected org.newdawn.slick.Image pictureLeft;
    protected org.newdawn.slick.Image pictureRight;
    protected Sound bruitFail;
    protected int x;
    protected int y;
    protected double masse;
    protected double degat;
    protected MoteurPhysique physic;
    protected double normeVitesse;
    protected final static double g = 9.81;
    protected final static int blocIntraversables[] = {1,3};
    protected final static int blocIndestructibles[] = {2,3};
    protected boolean alive;
    protected int terrain[][];
    protected int blockSize;
    protected int hitBoxHauteur;
    protected int hitBoxLargeur;
    protected Force launchForce;
    //protected long chronoLaunchForce;
    protected long chronoExplosion;
    protected boolean antiExplosion;
    protected int hauteurBlock;
    protected int largeurBlock;
    protected double drawAngle;
    protected int rayonExplosion;
    protected double normeSouffleExplosion;
    protected Worms tireur;
    protected long launchTime;

    public void launch(Weapon lanceur,double pourcentagePuissance){
        pourcentagePuissance /= 100;
        //this.x = lanceur.getxCentreRotation();
        //this.y = lanceur.getyCentreRotation();
        this.x = tireur.getX();
        this.y = tireur.getY();
        double angle = 2;
        if(lanceur.getOrientationWorms()==0){
            angle = 180 - lanceur.getLastAngle();
        }
        else{
            angle = lanceur.getLastAngle();
        }
        angle = (angle/180.0)*Math.PI;
        //double forceX = normeLaunchForce * Math.cos(angle);
        //double forceY = normeLaunchForce * Math.sin(angle);
        physic = new MoteurPhysique(terrain,blockSize,hitBoxHauteur,hitBoxLargeur,blocIntraversables,1.0,1.0,1.0/3.0,1.0/3.0,masse,x,y);
        Force forceGravite = new Force(0,g/200);
        //launchForce = new Force(forceX,forceY);
        physic.addForce(forceGravite);
        //physic.addForce(launchForce);
        physic.set_vitesse_x((int)( pourcentagePuissance * normeVitesse* Math.cos(angle)));
        physic.set_vitesse_y((int)( pourcentagePuissance * normeVitesse* Math.sin(angle)));
        specialInit(pourcentagePuissance);
        launchTime = System.currentTimeMillis();
    }

    public void applyPhysic(int delta,Worms[] joueurs){
        physic.applyForces(delta);
        x = physic.getPixelCoordX();
        y = physic.getPixelCoordY();
        specialPhysic(delta,joueurs);
    }

    public void removeLaunchForce(){
        physic.removeForce(launchForce);
    }

    public abstract void explosion(Worms[] joueurs);

    public void genericExplosion(int xe,int ye,int rayon){
        //Explosion !!!!!!

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

        boolean destructible;
        double demi_block = blockSize/2.0;
        for(int i=block_hg_y;i<=block_bd_y;i++){
            for(int j=block_hg_x;j<=block_bd_x;j++) {
                if (distance(xe, ye, j * blockSize + demi_block, i * blockSize + demi_block) <= rayon) {
                    destructible = true;
                    for (int strong : blocIndestructibles) {
                        if (terrain[i][j] == strong) {
                            destructible = false;
                        }
                    }
                    if (destructible) {
                        if (antiExplosion) {
                            terrain[i][j] = 1;
                        } else {
                            terrain[i][j] = 0;
                        }
                    }
                }
            }
        }
    }

    //dégat worms
    public void applyDegAndPhysic(Worms[] joueurs){
        //Pauvre Worms :'( ...

        //Check worms
        double distanceWor;
        double facteurDistance;
        double degatEffectif;
        double deltaX,deltaY, angleSouffle;
        for(Worms wor:joueurs) {
            distanceWor = distance(x, y,wor.getX()+ wor.getHitBoxLargeur()/2,wor.getY() - wor.getHitBoxHauteur()/2);
            if (distanceWor <= rayonExplosion) {
                facteurDistance = (1 - distanceWor/rayonExplosion);
                //Degats
                degatEffectif = facteurDistance * degat;
                wor.modifierVie(-degatEffectif);
                System.out.println(degatEffectif);

                //Worms projeté par le souffle de l'explosion
                deltaX = wor.getX()+ wor.getHitBoxLargeur()/2 - x;
                deltaY = wor.getY() - wor.getHitBoxHauteur()/2 - y;
                angleSouffle = Math.atan2(deltaY,deltaX);
                wor.set_vitesse_x((int)(Math.cos(angleSouffle)*facteurDistance*normeSouffleExplosion));
                wor.set_vitesse_y((int)(Math.sin(angleSouffle)*facteurDistance*normeSouffleExplosion));

                //Fail noise
                if(wor == tireur){
                    bruitFail.play();
                }
            }
        }

    }


    public double distance(double x1,double y1,double x2,double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
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

    public long getChronoExplosion() {
        return chronoExplosion;
    }

    public abstract void draw(org.newdawn.slick.Graphics g);

    public abstract void specialPhysic(int delta,Worms[] joueurs);

    public abstract void specialInit(double pourcentagePuissance);

    public boolean isAlive() {
        return alive;
    }

    public int getx() {
        return x;
    }

    public int gety() {
        return y;
    }

}
