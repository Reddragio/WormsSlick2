import org.newdawn.slick.*;

public class Teleporteur extends Weapon{
    //Teleporteur, permettant au jour de se teleporter n'importe où sur la cartes

    protected org.newdawn.slick.Image pictureRed;//Image du teleporteur en rouge, pour indiquer les endroits où on ne peut pas se teleporter
                                                 //(Là où il y a des blocks notamment, ou trop près du bord du terrain)
    protected Sound bruitTeleportation;//Bruit de la teleportation

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
        //Dessine le teleporteur à l'écran
        pictureLeft.draw(x-pictureLeft.getWidth()/2,y-pictureLeft.getHeight()/2);
    }

    public void drawTeleporteurRed(int x,int y){
        //Dessine la version rouge du teleporteur à l'écran
        pictureRed.draw(x-pictureRed.getWidth()/2,y-pictureRed.getHeight()/2);
    }

    public void playSound(){
        //Joue le son de la teleportation
        bruitTeleportation.play();
    }

}
