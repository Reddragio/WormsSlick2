import org.newdawn.slick.*;

public class Grenade extends Weapon {

    public Grenade(int nombre) throws SlickException {
        this.pictureLeft = new org.newdawn.slick.Image("images/grenade_essai_left_mini.png");
        this.pictureRight = new org.newdawn.slick.Image("images/grenade_essai_right_mini.png");
        this.viseur = new org.newdawn.slick.Image("images/croix_visee_mini.png");
        this.conePuissance = new org.newdawn.slick.Image("images/cone_puissance_mini.png");
        this.pictureHD = new org.newdawn.slick.Image("images/grenade_essai_right.png");
        offSetLeft = -40;
        offSetRight = 40;
        /*coinGaucheOffset = Math.PI/2.0+Math.atan(pictureRight.getWidth()/pictureRight.getHeight());
        coinDroiteOffset = Math.atan(pictureLeft.getHeight()/pictureLeft.getWidth());*/
        coinGaucheOffset = Math.PI;
        coinDroiteOffset = 0;
        facteurDrawOffsetLeft = 2.0/3.0;
        facteurDrawOffsetRight = 1.0/3.0;
        nombrePossede = nombre;
    }

    public Projectile generateProjectile(int terrain[][],int blockSize) throws SlickException {
        return new GrenadeProjectile(terrain,blockSize);
    }

}
