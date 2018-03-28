import org.newdawn.slick.BigImage;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;

public class GestionTours {
    protected org.newdawn.slick.Image chrono;
    protected BigImage endScreen;
    protected final static double tempsDeplacement = 30000;
    protected final static double tempsVisee = 20000;
    protected final static double tempsVisualiserExplosion = 1800;
    protected int phase;
    protected boolean tourEquipe;
    protected Worms[] joueurs;
    protected Worms actualWorms;
    protected int nombreJoueurs;
    protected int nombreJoueursDevantJouer;
    protected int nombreJoueursEnVie;
    protected long timer;
    protected boolean enTrainDeJouer;
    protected String couleurEquipe1;
    protected String couleurEquipe2;
    protected FenetreJeu mainFenetre;
    protected TrueTypeFont font2;
    protected int indexTeam1;
    protected int indexTeam2;
    protected int nombreJoueursEnVieTeam1;
    protected int nombreJoueursEnVieTeam2;
    protected long timerMessage;
    protected long tempsMessage;
    protected String messageAffiche;
    protected boolean printingMessage;
    protected org.newdawn.slick.Color couleurMessage;
    protected HashMap<String, org.newdawn.slick.Color> dico;
    protected boolean theEnd;
    protected int hauteur;
    protected int largeur;

