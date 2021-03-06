// holyGrenade visée
import org.newdawn.slick.SlickException;

public class HolyGrenade extends Grenade{

    public HolyGrenade(int nombre) throws SlickException {
        //Constructeur propre à la Holygrenade
        //Permet de la dessiner avec des images lui correspondant
        //(Le constructeur fait appel au constructeur de grenade car la HolyGrenade reprend de nombreuses
        //caracteristiques de son ancêtre)

        super(nombre);
        this.pictureLeft = new org.newdawn.slick.Image("images/HolyGrenadeSD_left.png");
        this.pictureRight = new org.newdawn.slick.Image("images/HolyGrenadeSD_right.png");
        this.viseur = new org.newdawn.slick.Image("images/croix_visee_mini.png");
        this.conePuissance = new org.newdawn.slick.Image("images/cone_puissance_mini.png");
        this.pictureHD = new org.newdawn.slick.Image("images/HolyGrenadeHD.png");
    }

    public Projectile generateProjectile(int terrain[][],int blockSize,Worms tireur) throws SlickException {
        //Au moment du lancement, permet d'obtenir la HolygrenadeProjectile associée à la HolyGrenade

        return new HolyGrenadeProjectile(terrain,blockSize,tireur);
    }

}
