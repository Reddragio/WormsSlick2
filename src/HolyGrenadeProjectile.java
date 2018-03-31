import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class HolyGrenadeProjectile extends GrenadeProjectile{
    protected final static org.newdawn.slick.Image pictureLeft_src = createImage("images/HolyGrenadeSD_left.png");
    protected final static org.newdawn.slick.Image pictureRight_src = createImage("images/HolyGrenadeSD_right.png");
    protected final static Sound bruitExplosion_src = createSound("music/HOLYGRENADE.wav");

    public HolyGrenadeProjectile(int terrain[][], int blockSize,Worms tireur) throws SlickException {
       super(terrain,blockSize,tireur);
        pictureLeft = pictureLeft_src;
        pictureRight = pictureRight_src;
        bruitExplosion = bruitExplosion_src;
        rayonExplosion = 150;
        degat=150;
        normeSouffleExplosion = 1200;
    }
}
