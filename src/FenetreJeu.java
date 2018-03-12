import org.newdawn.slick.*;
import javax.swing.*;

public class FenetreJeu extends BasicGame{
    protected GameContainer container;
    protected int[][] terrain;
    protected int blockSize;
    protected int hauteur;
    protected int largeur;
    protected int hauteurBlock;
    protected int largeurBlock;
    protected Timer mt;
    protected int vitesseDep; //Cette variable correspond à l'intervalle entre deux entrées claviers pour déplacer
    //le Worms. On peut donc l'associer en quelque sorte à la vitesse de déplacement.
    protected long antiRepeatTime;
    protected Worms[] joueurs;
    protected boolean[] changementPrint; //Permet de rafraichir l'écran uniquement si il y a eu un changement
    protected boolean isMovingLeft;
    protected boolean isMovingRight;
    protected boolean augmentationAngleVisee;
    protected boolean diminutionAngleVisee;
    protected long tempEcoule;
    protected long lastTempEcoule;
    protected BigImage sky;
    protected BigImage big_ground;
    protected Image water;
    protected int texture_size;
    protected int hauteur_draw_texture;
    protected int largeur_draw_texture;
    protected Music themeWorms;
    protected boolean themeWormsActivation;
    protected int stackedEnter; //permet de gerer le double saut
    protected long lastTime; //permet de gerer le double saut

    protected Projectile projectileActuel;
    protected long timerExplosionProjectile;
    protected boolean phaseChoixPuissance;
    protected boolean phaseProjectile;
    protected boolean phaseInventaire;
    protected long timerChoixPuissance;
    protected long chronoChoixPuissance;
    protected double pourcentage;
    protected boolean enterRelache;
    protected String[][] tabNomCoul;
    protected GestionTerrain monde; //permet de connaitre le terrain tel qu'il a été généré (avant les explosions)

    //Explosion
    protected Animation aExplosion;
    protected boolean isExplosion;
    protected float tempsExplo;
    protected float timerExplo;

    //Experimental:
    protected int rayonExplosion;
    protected boolean visualiserExplosion;
    protected Input input;
    protected boolean antiExplosion;//Permet de mettre des blocs au lieu d'en détruire
    protected final static int blocIndestructibles[] = {2,3};
    protected boolean experimentalVisee;

    public FenetreJeu(int s,int x,int y, String[][] tab) {
        super("Worms Fighter Z - Slick Version");
        blockSize=s;
        tabNomCoul = tab;
        monde=new GestionTerrain();
        monde.genererTerrain(x,y,1);
        monde.genererFaille();
        monde.genererIles();
        terrain=monde.getTerrainInitial();
        stackedEnter=0;

    }

