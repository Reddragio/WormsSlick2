public class Force {
    //Force s'appliquant à un objet
    //Cette classe permet une gestion facilité des forces s'appliquant aux objets dans le jeu
    //On peut ainsi ajouter ou enlever des forces avec un simple "add" ou "remove"

    protected double forceX;//Composantes selon X et Y
    protected double forceY;

    public Force(double forceX,double forceY){
        this.forceX = forceX;
        this.forceY = forceY;
    }

    public double getForceX() {
        return forceX;
    }

    public double getForceY() {
        return forceY;
    }
}
