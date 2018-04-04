import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class GrenadeProjectile extends Projectile{
    //Les images et les sons sont déclarés static afin d'être présents en un seul exemplaire dans la RAM
    //(Cela évite l'engorgement de celle ci et des lags occasionnant bug de physique voir crash)
    protected final static org.newdawn.slick.Image pictureLeft_src = createImage("images/grenade_essai_left_mini.png");
    protected final static org.newdawn.slick.Image pictureRight_src = createImage("images/grenade_essai_right_mini.png");
    protected final static Sound bruitExplosion_src = createSound("music/Grenade.ogg");
    protected Sound bruitExplosion;//Son joué lors de l'explosion

    public GrenadeProjectile(int terrain[][], int blockSize,Worms tireur) throws SlickException {
        pictureLeft = pictureLeft_src;
        pictureRight = pictureRight_src;
        bruitExplosion = bruitExplosion_src;
        bruitFail = bruitFail_src;
        alive = true;
        normeVitesse = 1600*0.0025;
        hitBoxHauteur = 20;//Dimensions de la hitbox
        hitBoxLargeur = 20;
        masse = 0.01;//10g
        rayonExplosion = 70;//Rayon de l'explosion
        this.terrain = terrain;
        this.blockSize = blockSize;
        antiExplosion = false;
        hauteurBlock = terrain.length;
        largeurBlock = terrain[0].length;
        degat=75;//Degat de la grenade
        normeSouffleExplosion = 800;//Norme du souffle
        this.tireur = tireur;//Sauvegarde du tireur

        //chronoLaunchForce = 200;
        chronoExplosion = 2000;
    }

    public void explosion(Worms[] joueurs){
        //Explosion propre à la grenade

        genericExplosion(x,y,rayonExplosion);
        applyDegAndPhysic(joueurs);
        bruitExplosion.play();
    }

    public void draw(org.newdawn.slick.Graphics g){
        //Dessin de la grenade

        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        pictureRight.setRotation((float)((drawAngle/Math.PI)*180.0)+40);
        pictureRight.draw(x,y-pictureRight.getHeight()+1);
    }

    public void specialPhysic(int delta,Worms[] joueurs){}

    public void specialInit(double pourcentagePuissance){}

    public void specialLaunch(Weapon lanceur,double pourcentagePuissance){
        //Lancement spécifique à la grenade

        launch(lanceur,pourcentagePuissance,true);
    }

}
