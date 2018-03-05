// Chargement des bibliothèques Swing et AWT
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends JFrame implements ActionListener {

    private JTextField textChoixNom11;
    private JTextField textChoixNom12;
    private JTextField textChoixNom13;
    private JTextField textChoixNom2;
    private JButton Jouer;
    private JComboBox couleurWorms1;


    public Menu(){
        this.setTitle("Menu Worms");
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(500,700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Main Panel
        JPanel Main = new JPanel();
        Main.setBounds(0,0,500,700);
        Main.setLayout(null);
        //A terme ce serai mieux une image en background
        Main.setBackground(Color.white);

        //Logo
        ImageIcon LogoW = new ImageIcon("./images/logo.png");
        JLabel logo = new JLabel(LogoW);
        logo.setBounds(50,50,396,112);
        Main.add(logo);

        //BoutonJouer
        ImageIcon BoutonJouer = new ImageIcon("./images/Jouer.png");
        Jouer = new JButton(BoutonJouer);
        Jouer.setBounds(50,550,400,100);
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


        textChoixNom11 = new JTextField();
        textChoixNom11.setBounds(80,230,90,30);
        textChoixNom12 = new JTextField();
        textChoixNom12.setBounds(80,270,90,30);
        textChoixNom13 = new JTextField();
        textChoixNom13.setBounds(80,310,90,30);

        JLabel photo1 = new JLabel(new ImageIcon("./images/skin_worms_left.png"));
        photo1.setBounds(60,350,40,80);

        String[] Couleurs = {"Rouge", "Vert", "Bleu", "Noir", "Marron", "Rose", "Blanc"};
        couleurWorms1 = new JComboBox(Couleurs);
        couleurWorms1.setSelectedIndex(0);
        couleurWorms1.addActionListener(this);
        couleurWorms1.setBounds(60,450,90,40);

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

        }
    }
    public static void main (String[] args){
        Menu menu = new Menu();
    }
}
