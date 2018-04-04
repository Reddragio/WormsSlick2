import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class HolyGrenadeProjectile extends GrenadeProjectile{
    //Les images et les sons sont déclarés static afin d'être présents en un seul exemplaire dans la RAM
    //(Cela évite l'engorgement de celle ci et des lags occasionnant bug de physique voir crash)
    protected final static org.newdawn.slick.Image pictureLeft_src = createImage("images/HolyGrenadeSD_left.png");
    protected final static org.newdawn.slick.Image pictureRight_src = createImage("images/HolyGrenadeSD_right.png");
    protected final static Sound bruitExplosion_src = createSound("music/HOLYGRENADE.wav");

    public HolyGrenadeProjectile(int terrain[][], int blockSize,Worms tireur) throws SlickException {
       super(terrain,blockSize,tireur);
       //Grace à super, on reprend toutes les caracteristiques de la grenade et on en modifie juste quelques une:
        pictureLeft = pictureLeft_src;
        pictureRight = pictureRight_src;
        bruitExplosion = bruitExplosion_src;
        rayonExplosion = 150;//Rayon d'explosion amelioré
        degat=150;//dégat doublé par rapport à la grenade
        normeSouffleExplosion = 1200;//souffle plus intense
    }
}
