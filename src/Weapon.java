import org.newdawn.slick.*;

public abstract class Weapon {
    protected int dmgBasic;
    protected int x;//Coordonnées de l'arme
    protected int y;
    protected org.newdawn.slick.Image pictureLeft;//Image gauche de l'arme
    protected org.newdawn.slick.Image pictureRight;//Image droite de l'arme
    protected org.newdawn.slick.Image pictureHD;//Image Haute définition de l'arme (pour l'inventaire)
    protected org.newdawn.slick.Image viseur;//Image du viseur
    protected org.newdawn.slick.Image conePuissance;//Image du cone de puissance
    protected final static int distanceWormsViseur = 60;//Distance entre le Worms et le viseur

    //Dessin visée
    //Ensemble de variables servant au dessin de la visée
    //Toute la difficulté du dessin de la visée repose dans la rotation de l'image de l'arme
    //En effet, la rotation de slick2d ne permet de faire tourner l'arme qu'uniquement autour de son centre propre
    //Il a donc été necessaire de faire un peu de trigonometrie pour donner l'illusion que le centre de rotation
    //se trouve grosso modo au niveau du centre du Worms
    protected double demiDiago;
    protected int xCentre;//Coordonnées du centre de l'image
    protected int yCentre;
    protected int xCoinGauche;//Coordonnées du coin gauche de l'image
    protected int yCoinGauche;
    protected int xCoinDroite;//Coordonnées du coin à droite de l'image
    protected int yCoinDroite;
    protected int xTemp;
    protected int yTemp;
    protected int offSetLeft;//Facteurs de correction graphique
    protected int offSetRight;
    protected int orientationWorms;//Orientation du Worms
    protected double coinGaucheOffset;
    protected double coinDroiteOffset;
    protected int xCentreRotation;//Coordonnées du centre de rotation lors de la visée
    protected int yCentreRotation;
    protected int xViseur;//Coordonnées du viseur
    protected int yViseur;
    protected double lastAngle;//Sauvegarde du dernière angle de visée
    protected int xCone;//Coordonnées du cone de puissance
    protected int yCone;
    protected double facteurDrawOffsetLeft;
    protected double facteurDrawOffsetRight;

    //Gestion inventaire
    protected int nombrePossede;//Indique la quantité de l'arme que possède le Worms

    //public abstract void exploser(int x,int y);

    public void init(int xw,int yw,int hitBoxLargeur,int hitBoxHauteur,int orientation){
        //Initialisation de la visée avec l'arme

        orientationWorms = orientation;//On récupère l'orientation du Worms

        //On définit les coordonnées du centre de rotation et de l'image selon l'orientation:
        if(orientationWorms==0){
            //On définit les coordonnées du centre de rotation et de l'image
            xCentreRotation = xw + (int)(facteurDrawOffsetLeft*hitBoxLargeur);
            x = xCentreRotation - pictureLeft.getWidth();
            yCentreRotation = yw - hitBoxHauteur/4;
            y = yCentreRotation - pictureLeft.getHeight()/2;
            demiDiago = pictureLeft.getWidth()/2.0;
        }
        else
        {
            xCentreRotation = xw + (int)(facteurDrawOffsetRight*hitBoxLargeur);
            x = xCentreRotation;
            yCentreRotation = yw - hitBoxHauteur/4;
            y = yCentreRotation - pictureRight.getHeight()/2;
            demiDiago = pictureRight.getWidth()/2.0;
        }
    }