    public GestionTours(Worms[] joueurs,FenetreJeu mainFenetre,int largeur,int hauteur) throws SlickException {
        //Initialisation
        chrono = new org.newdawn.slick.Image("images/mini_chrono.png");
        endScreen = new BigImage("images/endScreen.jpg");
        this.joueurs = joueurs;
        nombreJoueurs = joueurs.length;
        nombreJoueursEnVie = joueurs.length;
        nombreJoueursEnVieTeam1 = 3;
        nombreJoueursEnVieTeam2 = 3;
        couleurEquipe1 = joueurs[0].getCouleur();
        couleurEquipe2 = joueurs[3].getCouleur();
        enTrainDeJouer = false;
        tourEquipe = true;
        phase = 0;
        timer = 0;
        this.mainFenetre = mainFenetre;
        indexTeam1 = 0;
        indexTeam2 = 3;
        this.largeur = largeur;
        this.hauteur = hauteur;

        theEnd = false;

        timerMessage = 0;
        tempsMessage = 0;
        messageAffiche = "";
        printingMessage = false;
        couleurMessage = org.newdawn.slick.Color.black;
        //Création du dictionnaire des couleurs
        dico = new HashMap<String, org.newdawn.slick.Color>();
        dico.put("Rouge", org.newdawn.slick.Color.red);
        dico.put("Bleu", org.newdawn.slick.Color.blue);
        dico.put("Noir", org.newdawn.slick.Color.black);
        dico.put("Blanc", org.newdawn.slick.Color.white);
        dico.put("Vert", org.newdawn.slick.Color.green);

        //Init police nom et vie
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("./fonts/WormsFont.ttf");
            Font police = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            police = police.deriveFont(36f); // set font size
            font2 = new TrueTypeFont(police, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLogic(int delta) throws SlickException {
        if((nombreJoueursEnVieTeam1==0 || nombreJoueursEnVieTeam2==0)&&!theEnd){
            theEnd = true;
        }

        if(!theEnd){
            if(!enTrainDeJouer){
                if(tourEquipe){
                    while(!joueurs[indexTeam1].isAlive()){
                        indexTeam1++;
                        if(indexTeam1==3){
                            indexTeam1=0;
                        }
                    }
                    actualWorms = joueurs[indexTeam1];
                    indexTeam1++;
                    if(indexTeam1==3){
                        indexTeam1=0;
                    }
                }
                else{
                    while(!joueurs[indexTeam2].isAlive()){
                        indexTeam2++;
                        if(indexTeam2==6){
                            indexTeam2=3;
                        }
                    }
                    actualWorms = joueurs[indexTeam2];
                    indexTeam2++;
                    if(indexTeam2==6){
                        indexTeam2=3;
                    }
                }

                enTrainDeJouer = true;
                tourEquipe = !tourEquipe;
                phase = 0;
                timer = 0;
                actualWorms.setPlaying(true);
                actualWorms.setMovingState(true);
                showMessage("Tour de l'équipe "+actualWorms.getCouleur()+" ! "+actualWorms.getName()+" passe à l'attaque !",2500,dico.get(actualWorms.getCouleur()));
            }
            else{
                if(phase==0){
                    timer+=delta;
                    if(timer>=tempsDeplacement){
                        phase = 4;
                    }
                } else if (phase == 1) {
                    timer+=delta;
                    if(timer>=tempsVisee){
                        phase = 4;
                    }
                }
                else if(phase==3){
                    timer+=delta;
                    if(timer>=tempsVisualiserExplosion){
                        phase = 4;
                    }
                }
                else if(phase==4){
                    actualWorms.setPlaying(false);
                    actualWorms.setMovingState(false);
                    actualWorms.setAimingState(false);
                    actualWorms.setaDejaJoue(true);
                    mainFenetre.setPhaseInventaire(false);
                    mainFenetre.setPhaseTeleporteur(false);
                    enTrainDeJouer = false;
                }
            }
        }

    }

    public void setPhase(int phaseT){
        phase = phaseT;
        timer = 0;
    }

    public Worms getActualWorms() {
        return actualWorms;
    }

    public void nouvelleMort(Worms wor){
        nombreJoueursEnVie--;
        if(wor.getCouleur()==couleurEquipe1){
            nombreJoueursEnVieTeam1--;
        }
        else{
            nombreJoueursEnVieTeam2--;
        }
        if(wor==actualWorms){
            phase = 4;
        }
        showMessage(wor.getName()+" nous a quitté :'( ...",2500,dico.get(actualWorms.getCouleur()));
    }

    public void printTime(){
        if(phase==0){
            font2.drawString(largeur-70,25,String.valueOf((int)((tempsDeplacement-timer)*0.001)), org.newdawn.slick.Color.black);
        }
        else if(phase==1){
            font2.drawString(largeur-70,25,String.valueOf((int)((tempsVisee-timer)*0.001)), org.newdawn.slick.Color.black);
        }
        else if(phase==3){
            font2.drawString(largeur-70,25,String.valueOf((int)((tempsVisualiserExplosion-timer)*0.001)), org.newdawn.slick.Color.black);
        }
        if(phase==0 || phase==1 || phase==3){
            chrono.draw(largeur-150,2);
        }

    }

    public void printMessage(int delta){
        if(printingMessage){
            font2.drawString( largeur/2 - (messageAffiche.length()/2)*20,hauteur/2-10,messageAffiche,couleurMessage);
            timerMessage+=delta;
            if(timerMessage>=tempsMessage){
                printingMessage = false;
                timerMessage = 0;
            }
        }
    }

    public void showMessage(String message,int duree,org.newdawn.slick.Color couleur){
        messageAffiche = message;
        tempsMessage = duree;
        printingMessage = true;
        couleurMessage = couleur;
        timerMessage=0;
    }

    public void printEnd(){
        if(theEnd){
            endScreen.draw(0,0);
            if(nombreJoueursEnVieTeam1==0){
                messageAffiche = "L'équipe "+joueurs[3].getCouleur()+" a remporté la partie !";
                couleurMessage = dico.get(joueurs[3].getCouleur());
            }
            else{
                messageAffiche = "L'équipe "+joueurs[0].getCouleur()+" a remporté la partie !";
                couleurMessage = dico.get(joueurs[0].getCouleur());
            }
            font2.drawString(largeur/2 - (messageAffiche.length()/2)*20,hauteur/10,messageAffiche,couleurMessage);

            messageAffiche = "Merci d'avoir joué à notre jeu ;) !";
            couleurMessage = org.newdawn.slick.Color.magenta;
            font2.drawString(largeur/2 - (messageAffiche.length()/2)*20,hauteur/6,messageAffiche,couleurMessage);

            messageAffiche = "Un projet de fin d'année réalisé par Jack, Toto, Max et Paulo";
            couleurMessage = org.newdawn.slick.Color.red;
            font2.drawString(largeur/2 - (messageAffiche.length()/2)*20,hauteur/4,messageAffiche,couleurMessage);
        }
    }
}
