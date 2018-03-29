// Chargement des bibliothèques Swing et AWT
import java.awt.Font;
import java.io.*;

import org.newdawn.slick.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.util.ArrayList;

public class Menu extends JFrame implements ActionListener {

    private JTextField textChoixNom11;
    private JTextField textChoixNom12;
    private JTextField textChoixNom13;
    private JTextField textChoixNom2;
    private JButton Jouer;
    private JComboBox couleurWorms1;
    private JLabel photo1;

    private JTextField textChoixNom21;
    private JTextField textChoixNom22;
    private JTextField textChoixNom23;
    private JComboBox couleurWorms2;
    private JLabel photo2;
    private String[][] joueurs;
    private Font police;
    private Font police2;
    private Font FatPolice;

    private JComboBox choixMap;
    private JLabel previsuMap;

    private ArrayList<String> colorWormsList;
    private boolean seulementUneFois;

    protected Music themeWorms;

    public Menu() throws SlickException {
        this.setTitle("Menu Worms");
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(1500,800);
        this.setLocation(180,100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Police
        try {
            //create the font to use. Specify the size!
            police = Font.createFont(Font.TRUETYPE_FONT, new File("fonts\\WormsFont.ttf")).deriveFont(12f);
            FatPolice = Font.createFont(Font.TRUETYPE_FONT, new File("fonts\\WormsFont.ttf")).deriveFont(20f);
            police2 = police.deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts\\WormsFont.ttf")));

        } catch (IOException e) {
            e.printStackTrace();
        } catch(FontFormatException e) {
            e.printStackTrace();
        }



        //Main Panel
        JLabel Main = new JLabel();
        Main.setBounds(0,0,500,800);
        Main.setLayout(null);
        //Main.setIcon(new ImageIcon("./images/backgroundMenu.png"));
        Main.setIcon(new ImageIcon("images/Mountain_Background.png"));

        //NomWorms
        ArrayList<String> tab = nomWorm(6);

        //Logo
        ImageIcon LogoW = new ImageIcon("./images/logo.png");
        JLabel logo = new JLabel(LogoW);
        logo.setBounds(50,50,396,112);
        Main.add(logo);

        //BoutonJouer
        ImageIcon BoutonJouerPresse = new ImageIcon("./images/Jouer.png");
        ImageIcon BoutonJouer = new ImageIcon("./images/Jouer2.png");
        Jouer = new JButton(BoutonJouer);
        Jouer.setRolloverIcon(BoutonJouerPresse);
        Jouer.setBounds(50,650,400,100);
        Jouer.setBorderPainted(false);
        Jouer.setContentAreaFilled(false);
        Jouer.addActionListener(this);
        Main.add(Jouer);

        //Array List Couleur
        colorWormsList = new ArrayList<String>();
        colorWormsList.add("Rouge");
        colorWormsList.add("Bleu");
        colorWormsList.add("Noir");
        colorWormsList.add("Blanc");
        colorWormsList.add("Vert");

        //Pour l'équipe 1
        JLabel Worms1 = new JLabel();
        Worms1.setFont(FatPolice);
        Worms1.setText("Equipe 1 :");
        Worms1.setBounds(20,180,120,40);

        JLabel Nom11 = new JLabel();
        Nom11.setText("Nom n°1 :");
        Nom11.setBounds(20,230,80,30);
        JLabel Nom12 = new JLabel();
        Nom12.setText("Nom n°2 :");
        Nom12.setBounds(20,270,80,30);
        JLabel Nom13 = new JLabel();
        Nom13.setText("Nom n°3 :");
        Nom13.setBounds(20,310,80,30);
        Nom11.setFont(police);
        Nom12.setFont(police);
        Nom13.setFont(police);


        //System.out.println(tab);
        textChoixNom11 = new JTextField(tab.get(0));
        textChoixNom11.setBounds(100,230,100,30);
        textChoixNom12 = new JTextField(tab.get(1));
        textChoixNom12.setBounds(100,270,100,30);
        textChoixNom13 = new JTextField(tab.get(2));
        textChoixNom13.setBounds(100,310,100,30);
        textChoixNom11.setFont(police);
        textChoixNom12.setFont(police);
        textChoixNom13.setFont(police);



        couleurWorms1 = new JComboBox(colorWormsList.toArray());
        couleurWorms1.setSelectedIndex(0);
        couleurWorms1.addActionListener(this);
        couleurWorms1.setBounds(60,550,90,40);
        couleurWorms1.setFont(police);

        photo1 = new JLabel();
        photo1.setBounds(60,350,80,160);

        //Pour l'équipe 2 worms

        JLabel Worms2 = new JLabel();
        Worms2.setText("Equipe 2 :");
        Worms2.setBounds(280,180,120,40);
        Worms2.setFont(FatPolice);

        JLabel Nom21 = new JLabel();
        Nom21.setText("Nom n°1 :");
        Nom21.setBounds(280,230,80,30);
        JLabel Nom22 = new JLabel();
        Nom22.setText("Nom n°2 :");
        Nom22.setBounds(280,270,80,30);
        JLabel Nom23 = new JLabel();
        Nom23.setText("Nom n°3 :");
        Nom23.setBounds(280,310,80,30);
        Nom21.setFont(police);
        Nom22.setFont(police);
        Nom23.setFont(police);

        textChoixNom21 = new JTextField(tab.get(3));
        textChoixNom21.setBounds(360,230,100,30);
        textChoixNom22 = new JTextField(tab.get(4));
        textChoixNom22.setBounds(360,270,100,30);
        textChoixNom23 = new JTextField(tab.get(5));
        textChoixNom23.setBounds(360,310,100,30);
        textChoixNom21.setFont(police);
        textChoixNom22.setFont(police);
        textChoixNom23.setFont(police);

        couleurWorms2 = new JComboBox(colorWormsList.toArray());
        couleurWorms2.setSelectedIndex(1);
        couleurWorms2.addActionListener(this);
        couleurWorms2.setBounds(340,550,90,40);
        couleurWorms2.setFont(police);

        photo2 = new JLabel();
        photo2.setBounds(340,350,80,160);

        //Maps
        ArrayList<Map> maps = new ArrayList<Map>();
        maps.add(new Map("Montagnes célestes","images/Mountain_Background.png","images/big_ground_FullHD.png","images/map1.bmp","images/previsuMap1.png","images/sea_dark_large.png",40));
        //maps.add(new Map("Test eau","images/Mountain_Background.png","images/big_ground_FullHD.png","images/gruyere.bmp","images/previsuTest.png"));
        maps.add(new Map("Chateau Fort","images/zelda_background.jpg","images/big_ground_FullHD2.png","images/map3.bmp","images/previsuChateau_new.png","images/sea_dark_large.png",40));
        maps.add(new Map("London","images/london_background.jpg","images/London.png","images/London.bmp","images/previsuLondon.png","images/sea_dark_large_sepia.png",40));
        maps.add(new Map("Naval","images/pirate_background.jpg","images/Maps/Naval.png","images/Maps/Naval.bmp","images/previsuNaval.png","images/sea_verydark_large.png",40));
        maps.add(new Map("New York","images/Mountain_Background.png","images/Maps/NY.png","images/Maps/NY.bmp","images/previsuChateau.png","images/sea_dark_large.png",0));
        maps.add(new Map("Petrol","images/Mountain_Background.png","images/Maps/Petrol.png","images/Maps/Petrol.bmp","images/previsuChateau.png","images/sea_dark_large.png",20));

        //Choix map
        choixMap = new JComboBox(maps.toArray());
        choixMap.setSelectedIndex(0);
        choixMap.addActionListener(this);
        choixMap.setBounds(810,640,400,50);
        choixMap.setFont(police2);
        choixMap.addActionListener(this);
        Main.add(choixMap);

        JLabel titreMap = new JLabel();
        titreMap.setText("Carte :");
        titreMap.setBounds(710,646,120,40);
        titreMap.setFont(FatPolice);
        Main.add(titreMap);

        previsuMap = new JLabel();
        previsuMap.setBounds(500,50,1000,550);
        Main.add(previsuMap);

        updatePrevisu((Map)choixMap.getSelectedItem());

        //Initialisation des images des worms
        updateLabel(colorWormsList,couleurWorms1,couleurWorms2);


        Main.add(couleurWorms1);
        Main.add(Worms1);
        Main.add(Nom11);
        Main.add(Nom12);
        Main.add(Nom13);
        Main.add(textChoixNom11);
        Main.add(textChoixNom12);
        Main.add(textChoixNom13);
        Main.add(photo1);

        Main.add(couleurWorms2);
        Main.add(Worms2);
        Main.add(Nom21);
        Main.add(Nom22);
        Main.add(Nom23);
        Main.add(textChoixNom21);
        Main.add(textChoixNom22);
        Main.add(textChoixNom23);
        Main.add(photo2);

        //Musique
        themeWorms = new Music("music/worms-theme-song.ogg");
        themeWorms.loop();

        seulementUneFois = true;

        //Affichage
        this.setContentPane(Main);
        this.setVisible(true);
    }
    public void actionPerformed (ActionEvent e){
        if(seulementUneFois && e.getSource()== Jouer){
            seulementUneFois = false;
            themeWorms.stop();
            Jouer.removeActionListener(this);
            //Noms définitifs des worms
            String[] Noms = {textChoixNom11.getText(),textChoixNom12.getText(),textChoixNom13.getText(),textChoixNom21.getText(),textChoixNom22.getText(),textChoixNom23.getText()};

            /*System.out.println(NomWorms11);
            System.out.println((String)couleurWorms1.getSelectedItem());*/

            //Couleurs Choisies
            String[] CouleursChoisies = {(String)couleurWorms1.getSelectedItem(),(String)couleurWorms2.getSelectedItem()};


            //Initialisation Joueurs
            joueurs = new String[6][2];
            for (int i=0; i<6;i++) {
                int k=0;
                if(i>=3){
                    k=1;
                }
                joueurs[i][0] = Noms[i];
                joueurs[i][1] = CouleursChoisies[k];
            }



            this.setVisible(false);
            System.out.println("Lancement de la partie :) !...");
            /*try
            {
                Thread.sleep(200);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }*/
            try{
                launchGame(joueurs);
            }
            catch(SlickException e1){
                //Sert à contourner les exceptions slicks génériques
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        if(seulementUneFois &&(e.getSource() == couleurWorms1 || e.getSource() == couleurWorms2)){
            updateLabel(colorWormsList,couleurWorms1,couleurWorms2);
        }

        if(e.getSource() == choixMap){
            Map newMap = (Map)choixMap.getSelectedItem();
            updatePrevisu(newMap);
        }

    }

    public void launchGame(String[][] tab) throws SlickException, IOException {
        int tailleBloc = 5;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        int blocLargeur = (int)(width/tailleBloc); // imperativement des multiples de 10, pour que le dessin des textures se fasse sans bug
        int blocHauteur = (int)(height/tailleBloc);
        AppGameContainer app = new AppGameContainer(new FenetreJeu(tailleBloc,blocLargeur,blocHauteur,tab,(Map)choixMap.getSelectedItem(),(int)width,(int)height));
        app.setDisplayMode(blocLargeur*tailleBloc, blocHauteur*tailleBloc, true); // Mode fenêtré
        app.setVSync(false);
        app.setTargetFrameRate(120);



        app.start();
    }

    public static void main (String[] args) throws SlickException {
        Menu menu = new Menu();
    }

    public void updateLabel(ArrayList<String> tab, JComboBox worms1, JComboBox worms2) {
        int i = worms1.getSelectedIndex();
        int j = worms2.getSelectedIndex();

        if (i==j){
            j+= 1;
            if(j > tab.size()-1){
                j = 0;
            }
        }
        String Couleur1 = tab.get(i);
        String Couleur2 = tab.get(j);

        ImageIcon icon1 = new ImageIcon("images/Worm" + Couleur1 + "HD.png");
        ImageIcon icon2 = new ImageIcon("images/Worm" + Couleur2 + "HD.png");
        worms1.setSelectedIndex(i);
        worms2.setSelectedIndex(j);
        photo1.setIcon(icon1);
        photo2.setIcon(icon2);

    }

    public void updatePrevisu(Map newMap){
        previsuMap.setIcon(new ImageIcon(newMap.getAdressePrevisualisation()));
    }

    public ArrayList<String> nomWorm( int nb){
        String[] Noms = {"Alberto", "Rex", "Wormito", "Fredo", "Gilbert", "Michel", "xXKevinXx","Ivan Touskivol","Chibroux","ElVerDeLaVega","Carlos","Jean-Marie","Jacky-Michel","Chibrette","Blaise P","Freud","Jean-Eude","Roméo","Pedro","Juan","Vladimir","Trump","Margaux","Wormy","Nadine","Géraldine","Krustufle","Marguerite"};
        ArrayList<String> tab= new ArrayList<String>();
        int i =0;
        int max = Noms.length;
        while (i<nb){
            int random = (int)(Math.random()*max);
            if(!tab.contains(Noms[random])){
                tab.add(Noms[random]);
                i++;
            }
        }
        return tab;
    }
}
