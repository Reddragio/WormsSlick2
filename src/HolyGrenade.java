// holyGrenade visée
import org.newdawn.slick.SlickException;

public class HolyGrenade extends Grenade{

    public HolyGrenade(int nombre) throws SlickException {
        //AJOUTER IMAGE HOLY GRENADE
        super(nombre);
        this.pictureLeft = new org.newdawn.slick.Image("images/grenade_essai_left_mini.png");
        this.pictureRight = new org.newdawn.slick.Image("images/grenade_essai_right_mini.png");
        this.viseur = new org.newdawn.slick.Image("images/croix_visee_mini.png");
        this.conePuissance = new org.newdawn.slick.Image("images/cone_puissance_mini.png");
        this.pictureHD = new org.newdawn.slick.Image("images/grenade_essai_right.png");
    }

    public Projectile generateProjectile(int terrain[][],int blockSize,Worms tireur) throws SlickException {
        return new HolyGrenadeProjectile(terrain,blockSize,tireur);
    }

}
