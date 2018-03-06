import org.newdawn.slick.*;

public abstract class Weapon {
    protected int dmgBasic;
    protected int x;
    protected int y;
    protected org.newdawn.slick.Image pictureLeft;
    protected org.newdawn.slick.Image pictureRight;

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

    //public abstract void exploser(int x,int y);

    public void init(int xw,int yw,int hitBoxLargeur,int hitBoxHauteur,int orientation){
        orientationWorms = orientation;
        if(orientationWorms==0){
            /*x = xw - hitBoxLargeur/3;
            y = yw - hitBoxHauteur/2 - pictureLeft.getHeight()/4;
            demiDiago = Math.sqrt(Math.pow(pictureLeft.getWidth(),2)+Math.pow(pictureLeft.getHeight(),2))/2.0;*/
            x = xw - hitBoxLargeur/3;
            y = yw - hitBoxHauteur/2 - pictureLeft.getHeight()/4;
            demiDiago = pictureLeft.getWidth()/2.0;
        }
        else
        {
            /*x = xw + hitBoxLargeur/6;
            y = yw - hitBoxHauteur/2 - pictureRight.getHeight()/4;
            demiDiago = Math.sqrt(Math.pow(pictureRight.getWidth(),2)+Math.pow(pictureRight.getHeight(),2))/2.0;*/
            x = xw + hitBoxLargeur/6;
            y = yw - hitBoxHauteur/2 - pictureRight.getHeight()/4;
            demiDiago = pictureRight.getWidth()/2.0;
        }
    }

    public void drawVisee(double angle){
        if(orientationWorms==0){
            xCentre = x + pictureLeft.getWidth()/2;
            yCentre = y + pictureLeft.getHeight()/2;
            /*xCoinDroite = (int)(demiDiago * Math.cos((-angle/180.0)*Math.PI+coinDroiteOffset+(offSetLeft/180.0)*Math.PI) + xCentre);
            yCoinDroite = (int)(demiDiago * Math.sin((-angle/180.0)*Math.PI+coinDroiteOffset+(offSetLeft/180.0)*Math.PI) + yCentre);
            xTemp = 2*x + pictureLeft.getWidth() - xCoinDroite;
            yTemp = 2*y + pictureLeft.getHeight() - yCoinDroite;
            pictureLeft.setRotation((float)(-angle+offSetLeft));
            pictureLeft.draw(xTemp,yTemp);*/
            xCoinDroite = (int)(demiDiago * Math.cos((-angle/180.0)*Math.PI+coinDroiteOffset) + xCentre);
            yCoinDroite = (int)(demiDiago * Math.sin((-angle/180.0)*Math.PI+coinDroiteOffset) + yCentre);
            xTemp = 2*x + pictureLeft.getWidth() - xCoinDroite;
            yTemp = 2*y + pictureLeft.getHeight()/2 - yCoinDroite;
            pictureLeft.setRotation((float)(-angle+offSetLeft));
            pictureLeft.draw(xTemp,yTemp);
        }
        else{
            xCentre = x + pictureRight.getWidth()/2;
            yCentre = y + pictureRight.getHeight()/2;
            /*xCoinGauche = (int)(demiDiago * Math.cos((angle/180.0)*Math.PI+coinGaucheOffset+(offSetRight/180.0)*Math.PI) + xCentre);
            yCoinGauche = (int)(demiDiago * Math.sin((angle/180.0)*Math.PI+coinGaucheOffset+(offSetRight/180.0)*Math.PI) + yCentre);
            xTemp = 2*x - xCoinGauche;
            yTemp = 2*y + pictureRight.getHeight() - yCoinGauche;
            pictureRight.setRotation((float)(angle+offSetRight));
            pictureRight.draw(xTemp,yTemp);*/
            xCoinGauche = (int)(demiDiago * Math.cos((angle/180.0)*Math.PI+coinGaucheOffset) + xCentre);
            yCoinGauche = (int)(demiDiago * Math.sin((angle/180.0)*Math.PI+coinGaucheOffset) + yCentre);
            xTemp = 2*x - xCoinGauche;
            yTemp = 2*y + pictureRight.getHeight()/2 - yCoinGauche;
            pictureRight.setRotation((float)(angle+offSetRight));
            pictureRight.draw(xTemp,yTemp);
        }
    }
}

//System.out.println("angle="+angle);
//System.out.println("demiDiago="+demiDiago);
//System.out.println("xCentre="+xCentre);
//System.out.println("yCentre="+yCentre);
//System.out.println("xCoinGauche="+xCoinGauche);
//System.out.println("yCoinGauche="+yCoinGauche);
//System.out.println("diffX="+(x - xCoinGauche));
//System.out.println("diffY="+(y + pictureRight.getHeight() - yCoinGauche));
