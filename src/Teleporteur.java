import org.newdawn.slick.*;

public class Teleporteur extends Weapon{
    protected org.newdawn.slick.Image pictureRed;
    protected Sound bruitTeleportation;

    public Teleporteur(int nombre) throws SlickException {
        this.pictureLeft = new org.newdawn.slick.Image("images/viseurTeleporteurMini.png");
        this.pictureHD = new org.newdawn.slick.Image("images/teleporteur3.png");
        this.pictureRed = new org.newdawn.slick.Image("images/viseurTeleporteurRedMini.png");
        bruitTeleportation = new org.newdawn.slick.Sound("music/teleportation.ogg");
        nombrePossede = nombre;
    }

    public Projectile generateProjectile(int terrain[][],int blockSize,Worms tireur) throws SlickException {
        //Fonction inutilisée ici
        return new Rocket(terrain,blockSize,tireur);
    }

    public void drawTeleporteur(int x,int y){
        pictureLeft.draw(x-pictureLeft.getWidth()/2,y-pictureLeft.getHeight()/2);
    }

    public void drawTeleporteurRed(int x,int y){
        pictureRed.draw(x-pictureRed.getWidth()/2,y-pictureRed.getHeight()/2);
    }

    public void playSound(){
        bruitTeleportation.play();
    }

}
