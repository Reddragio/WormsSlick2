import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Rocket extends Projectile {
    protected final static org.newdawn.slick.Image pictureLeft_src = createImage("images/rocket_left.png");
    protected final static org.newdawn.slick.Image pictureRight_src = createImage("images/rocket_right.png");
    protected final static Sound bruitExplosion_src = createSound("music/rocket_explosion.ogg");
    protected final static Sound bruitTir_src = createSound("music/bazooka_tir_2.ogg");
    protected Sound bruitExplosion;
    protected Sound bruitTir;
    protected double normePropulsion;
    protected Force propulsion;
    protected int accumulateurPropulsion;
    protected double facteurPropulsion;
    protected boolean wormsTouche;

    public Rocket(int terrain[][], int blockSize,Worms tireur) throws SlickException {
        pictureLeft = pictureLeft_src;
        pictureRight = pictureRight_src;
        bruitExplosion = bruitExplosion_src;
        bruitTir =  bruitTir_src;
        bruitFail = bruitFail_src;
        alive = true;
        normeVitesse = (1.0/2.0)*2000*0.0025;
        normePropulsion = (3.0/4.0)*(g/200.0);
        hitBoxHauteur = 5;
        hitBoxLargeur = 5;
        masse = 0.01;
        rayonExplosion = 70;
        this.terrain = terrain;
        this.blockSize = blockSize;
        antiExplosion = false;
        hauteurBlock = terrain.length;
        largeurBlock = terrain[0].length;
        propulsion = new Force(42,42);
        accumulateurPropulsion = 1;
        degat=75;
        normeSouffleExplosion = 900;
        this.tireur = tireur;

        //chronoLaunchForce = 200;
        chronoExplosion = 10000;
    }

    public void explosion(Worms[] joueurs){
        genericExplosion(x,y,rayonExplosion);
        applyDegAndPhysic(joueurs);
        bruitExplosion.play();
        bruitTir.stop();
        bruitExplosion.play();
    }

    public void draw(org.newdawn.slick.Graphics g){
        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        pictureRight.setRotation((float)((drawAngle/Math.PI)*180.0));
        pictureRight.draw(x,y-pictureRight.getHeight()+1);
    }

    public void specialPhysic(int delta,Worms[] joueurs){
        wormsTouche = false;
        for(Worms wor:joueurs){
            if(wor.inside(x,y) && wor!=tireur){
                wormsTouche = true;
            }
        }

        if(physic.contactDetected() || wormsTouche){
            //&& (System.currentTimeMillis()-launchTime) >= 2000
            alive = false;
        }
        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        physic.removeForce(propulsion);
        facteurPropulsion = 1 - accumulateurPropulsion/800;
        if(facteurPropulsion<0){
            facteurPropulsion = 0;
        }
        propulsion = new Force( facteurPropulsion*normePropulsion*Math.cos(drawAngle), -(g/200.0)*(7.0/10.0)+facteurPropulsion*normePropulsion*Math.sin(drawAngle));
        physic.addForce(propulsion);
        accumulateurPropulsion+= delta;
    }

    public void specialInit(double pourcentagePuissance){
        if(pourcentagePuissance<30){
            pourcentagePuissance = 30;
        }
        normePropulsion*= pourcentagePuissance/100.0;
        bruitTir.play();
    }

    public void specialLaunch(Weapon lanceur,double pourcentagePuissance){
        launch(lanceur,pourcentagePuissance,false);
    }

}
