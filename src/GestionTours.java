import org.newdawn.slick.BigImage;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;

public class GestionTours {
    protected org.newdawn.slick.Image chrono;//Image du chronomètre affiché en haut à droite
    protected BigImage endScreen;//Image d'arrière plan affichée lorsque la partie est finie
    protected final static double tempsDeplacement = 30000;//Durée en ms de la phase de déplacement
    protected final static double tempsVisee = 20000;//Durée en ms de la phase inventaire+visée
    protected final static double tempsVisualiserExplosion = 1800;//Temps laissé pour admirer l'explosion du projectile et ses conséquences
    protected int phase;//Entier indiquant le numero de la phase en cours
    protected boolean tourEquipe;//Tour de l'équipe 1 si True, tour de l'équipe 2 sinon
    protected Worms[] joueurs;//pointeur vers le tableau contenant tous les Worms
    protected Worms actualWorms;//pointeur vers le Worms jouant actuellement
    protected int nombreJoueurs;//Nombre de joueurs, en l'occurence 6 dans la version actuelle du jeu
    protected int nombreJoueursEnVie;//Nombre de joueurs encore en vie
    protected long timer;
    protected boolean enTrainDeJouer;//Indique si un Worms est en train de jouer ou non
    protected String couleurEquipe1;//Couleur de l'équipe 1, au format String
    protected String couleurEquipe2;//Couleur de l'équipe 2, au format String
    protected FenetreJeu mainFenetre;//Pointeur vers la fenetre de jeu principal
    protected TrueTypeFont font2;//Police utilisée pour les messages d'annonce
    protected TrueTypeFont font3;//Police utilisée pour l'affichage des stats
    protected int indexTeam1;//Index du joueur devant actuellement joué dans l'équipe 1
    protected int indexTeam2;//Index du joueur devant actuellement joué dans l'équipe 2
    protected int nombreJoueursEnVieTeam1;//Nombre de joueurs en vie dans l'équipe 1
    protected int nombreJoueursEnVieTeam2;//Nombre de joueurs en vie dans l'équipe 2
    protected long timerMessage;//Timer servant à temporiser l'affichage des messages à l'écran
    protected long tempsMessage;//Temps durant lequel le message doit rester à l'écran
    protected String messageAffiche;//Message affiché à l'écran
    protected boolean printingMessage;//Indique si un message doit être affiché ou non
    protected org.newdawn.slick.Color couleurMessage;//Couleur d'affichage du message
    protected HashMap<String, org.newdawn.slick.Color> dico;//Dictionnaire permettant de faire la correspondance entre couleur sous forme
                                                            //de String et couleur sous forme d'objet Color
    protected boolean theEnd;//Boolean indiquant si la partie est finie ou non
    protected int hauteur;//Hauteur de la fenêtre du jeu
    protected int largeur;//Largeur de la fenêtre du jeu

    //Affichage cercle pour indiquer le Worms qui va jouer
    protected final static long tempsCercle = 1000;//Temps d'affichage du cercle
    protected final static int epaisseurCercle = 4;//Epaisseur du cercle
    protected final static int rayonCercle = 300;//Rayon du cercle (pixelsà
    protected final static int minRayonCercle = 25;//Plus petit rayon du cercle à afficher
    protected long timerCercle;//Timer servant à temporiser l'affichage du cercle
    protected boolean drawCercle;//Indique si le cercle doit être dessiné ou non
    protected int xCentre;//Coordonnée x du centre du cercle
    protected int yCentre;//Coordonnée y du centre du cercle
    protected int rayon;//rayon actuel du cercle

    //Les variables suivantes servant à l'affichage des données (positions,vitesses, etc) du Worms dans le CheatMode
    //On utilise un tableau pour les sauvegarder afin de pouvoir uniquement actualiser les données tous les 100ms
    //Cela évite d'avoir un effet de scintillement à l'écran, en plus d'avoir une difficulté à lire les données
    protected long accumulateurData;//Permet de temporiser l'affichage des stats à l'écran
    protected long lastData;//Permet de retenir le dernier instant où ont été sauvegardés les données du Worms
    protected double[][] dataMemory;//Garde en mémoire les données du Worms jusqu'au prochain rafraichissement

