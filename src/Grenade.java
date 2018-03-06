import org.newdawn.slick.*;

public class Grenade extends Weapon {

    public Grenade() throws SlickException {
        this.pictureLeft = new org.newdawn.slick.Image("images/grenade_essai_left_mini.png");
        this.pictureRight = new org.newdawn.slick.Image("images/grenade_essai_right_mini.png");
        offSetLeft = -40;
        offSetRight = 40;
        coinGaucheOffset = Math.PI/2.0+Math.atan(pictureRight.getWidth()/pictureRight.getHeight());
        coinDroiteOffset = Math.atan(pictureLeft.getHeight()/pictureLeft.getWidth());
    }


}
