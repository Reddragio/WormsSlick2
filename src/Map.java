public class Map {
    //Classe permettant de stocker efficacement toutes les données propre à chaque map

    protected String nom;//Nom de la map
    protected String adresseArrierePlan;//Adresse du fichier de l'arriere plan
    protected String adresseGround;//Adresse du fichier du premier plan
    protected String adresseMap;//Adresse de la bitmap de la physique
    protected String adressePrevisualisation;//Adresse de la previsualisation pour le menu
    protected String adresseEau;//Adresse de la mer pour le bas de la map
    protected int hauteurEau;//Hauteur de l'eau sur la map

    public Map(String nom,String adresseArrierePlan, String adresseGround, String adresseMap, String previsualisation,String adresseEau,int hauteurEau) {
        this.nom = nom;
        this.adresseArrierePlan = adresseArrierePlan;
        this.adresseGround = adresseGround;
        this.adresseMap = adresseMap;
        this.adressePrevisualisation = previsualisation;
        this.adresseEau = adresseEau;
        this.hauteurEau = hauteurEau;
    }

    public String toString(){
        return nom;
    }

    public String getAdressePrevisualisation() {
        return adressePrevisualisation;
    }

    public String getAdresseArrierePlan() {
        return adresseArrierePlan;
    }

    public String getAdresseGround() {
        return adresseGround;
    }

    public String getAdresseMap() {
        return adresseMap;
    }

    public String getAdresseEau() { return adresseEau; }

    public int getHauteurEau() { return hauteurEau; }

}
