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
}
