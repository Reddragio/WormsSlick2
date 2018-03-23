import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class HolyGrenadeProjectile extends GrenadeProjectile{

    public HolyGrenadeProjectile(int terrain[][], int blockSize,Worms tireur) throws SlickException {
       super(terrain,blockSize,tireur);
        pictureLeft = new org.newdawn.slick.Image("images/HolyGrenadeSD_left.png");
        pictureRight = new org.newdawn.slick.Image("images/HolyGrenadeSD_right.png");
        bruitExplosion = new Sound("music/HOLYGRENADE.wav"); //Changer son
        bruitFail = new Sound("music/failSound.ogg");
        rayonExplosion = 150;
        degat=150;
        normeSouffleExplosion = 1200;
    }
}
