import org.newdawn.slick.*;

public class Bazooka extends Weapon {
    public Bazooka(int nombre) throws SlickException {
        //Constructeur propre au bazooka
        //Permet de le dessiner avec des images lui correspondant

        this.pictureLeft = new org.newdawn.slick.Image("images/bazooka_mini_left.png");
        this.pictureRight = new org.newdawn.slick.Image("images/bazooka_mini_right.png");
        this.viseur = new org.newdawn.slick.Image("images/croix_visee_mini.png");
        this.conePuissance = new org.newdawn.slick.Image("images/cone_puissance_mini.png");
        this.pictureHD = new org.newdawn.slick.Image("images/bazooka.png");
        offSetLeft = 0;
        offSetRight = 0;
        /*coinGaucheOffset = Math.PI/2.0+Math.atan(pictureRight.getWidth()/pictureRight.getHeight());
        coinDroiteOffset = Math.atan(pictureLeft.getHeight()/pictureLeft.getWidth());*/
        coinGaucheOffset = Math.PI;
        coinDroiteOffset = 0;
        facteurDrawOffsetLeft = 1.0;
        facteurDrawOffsetRight = 0;
        nombrePossede = nombre;
    }

    public Projectile generateProjectile(int terrain[][],int blockSize,Worms tireur) throws SlickException {
        //Au moment du lancement, permet d'obtenir la rocket associée au bazooka

        return new Rocket(terrain,blockSize,tireur);
    }
}