    public void init(GameContainer container) throws SlickException {
		//Initialisation du jeu
		//S'execute juste après le constructeur
        hauteurBlock = terrain.length;
        hauteur = hauteurBlock*blockSize;
        largeurBlock = terrain[0].length;
        largeur = largeurBlock*blockSize;

        vitesseDep = 10;
        antiRepeatTime = 0;

        //mt = new Timer(20,this); //L'horloge du jeu est bassé sur un timer se déclenchant toutes les 20ms
        //Cela permet donc en théorie d'avoir du 50 fps, ce qui est largement suffisant pour notre jeu
        //mt.start();

        changementPrint = new boolean[1];
        changementPrint[0] = true;



        joueurs = new Worms[1];
        joueurs[0] = new Worms(1,tabNomCoul[0][1],tabNomCoul[0][0],terrain,blockSize,changementPrint,500,100);
        joueurs[0].setMovingState(true);
        joueurs[0].setWeapon(new Bazooka(100));
        joueurs[0].setPlaying(true);

        this.container = container;
        isMovingLeft = false;
        isMovingRight = false;
        augmentationAngleVisee = false;
        diminutionAngleVisee = false;
        tempEcoule = 0;
        lastTempEcoule = 0;

        sky = new BigImage("images/Mountain_Background.png");
        big_ground = new BigImage("images/big_ground.png");

        themeWorms = new Music("music/worms-theme-song.ogg");
        themeWormsActivation = false;

        phaseProjectile = false;
        phaseChoixPuissance = false;
        chronoChoixPuissance = 1500;
        enterRelache = false;

        phaseInventaire = false;

        //SpriteSheet explosion

        Image Sprite = new Image("./images/SpriteSheetExplosion.png");
        int spritelong = 130;
        SpriteSheet sExplosion = new SpriteSheet(Sprite, spritelong,spritelong);
        tempsExplo = 1000;
        timerExplo = 0;
        aExplosion = new Animation(sExplosion, (int)tempsExplo/10);

        isExplosion=false;

        //Experimental:
        rayonExplosion = 40;
        visualiserExplosion = false;
        input = container.getInput();
        antiExplosion = false;
        experimentalVisee = false;

        spawnWorm();
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
		//Rendu graphique du jeu !
		//S'execute un maximum de fois par secondes, afin de maximiser les fps
		
        changementPrint[0] = false;

        sky.draw(0,0);

        int iFirst = 0,jFirst = 0,iLast = 0,jLast = 0;
        boolean first = true;
        for(int i=0;i<hauteurBlock;i++){
            for(int j=0;j<largeurBlock;j++){
                if(terrain[i][j]==1){
                    if(first){
                        first = false;
                        iFirst = i;
                        jFirst = j;
                    }
                    iLast = i;
                    jLast = j;
                }
                if((terrain[i][j]!=1 || j == largeurBlock-1)&&!first){
                    big_ground.draw(jFirst*blockSize,iFirst*blockSize,(jLast+1)*blockSize,(iLast+1)*blockSize,jFirst*blockSize,iFirst*blockSize,(jLast+1)*blockSize,(iLast+1)*blockSize);
                    first = true;
                }
            }
        }

        for(int i=0;i<terrain.length;i++){
            for(int j=0;j<terrain[0].length;j++){
                /*if(terrain[i][j]==0){
                    g.setColor(Color.cyan);
                    g.fillRect(blockSize*j,blockSize*i,blockSize,blockSize);
                }
                if(terrain[i][j]==1){
                    ground.draw(blockSize*j,blockSize*i);
                }*/
                if(terrain[i][j]==2){
                    g.setColor(Color.blue);
                    g.fillRect(blockSize*j,blockSize*i,blockSize,blockSize);
                }

            }
        }

        for(Worms wor: joueurs){
            wor.draw(g);
            if(wor.getAimingState()){
                wor.drawVisee();
                if(phaseChoixPuissance && timerChoixPuissance<= chronoChoixPuissance){
                    wor.armeActuelle.drawConePuissance((((double)timerChoixPuissance)/((double)chronoChoixPuissance))*100.0);
                }
            }
            if(wor.isPlaying()){
                if(phaseInventaire){
                    wor.drawInventaire(input);
                }
            }
        }

        if(phaseProjectile){
            projectileActuel.draw(g);
        }

        if(visualiserExplosion){
            g.setColor(Color.red);
            g.drawOval((float)(input.getMouseX()-rayonExplosion),(float)(input.getMouseY()-rayonExplosion),(float)(2*rayonExplosion),(float)(2*rayonExplosion));
        }



        if(isExplosion){
            aExplosion.draw((float)(projectileActuel.getx()-65),(float)(projectileActuel.gety()-65));
        }
    }

    public void update(GameContainer container, int delta) throws SlickException {
		//Met à jour la logique du jeu !
		//la boucle s'execute à la meme fréquence que la boucle render
		//Ce qui explique qu'à l'heure actuelle le jeu soit plus ou moins rapide
		//selon l'ordi

        for(Worms wor: joueurs){
            wor.applyPhysic(delta);
            /*if(wor.isPlaying()){
                if(phaseInventaire){

                }
            }*/
        }

        if(phaseChoixPuissance){
            timerChoixPuissance += delta;
             if(timerChoixPuissance>=chronoChoixPuissance || enterRelache){
                phaseChoixPuissance = false;
                 for(Worms wor: joueurs){
                     if(wor.getAimingState()){
                         wor.setAimingState(false);
                         try {
                             projectileActuel = (wor.getArmeActuelle()).generateProjectile(terrain,blockSize);
                         } catch (SlickException e) {
                         }
                         double pourcentagePuissanceTir;
                         if(timerChoixPuissance<=chronoChoixPuissance){
                             pourcentagePuissanceTir = (((double)timerChoixPuissance)/((double)chronoChoixPuissance))*100.0;
                         }
                         else{
                             pourcentagePuissanceTir = 100;
                         }

                         projectileActuel.launch(wor.getArmeActuelle(),pourcentagePuissanceTir);
                         timerExplosionProjectile = 0;
                         phaseProjectile = true;
                     }
                 }
             }
        }

        if(phaseProjectile){
            projectileActuel.applyPhysic(delta);
            timerExplosionProjectile += delta;
            if(timerExplosionProjectile >= projectileActuel.getChronoExplosion() || !projectileActuel.isAlive()){
                projectileActuel.explosion(joueurs);
                isExplosion=true;
                phaseProjectile = false;
                joueurs[0].setMovingState(true);
            }
        }

        tempEcoule += delta;
        if(tempEcoule - lastTempEcoule >= vitesseDep){
            lastTempEcoule = tempEcoule;
            for(Worms wor: joueurs) {
                if(isMovingLeft){
                    wor.deplacer(0);
                }
                else if(isMovingRight){
                    wor.deplacer(1);
                }

                if(augmentationAngleVisee){
                    wor.augmenterAngle();
                }
                else if(diminutionAngleVisee){
                    wor.diminuerAngle();
                }
            }
        }


        if(isExplosion){
            timerExplo+=delta;
            if(timerExplo >= tempsExplo-50){
                isExplosion=false;
                timerExplo=0;
            }

        }
    }

