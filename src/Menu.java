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


        ArrayList<String> tab = nomWorm(3);
        System.out.println(tab);
        textChoixNom11 = new JTextField(tab.get(0));
        textChoixNom11.setBounds(80,230,90,30);
        textChoixNom12 = new JTextField(tab.get(1));
        textChoixNom12.setBounds(80,270,90,30);
        textChoixNom13 = new JTextField(tab.get(2));
        textChoixNom13.setBounds(80,310,90,30);



        String[] Couleurs = {"Rouge", "Vert", "Bleu", "Noir", "Blanc"};
        couleurWorms1 = new JComboBox(Couleurs);
        couleurWorms1.setSelectedIndex(0);
        couleurWorms1.addActionListener(this);
        couleurWorms1.setBounds(60,550,90,40);

        photo1 = new JLabel();
        photo1.setBounds(60,350,80,160);
        updateLabel(Couleurs[couleurWorms1.getSelectedIndex()]);


        Main.add(couleurWorms1);
        Main.add(Worms1);
        Main.add(Nom11);
        Main.add(Nom12);
        Main.add(Nom13);
        Main.add(textChoixNom11);
        Main.add(textChoixNom12);
        Main.add(textChoixNom13);
        Main.add(photo1);




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


        }
        if(e.getSource() == couleurWorms1){
            String CouleurChoisie1 = (String)couleurWorms1.getSelectedItem();
            updateLabel(CouleurChoisie1);
        }
    }
    public static void main (String[] args){
        Menu menu = new Menu();
    }

    public void updateLabel(String Couleur) {
        ImageIcon icon = new ImageIcon("images/Worm" + Couleur + "HD.png");
        photo1.setIcon(icon);
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