    public GestionTours(Worms[] joueurs,FenetreJeu mainFenetre,int largeur,int hauteur) throws SlickException {
        //Initialisation de la gestion des tours
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
        tourEquipe = true;//L'équipe 1 commence
        phase = 0;//Le premiers Worms commence par sa phase de deplacement
        timer = 0;
        this.mainFenetre = mainFenetre;
        indexTeam1 = 0;
        indexTeam2 = 3;
        this.largeur = largeur;//On récupère la taille de la fenêtre de jeu
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

        //Init police nom,vie et data
        try {
            InputStream inputStream	= ResourceLoader.getResourceAsStream("./fonts/WormsFont.ttf");
            Font police = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            police = police.deriveFont(36f); // set font size
            font2 = new TrueTypeFont(police, false);
            //police = police.deriveFont(12f);
            //font3 = new TrueTypeFont(police, false);
            Font awtFont = new Font("Arial", Font.BOLD, 12);
            font3 = new TrueTypeFont(awtFont, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Affichage du cercle pour désigner le Worms devant jouer
        drawCercle = false;
        timerCercle = 0;

        //Affichage des données dans le "cheat mode"
        accumulateurData = 0;
        lastData = 0;
        dataMemory = new double[6][8];
    }

    public void updateLogic(int delta) throws SlickException {
        //Fonction appellée à chaque frame permettant de temporiser et d'assurer le jeu tour par tour

        if((nombreJoueursEnVieTeam1==0 || nombreJoueursEnVieTeam2==0)&&!theEnd){
            //Si l'une des deux équipes n'a plus aucun Worms, cela signifie que la partie est finine
            theEnd = true;
        }

        if(!theEnd){
            //Tant que la partie n'est pas finie
            if(!enTrainDeJouer){
                //Si aucun Worms ne joue
                //Alors on regarde c'est à qui de jouer grâce à tourEquipe et aux indexs de chaque équipe
                if(tourEquipe){
                    while(!joueurs[indexTeam1].isAlive()){
                        //On ne fait jouer que les Worms encore vivant
                        indexTeam1++;
                        if(indexTeam1==3){
                            indexTeam1=0;
                        }
                    }
                    //On enregistre le Worms qui va jouer:
                    actualWorms = joueurs[indexTeam1];
                    indexTeam1++;
                    if(indexTeam1==3){
                        indexTeam1=0;
                    }
                }
                else{
                    while(!joueurs[indexTeam2].isAlive()){
                        //On ne fait jouer que les Worms encore vivant
                        indexTeam2++;
                        if(indexTeam2==6){
                            indexTeam2=3;
                        }
                    }
                    //On enregistre le Worms qui va jouer:
                    actualWorms = joueurs[indexTeam2];
                    indexTeam2++;
                    if(indexTeam2==6){
                        indexTeam2=3;
                    }
                }

                //On indique qu'un Worms a été trouvé et qu'il va jouer
                enTrainDeJouer = true;
                tourEquipe = !tourEquipe;//Au prochain tour, c'est l'autre équipe qui jouera
                phase = 0;//Le Worms commence par la phase de déplacement
                timer = 0;
                actualWorms.setPlaying(true);//On indique que le Worms actuel est en train de jouer
                actualWorms.setMovingState(true);//Et de bouger
                beginCercle();//On indique visuellement par un cercle le Worms qui va jouer
                //On affiche également un message:
                showMessage("Tour de l'équipe "+actualWorms.getCouleur()+" ! "+actualWorms.getName()+" passe à l'attaque !",2500,dico.get(actualWorms.getCouleur()));
            }
            else{
                if(phase==0){
                    //Phase de deplacement
                    timer+=delta;
                    if(timer>=tempsDeplacement){
                        phase = 4;
                    }
                } else if (phase == 1) {
                    //Phase menu+visée
                    timer+=delta;
                    if(timer>=tempsVisee){
                        phase = 4;
                    }
                }
                //La phase 2 n'a pas de code
                //Elle correspond au tir actuellement en cours
                //(la grenade ou la rocket est actuellement en train de se deplacer à l'écran)

                else if(phase==3){
                    //La phase 3 correspond à la visualisation de l'explosion
                    timer+=delta;
                    if(timer>=tempsVisualiserExplosion){
                        phase = 4;
                    }
                }
                else if(phase==4){
                    //La phase 4 correspond à la fin de tour
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
        //Permet de définir la phase de jeu
        phase = phaseT;
        timer = 0;
    }

    public Worms getActualWorms() {
        //Retourne le Worms actuellement en train de jouer
        return actualWorms;
    }

    public void nouvelleMort(Worms wor){
        //Permet d'indiquer à GestionTours qu'un Worms est mort
        //Cela lui permet de réagir en conséquence, en affichant un message
        //voir même en déclanchant la fin de parties

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
        //Affiche le temps restant pour la phase actuelle en haut à droite de l'écran

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
        //Affiche un message personnalisé à l'écran
        //Permet ainsi d'indique que c'est le tour d'un Worms, qu'un Worms est mort, etc

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
        //Permet d'afficher le message en paramètre pendant la durée souhaitée
        //Fonctionne grâce à la fonction printMessage ci-dessus

        messageAffiche = message;
        tempsMessage = duree;
        printingMessage = true;
        couleurMessage = couleur;
        timerMessage=0;
    }

    public void printEnd(){
        //Affiche l'écran de fin, une fois la partie finie

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

    public void printCercle(org.newdawn.slick.Graphics g,int delta){
        //Affiche le cercle permettant d'indiquer le Worms dont le tour vient de commencer

        if(drawCercle){
            timerCercle+=delta;
            xCentre = actualWorms.getX()+actualWorms.getHitBoxLargeur()/2;
            yCentre = actualWorms.getY()-actualWorms.getHitBoxHauteur()/2;
            rayon = (int)(rayonCercle*(1.0-timerCercle/(double)tempsCercle));
            g.setColor(dico.get(actualWorms.getCouleur()));
            for(int i=1;i<=epaisseurCercle;i++){
                rayon+=1;
                g.drawOval(xCentre-rayon,yCentre-rayon,2*rayon,2*rayon);
            }
            rayon-=epaisseurCercle;
            if(rayon < minRayonCercle || timerCercle > tempsCercle){
                drawCercle = false;
                timerCercle = 0;
            }
        }
    }

    public void beginCercle(){
        //Permet de débuter l'affichage du cercle autour du Worms jouant actuellement

        drawCercle = true;
        timerCercle = 0;
    }

    public void printData(int delta){
        //Affiche à l'écran les données de position, vitesses, accelerations pour chaque Worms
        //Cette fonction est utilisée dans le mode développeur (ou "cheat mode")

        accumulateurData+=delta;
        int hauteurLigne = 14;

        if(accumulateurData-lastData>= 100 || lastData == 0){
            lastData = accumulateurData;
            for(int i=0;i<joueurs.length;i++){
                font3.drawString( (i+1)*largeur/8,hauteurLigne,joueurs[i].getName(),dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,2*hauteurLigne,"X= "+(int)joueurs[i].physic.getXpixel()+"px",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,3*hauteurLigne,"Y= "+(int)joueurs[i].physic.getYpixel()+"px",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,4*hauteurLigne,"X= "+joueurs[i].physic.getX()+"m",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,5*hauteurLigne,"Y= "+joueurs[i].physic.getY()+"m",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,6*hauteurLigne,"Vitesse X= "+joueurs[i].physic.getVitesse_x()+"m/s",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,7*hauteurLigne,"Vitesse Y= "+joueurs[i].physic.getVitesse_y()+"m/s",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,8*hauteurLigne,"Acceleration X= "+joueurs[i].physic.getAcceleration_x()+"m/s²",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,9*hauteurLigne,"Acceleration Y= "+joueurs[i].physic.getAcceleration_y()+"m/s²",dico.get(joueurs[i].getCouleur()));
                dataMemory[i][0] = joueurs[i].physic.getXpixel();
                dataMemory[i][1] = joueurs[i].physic.getYpixel();
                dataMemory[i][2] = joueurs[i].physic.getX();
                dataMemory[i][3] = joueurs[i].physic.getY();
                dataMemory[i][4] = joueurs[i].physic.getVitesse_x();
                dataMemory[i][5] = joueurs[i].physic.getVitesse_y();
                dataMemory[i][6] = joueurs[i].physic.getAcceleration_x();
                dataMemory[i][7] = joueurs[i].physic.getAcceleration_y();
            }
        }
        else{
            for(int i=0;i<joueurs.length;i++){
                font3.drawString( (i+1)*largeur/8,hauteurLigne,joueurs[i].getName(),dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,2*hauteurLigne,"X= "+(int)dataMemory[i][0]+"px",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,3*hauteurLigne,"Y= "+(int)dataMemory[i][1]+"px",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,4*hauteurLigne,"X= "+dataMemory[i][2]+"m",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,5*hauteurLigne,"Y= "+dataMemory[i][3]+"m",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,6*hauteurLigne,"Vitesse X= "+dataMemory[i][4]+"m/s",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,7*hauteurLigne,"Vitesse Y= "+dataMemory[i][5]+"m/s",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,8*hauteurLigne,"Acceleration X= "+dataMemory[i][6]+"m/s²",dico.get(joueurs[i].getCouleur()));
                font3.drawString( (i+1)*largeur/8,9*hauteurLigne,"Acceleration Y= "+dataMemory[i][7]+"m/s²",dico.get(joueurs[i].getCouleur()));
            }
        }
    }
}
