import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Rocket extends Projectile {
    //Les images et les sons sont déclarés static afin d'être présents en un seul exemplaire dans la RAM
    //(Cela évite l'engorgement de celle ci et des lags occasionnant bug de physique voir crash)
    protected final static org.newdawn.slick.Image pictureLeft_src = createImage("images/rocket_left.png");
    protected final static org.newdawn.slick.Image pictureRight_src = createImage("images/rocket_right.png");
    protected final static Sound bruitExplosion_src = createSound("music/rocket_explosion.ogg");
    protected final static Sound bruitTir_src = createSound("music/bazooka_tir_2.ogg");
    protected Sound bruitExplosion;//Son joué lors de l'explosion
    protected Sound bruitTir;//Son joué lors du tir
    protected double normePropulsion;//Norme de la force de propulsion de la rocket
    protected Force propulsion;
    protected int accumulateurPropulsion;//Accumulateur permettant de faire decroiter la puissance de propulsion au fil du temps
    protected double facteurPropulsion;//Facteur multipliant la puissance de propulsion initiale
    protected boolean wormsTouche;//Indique si un Worms a été touché

    public Rocket(int terrain[][], int blockSize,Worms tireur) throws SlickException {
        pictureLeft = pictureLeft_src;
        pictureRight = pictureRight_src;
        bruitExplosion = bruitExplosion_src;
        bruitTir =  bruitTir_src;
        bruitFail = bruitFail_src;
        alive = true;
        normeVitesse = (1.0/2.0)*2000*0.0025;
        normePropulsion = (3.0/4.0)*(g/200.0);//Force de propulsion
        hitBoxHauteur = 5;//Dimensions de la hitbox de la rocket
        hitBoxLargeur = 5;
        masse = 0.01;
        rayonExplosion = 70;
        this.terrain = terrain;
        this.blockSize = blockSize;
        antiExplosion = false;
        hauteurBlock = terrain.length;
        largeurBlock = terrain[0].length;
        propulsion = new Force(42,42);//Initialisation factice
        accumulateurPropulsion = 1;
        degat=75;//degat de la rocket
        normeSouffleExplosion = 900;//Norme du souffle de l'explosion
        this.tireur = tireur;//On garde en mémoire le Worms ayant tiré

        //chronoLaunchForce = 200;
        chronoExplosion = 10000;
    }

    public void explosion(Worms[] joueurs){
        //Explosion du projectile

        genericExplosion(x,y,rayonExplosion);
        applyDegAndPhysic(joueurs);
        bruitExplosion.play();
        bruitTir.stop();
        bruitExplosion.play();
    }

    public void draw(org.newdawn.slick.Graphics g){
        //Dessin du projectile

        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        pictureRight.setRotation((float)((drawAngle/Math.PI)*180.0));
        pictureRight.draw(x,y-pictureRight.getHeight()+1);
    }

    public void specialPhysic(int delta,Worms[] joueurs){
        wormsTouche = false;
        //Regarde si la rocket est rentrée en collision avec des Worms
        for(Worms wor:joueurs){
            if(wor.inside(x,y) && wor!=tireur){
                wormsTouche = true;
            }
        }

        if(physic.contactDetected() || wormsTouche){
            //Si la rocket rentre en contact avec un block ou un mur, alors on indique qu'elle est mort, ie qu'elle doit exploser
            alive = false;
        }

        //On récupere l'angle actuelle de la trajectoire de la rocket
        drawAngle = Math.atan2(physic.getVitesse_y(),physic.getVitesse_x());
        //On retire l'ancienne force de propulsion
        physic.removeForce(propulsion);
        //On créer la nouvelle, légerement moins forte que la précedente
        facteurPropulsion = 1 - accumulateurPropulsion/800;
        if(facteurPropulsion<0){
            facteurPropulsion = 0;
        }
        //Et surtout toujours orienter selon la trajectoire
        propulsion = new Force( facteurPropulsion*normePropulsion*Math.cos(drawAngle), -(g/200.0)*(7.0/10.0)+facteurPropulsion*normePropulsion*Math.sin(drawAngle));
        //On applique à la rocket cette nouvelle force
        physic.addForce(propulsion);
        accumulateurPropulsion+= delta;
    }

    public void specialInit(double pourcentagePuissance){
        //Initialisation propre à la rocket
        //Permet de jouer le son du tir

        if(pourcentagePuissance<30){
            pourcentagePuissance = 30;
        }
        normePropulsion*= pourcentagePuissance/100.0;
        bruitTir.play();
    }

    public void specialLaunch(Weapon lanceur,double pourcentagePuissance){
        //Lancement spécifique à la rocket

        launch(lanceur,pourcentagePuissance,false);
    }

}
