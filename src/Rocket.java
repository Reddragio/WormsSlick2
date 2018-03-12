import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Rocket extends Projectile {
    protected int rayonExplosion;
    protected Sound bruitExplosion;
    protected Sound bruitTir;
    protected double normePropulsion;
    protected Force propulsion;
    protected int accumulateurPropulsion;
    protected double facteurPropulsion;

    public Rocket(int terrain[][], int blockSize) throws SlickException {
        pictureLeft = new org.newdawn.slick.Image("images/rocket_left.png");
        pictureRight = new org.newdawn.slick.Image("images/rocket_right.png");
        bruitExplosion = new Sound("music/rocket_explosion.ogg");
        bruitTir = new Sound("music/bazooka_tir_2.ogg");
        alive = true;
        normeVitesse = 2000;
        normePropulsion = (g/200.0);
        hitBoxHauteur = 20;
        hitBoxLargeur = 40;
        masse = 0.01;
        rayonExplosion = 70;
        this.terrain = terrain;
        this.blockSize = blockSize;
        antiExplosion = false;
        hauteurBlock = terrain.length;
        largeurBlock = terrain[0].length;
        propulsion = new Force(42,42);
        accumulateurPropulsion = 1;
        degat=-75;

        //chronoLaunchForce = 200;
        chronoExplosion = 10000;
    }

    public void explosion(Worms[] joueurs){
        applyDeg(joueurs,rayonExplosion,degat);
        bruitExplosion.play();
        bruitTir.stop();
        bruitExplosion.play();
    }

    public void draw(org.newdawn.slick.Graphics g){
        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        pictureRight.setRotation((float)((drawAngle/Math.PI)*180.0));
        pictureRight.draw(x,y-pictureRight.getHeight()+1);
    }

    public void specialPhysic(int delta){
        if(physic.contactDetected()){
            alive = false;
        }
        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        physic.removeForce(propulsion);
        facteurPropulsion = 1 - accumulateurPropulsion/800;
        if(facteurPropulsion<0){
            facteurPropulsion = 0;
        }
        propulsion = new Force( facteurPropulsion*normePropulsion*Math.cos(drawAngle), -(g/200.0)*(9.0/10.0)+facteurPropulsion*normePropulsion*Math.sin(drawAngle));
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

}
