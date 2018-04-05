public class Block {
    //Contient l'ensemble des informations utiles pour un block de la map donnée
    //L'usage de cette classe permet de simplifier grandement le code du moteur physique
    //en évitant l'usage systematique de tableau pour manipuler les blocks

    public int x;//Coordonnées du block
    public int y;
    public int valeur;//valeur du block

    public Block(int x,int y,int valeur){
        this.x = x;
        this.y = y;
        this.valeur = valeur;
    }

    public String toString() {
        return "Block{" +
                "x=" + x +
                ", y=" + y +
                ", valeur=" + valeur +
                '}';
    }
}
