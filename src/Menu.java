// Chargement des bibliothèques Swing et AWT
import org.newdawn.slick.*;
import javax.swing.*;
import java.awt.*;
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

    private ArrayList<String> colorWormsList;

    public Menu(){
        this.setTitle("Menu Worms");
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(500,800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Main Panel
        JLabel Main = new JLabel();
        Main.setBounds(0,0,500,800);
        Main.setLayout(null);
        Main.setIcon(new ImageIcon("./images/backgroundMenu.png"));

        //NomWorms
        ArrayList<String> tab = nomWorm(6);

        //Logo
        ImageIcon LogoW = new ImageIcon("./images/logo.png");
        JLabel logo = new JLabel(LogoW);
        logo.setBounds(50,50,396,112);
        Main.add(logo);

        //BoutonJouer
        ImageIcon BoutonJouer = new ImageIcon("./images/Jouer.png");
        Jouer = new JButton(BoutonJouer);
        Jouer.setBounds(50,650,400,100);
        Jouer.setBorderPainted(false);
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
        Worms1.setText("Equipe 1 :");
        Worms1.setBounds(20,180,60,40);

        JLabel Nom11 = new JLabel();
        Nom11.setText("Nom n°1 :");
        Nom11.setBounds(20,230,60,30);
        JLabel Nom12 = new JLabel();
        Nom12.setText("Nom n°2 :");
        Nom12.setBounds(20,270,60,30);
        JLabel Nom13 = new JLabel();
        Nom13.setText("Nom n°3 :");
        Nom13.setBounds(20,310,60,30);



        //System.out.println(tab);
        textChoixNom11 = new JTextField(tab.get(0));
        textChoixNom11.setBounds(80,230,90,30);
        textChoixNom12 = new JTextField(tab.get(1));
        textChoixNom12.setBounds(80,270,90,30);
        textChoixNom13 = new JTextField(tab.get(2));
        textChoixNom13.setBounds(80,310,90,30);

        couleurWorms1 = new JComboBox(colorWormsList.toArray());
        couleurWorms1.setSelectedIndex(0);
        couleurWorms1.addActionListener(this);
        couleurWorms1.setBounds(60,550,90,40);

        photo1 = new JLabel();
        photo1.setBounds(60,350,80,160);

        //Pour l'équipe 2 worms

        JLabel Worms2 = new JLabel();
        Worms2.setText("Equipe 2 :");
        Worms2.setBounds(300,180,60,40);

        JLabel Nom21 = new JLabel();
        Nom21.setText("Nom n°1 :");
        Nom21.setBounds(300,230,60,30);
        JLabel Nom22 = new JLabel();
        Nom22.setText("Nom n°2 :");
        Nom22.setBounds(300,270,60,30);
        JLabel Nom23 = new JLabel();
        Nom23.setText("Nom n°3 :");
        Nom23.setBounds(300,310,60,30);

        textChoixNom21 = new JTextField(tab.get(3));
        textChoixNom21.setBounds(360,230,90,30);
        textChoixNom22 = new JTextField(tab.get(4));
        textChoixNom22.setBounds(360,270,90,30);
        textChoixNom23 = new JTextField(tab.get(5));
        textChoixNom23.setBounds(360,310,90,30);

        couleurWorms2 = new JComboBox(colorWormsList.toArray());
        couleurWorms2.setSelectedIndex(1);
        couleurWorms2.addActionListener(this);
        couleurWorms2.setBounds(340,550,90,40);

        photo2 = new JLabel();
        photo2.setBounds(340,350,80,160);


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

        //Affichage
        this.setContentPane(Main);
        this.setVisible(true);
    }
    public void actionPerformed (ActionEvent e){
        if(e.getSource()== Jouer){
            String NomWorms11 = textChoixNom11.getText();
            String NomWorms12 = textChoixNom12.getText();
            String NomWorms13 = textChoixNom13.getText();
            System.out.println(NomWorms11);
            System.out.println((String)couleurWorms1.getSelectedItem());

            this.setVisible(false);
            System.out.println("Lancement de la partie :) !...");
            try{
                launchGame();
            }
            catch(SlickException e1){
                //Sert à contourner les exceptions slicks génériques
            }

        }
        if(e.getSource() == couleurWorms1 || e.getSource() == couleurWorms2){
            updateLabel(colorWormsList,couleurWorms1,couleurWorms2);
        }

    }

    public void launchGame() throws SlickException{
        int tailleBloc = 5;
        int blocLargeur = 300; // imperativement des multiples de 10, pour que le dessin des textures se fasse sans bug
        int blocHauteur = 200;
        AppGameContainer app = new AppGameContainer(new FenetreJeu(tailleBloc,blocLargeur,blocHauteur));
        app.setDisplayMode(blocLargeur*tailleBloc, blocHauteur*tailleBloc, false); // Mode fenêtré
        app.setVSync(false);
        app.setTargetFrameRate(120);
        app.start();
    }

    public static void main (String[] args){
        Menu menu = new Menu();
    }

    public void updateLabel(ArrayList<String> tab, JComboBox worms1, JComboBox worms2) {
        int i = worms1.getSelectedIndex();
        int j = worms2.getSelectedIndex();

        if (i==j){
            j+=1;
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

    public ArrayList<String> nomWorm( int nb){
        String[] Noms = {"Alberto", "Rex", "Wormito", "Fredo", "Gilbert", "Michel", "xXKevinXx","Ivan Touskivol","Chibroux","ElVerDeLaVega","Carlos"};
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
