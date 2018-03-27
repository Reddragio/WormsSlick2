public class Map {
    protected String nom;
    protected String adresseArrierePlan;
    protected String adresseGround;
    protected String adresseMap;
    protected String adressePrevisualisation;

    public Map(String nom,String adresseArrierePlan, String adresseGround, String adresseMap, String previsualisation) {
        this.nom = nom;
        this.adresseArrierePlan = adresseArrierePlan;
        this.adresseGround = adresseGround;
        this.adresseMap = adresseMap;
        this.adressePrevisualisation = previsualisation;
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
}
