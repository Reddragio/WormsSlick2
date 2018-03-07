import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class GrenadeProjectile extends Projectile{
    protected int rayonExplosion;
    protected Sound bruitExplosion;

    public GrenadeProjectile(int terrain[][], int blockSize) throws SlickException {
        this.pictureLeft = new org.newdawn.slick.Image("images/grenade_essai_left_mini.png");
        this.pictureRight = new org.newdawn.slick.Image("images/grenade_essai_right_mini.png");
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

        chronoLaunchForce = 200;
        chronoExplosion = 2000;
    }

    public void explosion(){
        experimentalExplosion(x,y,rayonExplosion);
        bruitExplosion.play();
    }

    public void draw(org.newdawn.slick.Graphics g){
        pictureRight.draw(x,y-pictureRight.getHeight()+1);
    }

}