    /*public static void main(String[] args) throws SlickException {
		//Main
		//A deplacer dans une classe apart à l'avenir
		
        int tailleBloc = 5;
        int blocLargeur = 300; // imperativement des multiples de 10, pour que le dessin des textures se fasse sans bug
        int blocHauteur = 200;
        AppGameContainer app = new AppGameContainer(new FenetreJeu(tailleBloc,blocLargeur,blocHauteur));
        app.setDisplayMode(blocLargeur*tailleBloc, blocHauteur*tailleBloc, false); // Mode fenêtré
        app.setVSync(false);
        app.setTargetFrameRate(120);
        app.start();
    }*/

    /*public void actionPerformed(ActionEvent e){
        if(e.getSource()==mt){

            if(changementPrint[0]==true){
                repaint();
            }
        }
    }*/

    /*public void keyPressed(KeyEvent e) {
        for(Worms wor: joueurs){
            if(wor.getMovingState() == true){
                if((System.currentTimeMillis()-antiRepeatTime) >= vitesseDep){
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        wor.deplacer(0);
                        antiRepeatTime = System.currentTimeMillis();
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                        wor.deplacer(1);
                        antiRepeatTime = System.currentTimeMillis();
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        if(wor.get_orientation()==0){
                            wor.set_vitesse_x(-5);
                        }
                        else{
                            wor.set_vitesse_x(5);
                        }
                        wor.set_vitesse_y(5);
                        antiRepeatTime = System.currentTimeMillis();
                    }
                }
            }

        }
    }*/

    public void keyPressed(int key, char c){
		//Traitement des entrées claviers (appuis spécifiquement)
		
        for(Worms wor: joueurs) {
            if (wor.getMovingState()) {
                if (Input.KEY_LEFT == key) {
                        isMovingLeft = true;
                    } else if (Input.KEY_RIGHT == key) {
                        isMovingRight = true;
                    } else if (Input.KEY_SPACE == key) {
                        /*if (wor.get_orientation() == 0) {
                            wor.set_vitesse_x(-5);
                        } else {
                            wor.set_vitesse_x(5);
                        }*/
                        if(wor.isOnFloor)
                            stackedEnter=0;

                        if(stackedEnter==0  && wor.isOnFloor) {
                            wor.set_vitesse_y(-300);
                            if(wor.get_orientation()==0)
                                wor.set_vitesse_x(-100);
                            if(wor.get_orientation()==1)
                                wor.set_vitesse_x(100);
                            lastTime=System.currentTimeMillis();
                        }
                    stackedEnter++;

                        if(stackedEnter>1 && System.currentTimeMillis()-lastTime<180){
                            wor.set_vitesse_y(-450);

                            if(wor.get_orientation()==0)
                                wor.set_vitesse_x(70);
                            if(wor.get_orientation()==1)
                                wor.set_vitesse_x(-70);
                            stackedEnter=0;
                        }
                        wor.onFloorUpdate();
                }
                else if(Input.KEY_ENTER == key){
                    phaseInventaire = true;
                    joueurs[0].setMovingState(false);
                }
            }
            if(wor.getAimingState() && !phaseChoixPuissance){
                if (Input.KEY_UP == key) {
                    augmentationAngleVisee = true;
                }
                else if(Input.KEY_DOWN == key){
                    diminutionAngleVisee = true;
                }
                else if (Input.KEY_LEFT == key) {
                    wor.setOrientation(0);
                    wor.updateViseeOrientation();
                } else if (Input.KEY_RIGHT == key) {
                    wor.setOrientation(1);
                    wor.updateViseeOrientation();
                }
                else if(Input.KEY_ENTER == key){
                    phaseChoixPuissance = true;
                    timerChoixPuissance = 0;
                    enterRelache = false;
                }
            }
        }
        if (Input.KEY_M == key){
            if(!themeWormsActivation){
                themeWorms.loop();
                themeWormsActivation = true;
            }
            else{
                themeWorms.stop();
                themeWormsActivation = false;
            }
        }
        else if(Input.KEY_B == key){
            antiExplosion = !antiExplosion;
        }
        /*else if(Input.KEY_V == key){
            experimentalVisee = !experimentalVisee;
            if(experimentalVisee){
                joueurs[0].setMovingState(false);
                joueurs[0].setAimingState(true);
                joueurs[0].initVisee();
            }
            else{
                joueurs[0].setMovingState(true);
                joueurs[0].setAimingState(false);
            }
        }*/
        /*else if(Input.KEY_T == key){
            joueurs[0].set_y(50);
            System.out.println(joueurs[0].physic.getPixelCoordX());
        }*/

    }

