import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class GrenadeProjectile extends Projectile{
    protected Sound bruitExplosion;

    public GrenadeProjectile(int terrain[][], int blockSize) throws SlickException {
        pictureLeft = new org.newdawn.slick.Image("images/grenade_essai_left_mini.png");
        pictureRight = new org.newdawn.slick.Image("images/grenade_essai_right_mini.png");
        bruitExplosion = new Sound("music/Grenade.ogg");
        alive = true;
        normeVitesse = 1600;
        hitBoxHauteur = 20;
        hitBoxLargeur = 20;
        masse = 0.01;
        rayonExplosion = 70;
        this.terrain = terrain;
        this.blockSize = blockSize;
        antiExplosion = false;
        hauteurBlock = terrain.length;
        largeurBlock = terrain[0].length;
        degat=50;
        normeSouffleExplosion = 800;

        //chronoLaunchForce = 200;
        chronoExplosion = 2000;
    }

    public void explosion(Worms[] joueurs){
        genericExplosion(x,y,rayonExplosion);
        applyDegAndPhysic(joueurs);
        bruitExplosion.play();
    }

    public void draw(org.newdawn.slick.Graphics g){
        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        pictureRight.setRotation((float)((drawAngle/Math.PI)*180.0)+40);
        pictureRight.draw(x,y-pictureRight.getHeight()+1);
    }

    public void specialPhysic(int delta){}

    public void specialInit(double pourcentagePuissance){}

}
