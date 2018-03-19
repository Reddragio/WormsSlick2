import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class HolyGrenadeProjectile extends GrenadeProjectile{

    public HolyGrenadeProjectile(int terrain[][], int blockSize,Worms tireur) throws SlickException {
       //CHANGER IMAGE HOLY NADE
       super(terrain,blockSize,tireur);
        pictureLeft = new org.newdawn.slick.Image("images/grenade_essai_left_mini.png");
        pictureRight = new org.newdawn.slick.Image("images/grenade_essai_right_mini.png");
        bruitExplosion = new Sound("music/Grenade.ogg"); //Changer son
        bruitFail = new Sound("music/failSound.ogg");
        rayonExplosion = 40000;
        degat=200;
    }
}