    public void drawVisee(double angle){
        //Permet de dessiner la visée

        if(orientationWorms==0){
            //Si le Worms est tourné vers la gauche
            //Calcul des coordonnées du centre et du coin droit, pour permettre le déplacement du centre de rotation effectif
            xCentre = x + pictureLeft.getWidth()/2;
            yCentre = y + pictureLeft.getHeight()/2;
            xCoinDroite = (int)(demiDiago * Math.cos((-angle/180.0)*Math.PI+coinDroiteOffset) + xCentre);
            yCoinDroite = (int)(demiDiago * Math.sin((-angle/180.0)*Math.PI+coinDroiteOffset) + yCentre);
            xTemp = 2*x + pictureLeft.getWidth() - xCoinDroite;
            yTemp = 2*y + pictureLeft.getHeight()/2 - yCoinDroite;
            //On applique la rotation d'après l'angle de visée et on dessine à l'écran
            pictureLeft.setRotation((float)(-angle+offSetLeft));
            pictureLeft.draw(xTemp,yTemp);
            //On calcule les coordonnées du viseur
            xViseur = (int)(distanceWormsViseur * Math.cos((-angle/180.0)*Math.PI + Math.PI) - viseur.getWidth()/2 + xCentreRotation);
            yViseur = (int)(distanceWormsViseur * Math.sin((-angle/180.0)*Math.PI + Math.PI) - viseur.getHeight()/2 + yCentreRotation);
            viseur.setRotation((float)(-2*angle));
        }
        else{
            //Si le Worms est tourné vers la droite
            //Calcul des coordonnées du centre et du coin gauche, pour permettre le déplacement du centre de rotation effectif
            xCentre = x + pictureRight.getWidth()/2;
            yCentre = y + pictureRight.getHeight()/2;
            xCoinGauche = (int)(demiDiago * Math.cos((angle/180.0)*Math.PI+coinGaucheOffset) + xCentre);
            yCoinGauche = (int)(demiDiago * Math.sin((angle/180.0)*Math.PI+coinGaucheOffset) + yCentre);
            xTemp = 2*x - xCoinGauche;
            yTemp = 2*y + pictureRight.getHeight()/2 - yCoinGauche;
            //On applique la rotation d'après l'angle de visée et on dessine à l'écran
            pictureRight.setRotation((float)(angle+offSetRight));
            pictureRight.draw(xTemp,yTemp);
            //On calcule les coordonnées du viseur
            xViseur = (int)(distanceWormsViseur * Math.cos((angle/180.0)*Math.PI) - viseur.getWidth()/2 + xCentreRotation);
            yViseur = (int)(distanceWormsViseur * Math.sin((angle/180.0)*Math.PI) - viseur.getHeight()/2 + yCentreRotation);
            viseur.setRotation((float)(2*angle));
        }
        //On dessine le viseur
        viseur.draw(xViseur,yViseur);
        lastAngle = angle;
    }

    public void drawConePuissance(double pourcentage){
        //Permet de dessiner le cone de puissance selon le pourcentage de puissance actuel

        pourcentage /= 100;
        if(orientationWorms==0){
            xCone = (int)(4.0/5.0 * distanceWormsViseur * Math.cos((-lastAngle/180.0)*Math.PI + Math.PI) - conePuissance.getWidth()/2 + xCentreRotation);
            yCone = (int)(4.0/5.0 * distanceWormsViseur * Math.sin((-lastAngle/180.0)*Math.PI + Math.PI) - conePuissance.getHeight()/2 + yCentreRotation);
            conePuissance.setRotation((float)(-lastAngle+180));
        }
        else{
            xCone = (int)(4.0/5.0 * distanceWormsViseur * Math.cos((lastAngle/180.0)*Math.PI) - conePuissance.getWidth()/2 + xCentreRotation);
            yCone = (int)(4.0/5.0 * distanceWormsViseur * Math.sin((lastAngle/180.0)*Math.PI) - conePuissance.getHeight()/2 + yCentreRotation);
            conePuissance.setRotation((float)lastAngle);
        }
        conePuissance.draw(xCone,yCone,xCone+(int)(pourcentage*conePuissance.getWidth()),yCone+conePuissance.getHeight(),0,0,(int)(pourcentage*conePuissance.getWidth()),conePuissance.getHeight());
    }

    public int getxCentreRotation() {
        return xCentreRotation;
    }

    public int getyCentreRotation() {
        return yCentreRotation;
    }

    public int getOrientationWorms() {
        return orientationWorms;
    }

    public double getLastAngle() {
        return lastAngle;
    }

    public int getNombrePossede() {
        //Retourne la quantité de l'arme possedée par le Worms
        return nombrePossede;
    }

    public void decreasePossede(){
        //Permet de diminuer de 1 la quantité possedée
        nombrePossede--;
    }

    public void draw(int x,int y,float scale){
        //Permet de dessiner l'arme dans l'inventaire
        //avec un éventuel facteur d'échelle (pour que l'arme n'apparaisse ni trop grosse ni trop petite)
        pictureHD.draw(x,y,scale);
    }

    //Fonction permettant de generer le projectile associée à l'arme:
    //(une rocket pour un bazooka, une GrenadeProjectile pour une Grenade, etc...)
    public abstract Projectile generateProjectile(int terrain[][], int blockSizes,Worms tireur) throws SlickException;

}
