import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public abstract class Projectile {
    protected final static Sound bruitFail_src = createSound("music/failSound.ogg");
    protected org.newdawn.slick.Image pictureLeft;//Image du projectile orienté vers la gauche
    protected org.newdawn.slick.Image pictureRight;//Image du projectile orienté vers la droite
    protected Sound bruitFail;//Son joué lorsque le joueur est touché par son propre projectile
    protected int x;//Coordonnées du projectile
    protected int y;
    protected double masse;
    protected double degat;
    protected MoteurPhysique physic;//Moteur physique dedié au projectile
    protected double normeVitesse;//norme de la vitesse au lancement
    protected final static double g = 9.81;//intensité de pesanteur
    protected final static int blocIntraversables[] = {1,3};//Blocks intraversables (décor+limites intraversables)
    protected final static int blocIndestructibles[] = {2,3};//Blocks indestructibles (eau+limites invisibles)
    protected boolean alive;//Indique si le projectile est en vie ou non
    protected int terrain[][];//pointeur vers le tableau bidimensionnel de la physique
    protected int blockSize;//taille des blocks (5 pixels)
    protected int hitBoxHauteur;//Dimensions de la hitbox du projectile
    protected int hitBoxLargeur;
    protected Force launchForce;
    //protected long chronoLaunchForce;
    protected long chronoExplosion;//Temps que met l'arme pour exploser d'elle même
    protected boolean antiExplosion;
    protected int hauteurBlock;//Hauteur du terrain en block
    protected int largeurBlock;//Largeur du terrain en block
    protected double drawAngle;//Angle actuelle décrit par la trajectoire du projectile
    protected int rayonExplosion;//Rayon de l'explosion du projectile
    protected double normeSouffleExplosion;//Norme du souffle generée par l'explosion (que subissent les Worms à proximité)
    protected Worms tireur;//Worms ayant tiré le projectile
    protected long launchTime;//Instant ou le projectile a été tiré

    public void launch(Weapon lanceur,double pourcentagePuissance,boolean physicSecurity){
        //Permet le lancement du projectile

        pourcentagePuissance /= 100;//transposition du pourcentage sur l'intervalle [0;1]
        this.x = tireur.getX() + tireur.hitBoxLargeur/2;//Le projectile part arbitrairement du centre du Worms
        this.y = tireur.getY() - tireur.hitBoxHauteur/2;//Permet d'éviter principalement que les rockets explosent au moment du lancement
                                                        //lorsque le Worms est près d'un mur
        double angle = 0;//2
        //On récupere le dernier angle de la phase de visée:
        if(lanceur.getOrientationWorms()==0){
            angle = 180 - lanceur.getLastAngle();
        }
        else{
            angle = lanceur.getLastAngle();
        }
        angle = (angle/180.0)*Math.PI;//On le convertit en radian
        //double forceX = normeLaunchForce * Math.cos(angle);
        //double forceY = normeLaunchForce * Math.sin(angle);

        //On crée le moteur physique dedié à la trajectoire du projectile:
        physic = new MoteurPhysique(terrain,blockSize,hitBoxHauteur,hitBoxLargeur,blocIntraversables,1.0,1.0,1.0/3.0,1.0/3.0,masse,x,y,physicSecurity);
        //Le projectile est soumis à la gravité:
        Force forceGravite = new Force(0,g/200);
        physic.addForce(forceGravite);

        //launchForce = new Force(forceX,forceY);
        //physic.addForce(launchForce);
        //On donne au projectile une vitesse initiale
        physic.setVitesse_x(pourcentagePuissance * normeVitesse* Math.cos(angle));
        physic.setVitesse_y(pourcentagePuissance * normeVitesse* Math.sin(angle));
        //Initialisation spécifique à chaque arme:
        specialInit(pourcentagePuissance);
        //On enregistre l'instant du tir
        launchTime = System.currentTimeMillis();
    }

    public void applyPhysic(int delta,Worms[] joueurs){
        //Applique la physique au projectile

        physic.applyForces(delta);
        x = physic.getPixelCoordX();
        y = physic.getPixelCoordY();
        specialPhysic(delta,joueurs);
    }

    public void removeLaunchForce(){
        //Permet de retirer une force s'appliquant au projectile
        physic.removeForce(launchForce);
    }

    //Fait exploser le projectile:
    public abstract void explosion(Worms[] joueurs);

    public void genericExplosion(int xe,int ye,int rayon){
        //Explosion !!!!!!
        //Genere une explosion à l'emplacement et avec le rayon en paramètre

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
        //Applique les dégâts et le souffle aux Worms

        //Check worms
        double distanceWor;
        double facteurDistance;
        double degatEffectif;
        double deltaX,deltaY, angleSouffle;
        for(Worms wor:joueurs) {
            distanceWor = distance(x, y,wor.getX()+ wor.getHitBoxLargeur()/2,wor.getY() - wor.getHitBoxHauteur()/2);
            if (distanceWor <= rayonExplosion) {
                //Si le Worms est dans le rayon de l'explosion
                facteurDistance = (1 - distanceWor/rayonExplosion);
                //On lui applique des dégâts d'autant plus grands qu'il est proche du centre de l'explosion
                degatEffectif = facteurDistance * degat;
                wor.modifierVie(-degatEffectif);
                //System.out.println(degatEffectif);

                //Worms projeté par le souffle de l'explosion
                deltaX = wor.getX()+ wor.getHitBoxLargeur()/2 - x;
                deltaY = wor.getY() - wor.getHitBoxHauteur()/2 - y;
                angleSouffle = Math.atan2(deltaY,deltaX);
                wor.set_vitesse_x((int)(Math.cos(angleSouffle)*facteurDistance*normeSouffleExplosion));
                wor.set_vitesse_y((int)(Math.sin(angleSouffle)*facteurDistance*normeSouffleExplosion));

                //Fail noise
                if(wor == tireur){
                    //Si le Worms est touché par son propre projectile, un joue un son pour se moquer de lui
                    bruitFail.play();
                }
            }
        }

    }


    public double distance(double x1,double y1,double x2,double y2){
        //Retourne la distance entre 2 points
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

    //Dessine le projectile à l'écran:
    public abstract void draw(org.newdawn.slick.Graphics g);

    public abstract void specialPhysic(int delta,Worms[] joueurs);

    public abstract void specialInit(double pourcentagePuissance);

    public abstract void specialLaunch(Weapon lanceur,double pourcentagePuissance);

    //Indique si le projectile est vivant:
    public boolean isAlive() {
        return alive;
    }

    public int getx() {
        //Retourne la coordonnée X du projectile
        return x;
    }

    public int gety() {
        //Retourne la coordonnée Y du projectile
        return y;
    }

    protected static Sound createSound(final String adresse) {
        try {
            return new Sound(adresse);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static org.newdawn.slick.Image createImage(final String adresse) {
        try {
            return new org.newdawn.slick.Image(adresse);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        return null;
    }

}
