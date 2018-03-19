import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.InputStream;

public class GestionTours {
    protected final static double tempsDeplacement = 30000;
    protected final static double tempsVisee = 20000;
    protected final static double tempsVisualiserExplosion = 2000;
    protected int phase;
    protected boolean tourEquipe;
    protected Worms[] joueurs;
    protected Worms actualWorms;
    protected int nombreJoueurs;
    protected int nombreJoueursDevantJouer;
    protected long timer;
    protected boolean enTrainDeJouer;
    protected String couleurEquipe1;
    protected String couleurEquipe2;
    protected FenetreJeu mainFenetre;
    protected TrueTypeFont font2;

    public GestionTours(Worms[] joueurs,FenetreJeu mainFenetre){
        //Initialisation
        this.joueurs = joueurs;
        nombreJoueurs = joueurs.length;
        nombreJoueursDevantJouer = joueurs.length;
        couleurEquipe1 = joueurs[0].getCouleur();
        couleurEquipe2 = joueurs[3].getCouleur();
        enTrainDeJouer = false;
        tourEquipe = true;
        phase = 0;
        timer = 0;
        this.mainFenetre = mainFenetre;

        //Init police nom et vie
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("./fonts/WormsFont.ttf");
            Font police = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            police = police.deriveFont(18f); // set font size
            font2 = new TrueTypeFont(police, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLogic(int delta){
        if(!enTrainDeJouer){
            if(nombreJoueursDevantJouer==0){
                for(Worms wor:joueurs){
                    wor.setaDejaJoue(false);
                }
            }
            for(Worms wor: joueurs){
                if(!wor.aDejaJoue()){
                    if(tourEquipe && wor.getCouleur() == couleurEquipe1){
                        actualWorms = wor;
                        break;
                    }
                    else if(!tourEquipe && wor.getCouleur() == couleurEquipe2){
                        actualWorms = wor;
                        break;
                    }
                }
            }
            enTrainDeJouer = true;
            tourEquipe = !tourEquipe;
            phase = 0;
            timer = 0;
            actualWorms.setPlaying(true);
            actualWorms.setMovingState(true);
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
                nombreJoueursDevantJouer--;
                enTrainDeJouer = false;
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

    public void printTime(){
        if(phase==0){
            font2.drawString(20,20,String.valueOf((int)((tempsDeplacement-timer)*0.001)), org.newdawn.slick.Color.red);
        }
        else if(phase==1){
            font2.drawString(20,20,String.valueOf((int)((tempsVisee-timer)*0.001)), org.newdawn.slick.Color.red);
        }
        else if(phase==3){
            font2.drawString(20,20,String.valueOf((int)((tempsVisualiserExplosion-timer)*0.001)), org.newdawn.slick.Color.red);
        }

    }
}
