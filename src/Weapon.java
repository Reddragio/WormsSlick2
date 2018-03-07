import org.newdawn.slick.*;

public abstract class Weapon {
    protected int dmgBasic;
    protected int x;
    protected int y;
    protected org.newdawn.slick.Image pictureLeft;
    protected org.newdawn.slick.Image pictureRight;
    protected org.newdawn.slick.Image viseur;
    protected org.newdawn.slick.Image conePuissance;
    protected final static int distanceWormsViseur = 60;

    //Dessin visée
    protected double demiDiago;
    protected int xCentre;
    protected int yCentre;
    protected int xCoinGauche;
    protected int yCoinGauche;
    protected int xCoinDroite;
    protected int yCoinDroite;
    protected int xTemp;
    protected int yTemp;
    protected int offSetLeft;
    protected int offSetRight;
    protected int orientationWorms;
    protected double coinGaucheOffset;
    protected double coinDroiteOffset;
    protected int xCentreRotation;
    protected int yCentreRotation;
    protected int xViseur;
    protected int yViseur;
    protected double lastAngle;
    protected int xCone;
    protected int yCone;

    //public abstract void exploser(int x,int y);

    public void init(int xw,int yw,int hitBoxLargeur,int hitBoxHauteur,int orientation){
        orientationWorms = orientation;
        if(orientationWorms==0){
            xCentreRotation = xw + 2*hitBoxLargeur/3;
            x = xCentreRotation - pictureLeft.getWidth();
            yCentreRotation = yw - hitBoxHauteur/4;
            y = yCentreRotation - pictureLeft.getHeight()/2;
            demiDiago = pictureLeft.getWidth()/2.0;
        }
        else
        {
            xCentreRotation = xw + hitBoxLargeur/3;
            x = xCentreRotation;
            yCentreRotation = yw - hitBoxHauteur/4;
            y = yCentreRotation - pictureRight.getHeight()/2;
            demiDiago = pictureRight.getWidth()/2.0;
        }
    }

    public void drawVisee(double angle){
        if(orientationWorms==0){
            xCentre = x + pictureLeft.getWidth()/2;
            yCentre = y + pictureLeft.getHeight()/2;
            xCoinDroite = (int)(demiDiago * Math.cos((-angle/180.0)*Math.PI+coinDroiteOffset) + xCentre);
            yCoinDroite = (int)(demiDiago * Math.sin((-angle/180.0)*Math.PI+coinDroiteOffset) + yCentre);
            xTemp = 2*x + pictureLeft.getWidth() - xCoinDroite;
            yTemp = 2*y + pictureLeft.getHeight()/2 - yCoinDroite;
            pictureLeft.setRotation((float)(-angle+offSetLeft));
            pictureLeft.draw(xTemp,yTemp);
            xViseur = (int)(distanceWormsViseur * Math.cos((-angle/180.0)*Math.PI + Math.PI) - viseur.getWidth()/2 + xCentreRotation);
            yViseur = (int)(distanceWormsViseur * Math.sin((-angle/180.0)*Math.PI + Math.PI) - viseur.getHeight()/2 + yCentreRotation);
            viseur.setRotation((float)(-2*angle));
        }
        else{
            xCentre = x + pictureRight.getWidth()/2;
            yCentre = y + pictureRight.getHeight()/2;
            xCoinGauche = (int)(demiDiago * Math.cos((angle/180.0)*Math.PI+coinGaucheOffset) + xCentre);
            yCoinGauche = (int)(demiDiago * Math.sin((angle/180.0)*Math.PI+coinGaucheOffset) + yCentre);
            xTemp = 2*x - xCoinGauche;
            yTemp = 2*y + pictureRight.getHeight()/2 - yCoinGauche;
            pictureRight.setRotation((float)(angle+offSetRight));
            pictureRight.draw(xTemp,yTemp);
            xViseur = (int)(distanceWormsViseur * Math.cos((angle/180.0)*Math.PI) - viseur.getWidth()/2 + xCentreRotation);
            yViseur = (int)(distanceWormsViseur * Math.sin((angle/180.0)*Math.PI) - viseur.getHeight()/2 + yCentreRotation);
            viseur.setRotation((float)(2*angle));
        }
        viseur.draw(xViseur,yViseur);
        lastAngle = angle;
    }

    public void drawConePuissance(double pourcentage){
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

    public abstract Projectile generateProjectile(int terrain[][],int blockSizes) throws SlickException;

}