    // méthode exécutée à chaque fois qu’une touche est relâchée
    public void keyReleased(int key, char c) {
		//Traitement des entrées claviers (relachements de touches spécifiquement)
		
        if (Input.KEY_ESCAPE == key) {
            container.exit();
        }
        else if (Input.KEY_LEFT == key) {
            isMovingLeft = false;
        } else if (Input.KEY_RIGHT == key) {
            isMovingRight = false;
        }
        else if (Input.KEY_UP == key) {
            augmentationAngleVisee = false;
        }
        else if(Input.KEY_DOWN == key){
            diminutionAngleVisee = false;
        }
        else if(Input.KEY_ENTER == key){
            enterRelache = true;
        }
    }




    // méthode exécutée à chaque fois qu’une touche unicode est utilisée (donc pas CTRL, SHIFT ou ALT par exemple)
    /*public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }*/

    public void mousePressed(int button, int x, int y){
		//Traitement de la souris
		
        if(button == 0){//Clik gauche
            if(phaseInventaire){
                for(Worms wor:joueurs){
                    if(wor.isPlaying){
                        if(wor.interactInventaire(input)){
                            joueurs[0].setAimingState(true);
                            joueurs[0].initVisee();
                            phaseInventaire = false;
                            }
                    }
                }
            }
            else{
                experimentalExplosion(x,y,rayonExplosion);
            }
        }
        else if(button==1){//Clik droit
            joueurs[0].set_x(x);
            joueurs[0].set_y(y);
        }
        else if(button==2){//Clik molette
            visualiserExplosion = !visualiserExplosion;
        }
    }

    public void mouseWheelMoved(int change){
        //Gestion de la rotation de la molette
        
        rayonExplosion += (change/120)*5;
        if(rayonExplosion <= 10){
            rayonExplosion = 10;
        }
    }



    public void experimentalExplosion(int xe,int ye,int rayon){
		//Explosion !!!!!!
		
        //Penser vérifier xe, ye dans les clous
        //Coin en haut à gauche du rectangle:
        int block_hg_x = (xe - rayon)/blockSize;
        block_hg_x = limiteInferieur(block_hg_x);
        int block_hg_y = (ye - rayon)/blockSize;
        block_hg_y = limiteInferieur(block_hg_y);
        //Coin en bas à droite du rectangle:
        int block_bd_x = (xe + rayon)/blockSize;
        block_bd_x = limiteSuperieurX(block_bd_x);
        int block_bd_y = (ye + rayon)/blockSize;
        block_bd_y = limiteSuperieurY(block_bd_y);

        double demi_block = blockSize/2.0;
        boolean destructible;
        for(int i=block_hg_y;i<=block_bd_y;i++){
            for(int j=block_hg_x;j<=block_bd_x;j++){
                if(distance(xe,ye,j*blockSize+demi_block,i*blockSize+demi_block)<=rayon){
                    destructible = true;
                    for(int strong :blocIndestructibles){
                        if(terrain[i][j]==strong){
                            destructible = false;
                        }
                    }
                    if(destructible){
                        if(!antiExplosion){
                            terrain[i][j] = 0;
                        }
                        else{
                            terrain[i][j] = 1;
                        }
                    }
                }
            }
        }
    }

    public int limiteInferieur(int k){
        if(k<0){
            k = 0;
        }
        return k;
    }

    public int limiteSuperieurX(int k){
        if(k>=largeurBlock){
            k = largeurBlock-1;
        }
        return k;
    }

    public int limiteSuperieurY(int k){
        if(k>=hauteurBlock){
            k = hauteurBlock-1;
        }
        return k;
    }

    public double distance(double x1,double y1,double x2,double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public void spawnWorm(){
        int spawnArea=largeurBlock/joueurs.length;
        int i=1;
        for(Worms wor: joueurs){
            int xs=(int) (i*spawnArea-Math.random()*spawnArea);
            int ys=monde.surfaceBlock(xs);
            wor.setpos(xs*blockSize,ys*blockSize-4);
        }

    }


}
