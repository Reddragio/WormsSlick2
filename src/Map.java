public class Map {
    protected String nom;
    protected String adresseArrierePlan;
    protected String adresseGround;
    protected String adresseMap;
    protected String adressePrevisualisation;
    protected String adresseEau;
    protected int hauteurEau;

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
