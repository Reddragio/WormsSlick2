import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.InputStream;

public class Inventaire {
    //Classe permettant la gestion de l'inventaire du Worms
    //Permet de gerer les stocks de chaque arme que possède les équipes
    //Permet aussi d'assurer le dessin de l'inventaire et l'interaction de l'utilisateur avec celui-ci

    protected org.newdawn.slick.Image image;//Image de l'invenaitre (sans aucun objet à l'interieur)
    protected Weapon[] armesWorms;//Ensemble des armes contenues dans l'inventaire
    protected double drawScale[];//Echelles de dessin de chacune des armes
    protected TrueTypeFont font2;//Police utilisée pour indiquer le nombre de chaque arme possédée
    protected final static int hauteur = 2;//Hauteur en cases de l'inventaire
    protected final static int largeur = 3;//Largeur en case de l'inventaire
    protected int hauteurFenetre;//Hauteur de la fenêtre de jeu
    protected int largeurFenetre;//Largeur de la fenêtre de jeu
    protected int x,y;//Coordonnées du point en haut à gauche de l'inventaire
    protected final static int offsetX = 15;//Offset des cases de l'inventaire
    protected final static int offsetY = 55;
    protected final static int tailleCase = 110;//Taille du côté d'une case en pixels

    public Inventaire(int largeurFenetre,int hauteurFenetre) throws SlickException {
        image = new org.newdawn.slick.Image("images/inventaire.png");
        armesWorms = new Weapon[4];
        drawScale = new double[4];
        armesWorms[0] = new Bazooka(15);
        drawScale[0] = 0.7;
        armesWorms[1] = new Grenade(20);
        drawScale[1] = 0.9;
        armesWorms[2] = new HolyGrenade(2);
        drawScale[2] = 0.9;
        armesWorms[3] = new Teleporteur(3);
        drawScale[3] = 0.9;

        this.hauteurFenetre = hauteurFenetre;
        this.largeurFenetre = largeurFenetre;
        //Definition des coordonnées de l'inventaire à partir des dimensions de la fenêtre de jeu:
        x = largeurFenetre/2-image.getWidth()/2;
        y = hauteurFenetre/2-image.getHeight()/2;

        //Creation de la police
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("./fonts/WormsFont.ttf");
            Font police = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            police = police.deriveFont(19f); // set font size
            font2 = new TrueTypeFont(police, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Input input){
        //Dessine l'inventaire à l'écran

        //On dessine en premier l'inventaire vide:
        image.draw(x,y);

        //On dessine ensuite toutes les armes possédés par l'équipe à l'interieur:
        int i=0,j=0,k=0,l=0;
        while(((i+1)+j*largeur)<=armesWorms.length){
            if(armesWorms[i+j*largeur].getNombrePossede()>=1){
                if(x+offsetX+k*tailleCase<=input.getMouseX() && input.getMouseX() <= x+offsetX+(k+1)*tailleCase && y+offsetY+l*tailleCase <= input.getMouseY() && input.getMouseY() <= y+offsetY+(l+1)*tailleCase){
                    armesWorms[i+j*largeur].draw(x+offsetX+k*tailleCase,y+offsetY+l*(tailleCase-10),(float)(drawScale[i+j*largeur]+0.1));
                }
                else{
                    armesWorms[i+j*largeur].draw(x+offsetX+k*tailleCase,y+offsetY+l*(tailleCase-10),(float)drawScale[i+j*largeur]);
                }
                font2.drawString((float)(x+offsetX+(k+0.6)*tailleCase),(float)(y+offsetY+(l-0.2)*tailleCase),"x"+armesWorms[i+j*largeur].getNombrePossede(),org.newdawn.slick.Color.red);

                k++;
                if(k==largeur){
                    k = 0;
                    l++;
                }
            }
            i++;
            if(i==largeur){
                i = 0;
                j++;
            }
        }
    }

    public boolean interact(Input input,Worms worms){
        //Permet d'interargir avec l'inventaire d'après la position de la souris
        //(sachant que cette fonction a été appellée après un clik gauche de la souris)

        boolean armeChoisie = false;
        int i=0,j=0,k=0,l=0;
        while(((i+1)+j*largeur)<=armesWorms.length){
            if(armesWorms[i+j*largeur].getNombrePossede()>=1){
                if(x+offsetX+k*tailleCase<=input.getMouseX() && input.getMouseX() <= x+offsetX+(k+1)*tailleCase && y+offsetY+l*(tailleCase-10) <= input.getMouseY() && input.getMouseY() <= y+offsetY+(l+1)*(tailleCase-10)){
                    //Si le joueur a cliqué sur une arme, alors elle devient son arme principal
                    worms.setWeapon(armesWorms[i+j*largeur]);
                    armesWorms[i+j*largeur].decreasePossede();
                    armeChoisie = true;
                }
                k++;
                if(k==largeur){
                    k = 0;
                    l++;
                }
            }
            i++;
            if(i==largeur){
                i = 0;
                j++;
            }
        }
        //On indique si le joueur a choisi une arme ou non
        return armeChoisie;
    }

}

