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
    protected long tempEcoule;
    protected long lastTempEcoule;
    protected Image[] ground;
    protected BigImage sky;
    protected Image water;
    protected int texture_size;
    protected int hauteur_draw_texture;
    protected int largeur_draw_texture;
    protected int[][] terrain_alea_texture;//Map de valeurs aléatoires servant à dessiner les textures aléatoirement
    protected Music themeWorms;
    protected boolean themeWormsActivation;

    //Experimental:
    protected int rayonExplosion;
    protected boolean visualiserExplosion;
    protected Input input;
    protected boolean antiExplosion;//Permet de mettre des blocs au lieu d'en détruire

    public FenetreJeu(int s,int x,int y) {
        super("Worms Fighter Z - Slick Version");
        blockSize=s;
        genererTerrain(x,y);

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
        joueurs[0] = new Worms(1,"Popaul",terrain,blockSize,changementPrint,500,100);
        joueurs[0].setMovingState(true);

        this.container = container;
        isMovingLeft = false;
        isMovingRight = false;
        tempEcoule = 0;
        lastTempEcoule = 0;

        sky = new BigImage("images/Mountain_Background.png");

        ground = new Image[5];
        ground[0] = new Image("images/texture_sol50_pixel.png");
        ground[1] = new Image("images/texture_sol51_pixel.png");
        ground[2] = new Image("images/texture_sol52_pixel.png");
        ground[3] = new Image("images/texture_sol53_pixel.png");
        ground[4] = new Image("images/texture_sol54_pixel.png");

        texture_size = ground[0].getWidth();
        hauteur_draw_texture = hauteur/texture_size;
        largeur_draw_texture = largeur/texture_size;

        terrain_alea_texture = new int[hauteur_draw_texture][largeur_draw_texture];
        for(int i=0;i<hauteur_draw_texture;i++){
            for(int j=0;j<largeur_draw_texture;j++){
                terrain_alea_texture[i][j] = (int)(Math.random()*5);
            }
        }

        themeWorms = new Music("music/worms-theme-song.ogg");
        themeWormsActivation = false;

        //Experimental:
        rayonExplosion = 40;
        visualiserExplosion = false;
        input = container.getInput();
        antiExplosion = false;
    }

    public void render(GameContainer container, Graphics g) throws SlickException {
		//Rendu graphique du jeu !
		//S'execute un maximum de fois par secondes, afin de maximiser les fps
		
        changementPrint[0] = false;

        sky.draw(0,0);

        int block_in_texture = texture_size/blockSize;
        for(int k=0;k<hauteur_draw_texture;k++){
            for(int m=0;m<largeur_draw_texture;m++){
                for(int i=0;i<block_in_texture;i++){
                    for(int j=0;j<block_in_texture;j++){
                        if(terrain[k*block_in_texture+i][m*block_in_texture+j]==1){
                            ground[terrain_alea_texture[k][m]].draw(m*texture_size+j*blockSize,k*texture_size+i*blockSize,m*texture_size+(j+1)*blockSize,k*texture_size+(i+1)*blockSize,j*blockSize,i*blockSize,(j+1)*blockSize,(i+1)*blockSize);
                        }
                    }
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
        }

        if(visualiserExplosion){
            g.setColor(Color.red);
            g.drawOval((float)(input.getMouseX()-rayonExplosion),(float)(input.getMouseY()-rayonExplosion),(float)(2*rayonExplosion),(float)(2*rayonExplosion));
        }
    }

    public void update(GameContainer container, int delta) throws SlickException {
		//Met à jour la logique du jeu !
		//la boucle s'execute à la meme fréquence que la boucle render
		//Ce qui explique qu'à l'heure actuelle le jeu soit plus ou moins rapide
		//selon l'ordi
		
        for(Worms wor: joueurs){
            wor.applyForces();
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
            }
        }
    }

    public static void main(String[] args) throws SlickException {
		//Main
		//A deplacer dans une classe apart à l'avenir
		
        int tailleBloc = 5;
        int blocLargeur = 300; // imperativement des multiples de 10, pour que le dessin des textures se fasse sans bug
        int blocHauteur = 200;
        AppGameContainer app = new AppGameContainer(new FenetreJeu(tailleBloc,blocLargeur,blocHauteur));
        app.setDisplayMode(blocLargeur*tailleBloc, blocHauteur*tailleBloc, false); // Mode fenêtré
        //app.setVSync(false);
        //app.setTargetFrameRate(120);
        app.start();
    }

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
                    } else if (Input.KEY_ENTER == key) {
                        if (wor.get_orientation() == 0) {
                            wor.set_vitesse_x(-5);
                        } else {
                            wor.set_vitesse_x(5);
                        }
                        wor.set_vitesse_y(5);
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
    }



    // méthode exécutée à chaque fois qu’une touche unicode est utilisée (donc pas CTRL, SHIFT ou ALT par exemple)
    /*public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }*/

    public void mousePressed(int button, int x, int y){
		//Traitement de la souris
		
        if(button == 0){//Clik gauche
            experimentalExplosion(x,y,rayonExplosion);
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

    public void genererTerrain(int x, int y){
        int[][] t=new int[y][x];
        int p=(int) y/2;
        int r=0; //modification denivelé
        double montagnes=2.2; //coefficient montagnes
        double plaine=2.5; //coefficient plaines
        int oldp=p;
        for(int i=0;i<t[0].length;i++){
            if((Math.random()*montagnes)>1&&p!=oldp){
                r=(p-oldp)+(int)(Math.random()*2-1);
            }
            else{
                r=(int)((Math.random()*plaine-Math.random()*plaine));
            }
            oldp=p;
            if(p>y-24) p-=Math.abs(r);
            if(p<24) p+=Math.abs(r);
            else p+=r;
            if(p<t.length && p>0){
                t[p][i]=1;
                for(int j=p;j<t.length;j++){
                    t[j][i]=1;
                }
            }
        }
        for(int i=0;i<t[0].length;i++){
 +            t[t.length-1][i]=2;
 +            t[t.length-2][i]=2;
 +            t[t.length-3][i]=2;
 +        }
        terrain=t;
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
        for(int i=block_hg_y;i<=block_bd_y;i++){
            for(int j=block_hg_x;j<=block_bd_x;j++){
                if(distance(xe,ye,j*blockSize+demi_block,i*blockSize+demi_block)<=rayon){
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

}
