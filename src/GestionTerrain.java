import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GestionTerrain {
    //Classe qui assurait auparavant la génération aléatoire du terrain
    //Aujourd'hui, seule le constructeur prenant en argument une bitmap de la physique de la map est utilisé dans le jeu

    private int[][] terrainInitial; //Le terrain tel qu'il est après la génération
    int[] plusBas;
    int[] plusHaut;
    protected int niveauEau;

    public GestionTerrain() {
    }

    public void genererTerrain(int x, int y, int type){
        int[][] t=new int[y][x];
        if(type == 1){
            //Generation classique
            int p=(int) y/2; //point de départ
            int r=0;
            double montagnes=3; //coefficient montagnes
            double plaine=2.2; //dénivelé
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


        }
        else if(type >=2){
            if(type == 2){
                //map plate
                for(int i=y/2;i<y;i++){
                    for(int j=0;j<x;j++){
                        t[i][j] = 1;
                    }
                }
            }
            else if(type == 3){
                //map escalier
                for(int i=y/2;i<y;i++){
                    for(int j=i;j<x;j++){
                        t[y-1-i][j] = 1;
                    }
                }
            }
        }

        terrainInitial=t;
        plusBas=pointLePlusBas();
        plusHaut=pointLePlusHaut();
    }

    public void genererTerrain(int x, int y, String adresse,double drawScaleX,double drawScaleY) throws IOException {
        int[][] t = new int[y][x];
        BufferedImage image = ImageIO.read(new File(adresse));

        for(double antibugOffset=0;antibugOffset<=0.25;antibugOffset+=0.25){
            for (int xPixel = 0; xPixel < image.getWidth(); xPixel++) //*
            {
                for (int yPixel = 0; yPixel < image.getHeight(); yPixel++) //*
                {
                    int color = image.getRGB(xPixel,yPixel); //*
                    if (color== Color.BLACK.getRGB()) {
                        t[(int)(yPixel*drawScaleY+antibugOffset)][(int)(xPixel*drawScaleX+antibugOffset)] = 1;
                    } else {
                        t[(int)(yPixel*drawScaleY+antibugOffset)][(int)(xPixel*drawScaleX+antibugOffset)] = 0;
                    }
                }
            }
        }

        terrainInitial = t;
        plusBas=pointLePlusBas();
        plusHaut=pointLePlusHaut();
    }

    public void setHauteurEau(int y,int hauteurEau){
        niveauEau = y-hauteurEau;
    }

    public int[] pointLePlusBas(){
        int xbas=0;
        int ybas=0;
        for(int i=2;i<terrainInitial.length-2;i++){
            for(int j=1;j<terrainInitial[0].length;j++){
                if(terrainInitial[i][j]==1 && terrainInitial[i-1][j]==0)
                    if(i>ybas){
                    xbas=j;
                    ybas=i;
                    }
            }
        }
        return new int[]{xbas,ybas};
    }

    public int[] pointLePlusHaut(){
        int xhaut=0;
        int yhaut=terrainInitial.length;
        for(int i=terrainInitial.length-2;i>2;i--){
            for(int j=1;j<terrainInitial[0].length;j++){
                if(terrainInitial[i][j]==1 && terrainInitial[i-1][j]==0)
                    if(i<yhaut){
                        xhaut=j;
                        yhaut=i;
                    }
            }
        }
        return new int[]{xhaut,yhaut};
    }

    public void genererFaille(){
        int epaisseur=12; //l'épaisseur de la faille
        int asperites=3; //irrégularités dans la faille
        int epMini=7; //épaisseur minimum de la faille
        int epMaxi=17; //épaisseur maximum de la faille
        int entree=4; //taille de l'embouchure de la faille
        int lastx=50;
        int lasty=50;



        int xInit=plusBas[0];
        int yInit=plusBas[1];
        boolean sens; //Pour savoir si la faille sera vers la gauche ou la droite: 0= gauche, 1= droite
        if(xInit<terrainInitial[0].length/2) sens=true;
        else sens=false;
        /*for(int I=-epaisseur-entree+4;I<epaisseur+entree+4;I++){
            for(int J=-epaisseur-entree;J<epaisseur+entree;J++){
                if(xInit+J>0 && xInit+J<terrainInitial[0].length&& yInit+I>0 && yInit+I<terrainInitial.length){
                    if(Math.sqrt(I*I+J*J)<epaisseur+entree){
                        terrainInitial[yInit+I-epaisseur][xInit+J]=0;
                    }
                }
            }
        }*/
        for(int i=yInit-10;i<terrainInitial.length-Math.random()*40;i++){
            int randomIntRel=(int) (Math.random()*asperites-Math.random()*asperites);
            if(epaisseur+randomIntRel>epMini&&epaisseur+randomIntRel<epMaxi) epaisseur+=randomIntRel;
            if(sens){
                for(int j=xInit-10;j<terrainInitial[0].length-xInit-2;j++){
                    for(int k=-epaisseur;k<epaisseur;k++)
                        if(j-xInit==i-yInit-k){
                            if(i<terrainInitial.length&&j>0&&j<terrainInitial[0].length){
                                terrainInitial[i][j]=0;
                                lastx=j;
                                lasty=i;
                            }
                        }
                }
            }
            else{
                for(int j=xInit+10;j>2;j--){
                    for(int k=-epaisseur;k<epaisseur;k++)
                        if(xInit-j==i-yInit-k){
                        if(i<terrainInitial.length&&j>0&&j<terrainInitial[0].length){
                            terrainInitial[i][j]=0;
                            lastx=j;
                            lasty=i;
                        }
                        }
                }
            }
            lastx=(int) (lastx-epaisseur/2);
            for(int k=0;k<epaisseur;k++){
                for(int l=0;l<epaisseur/2;l++){
                    if(lasty+l<terrainInitial.length&&lastx +k-1<terrainInitial[0].length) {
                        if(lasty + l<terrainInitial.length && lastx + k - l>0 && lastx + k - l<terrainInitial[0].length) terrainInitial[lasty + l][lastx + k - l] = 0;
                        if(lasty + l<terrainInitial.length && lastx - k + l>0 && lastx - k + l<terrainInitial[0].length) terrainInitial[lasty + l][lastx - k + l] = 0;
                    }
                }
            }
        }
    }

    public void genererIles(){
        int largeur=130;
        int hauteur=40;
        double montagnes1=3; //coefficient montagnes
        double montagnes2=2; //coefficient montagnes
        double plaine=2; //dénivelé

        int centreIleY=(int) (Math.random()*(plusHaut[1]-2*hauteur)+hauteur);
        if(centreIleY-hauteur<=0)
            centreIleY=(int) (Math.random()*(plusHaut[1]-2*hauteur)+hauteur);
        int centreIleX=(int) (Math.random()*(terrainInitial[0].length-2*largeur)+largeur);

        int p=(int) centreIleY-hauteur/2+2; //point de départ
        int r=0;
        int oldp=p;
        /*for(int y=centreIleY-hauteur/2;y<centreIleY+hauteur/2;y++){
            for(int x=centreIleX-largeur/2+randomIntRel(asperites);x<centreIleX+largeur/2-randomIntRel(asperites);x++){
                if(y<terrainInitial.length && x<terrainInitial[0].length)
                terrainInitial[y][x]=1;
            }
        }*/

        int[] hautGauche={centreIleX-largeur/2,centreIleY};
        int[] basGauche={centreIleX-largeur/2,centreIleY};
        int[] hautDroite={centreIleX+largeur/2,centreIleY};
        int[] basDroite={centreIleX+largeur/2,centreIleY};

        for(int i=centreIleX-largeur/2;i<centreIleX+largeur/2+1;i++){
            if((Math.random()*montagnes1)>1&&p!=oldp){
                r=(p-oldp)+(int)(Math.random()*2-1);
            }
            else{
                r=(int)((Math.random()*plaine-Math.random()*plaine));
            }
            oldp=p;
            if(p>centreIleY) p-=Math.abs(r);
            if(p<centreIleY-hauteur) p+=Math.abs(r);
            else p+=r;
            while(p<=0) p++;
            terrainInitial[p][i]=1;
            if(i==centreIleX-largeur/2)
                hautGauche[1]=p;
            if(i==centreIleX+largeur/2)
                hautDroite[1]=p;
            for(int j=p;j<centreIleY+1;j++){
                if(j<terrainInitial.length && j>0)
                    terrainInitial[j][i]=1;
            }
        }
        p=(int) centreIleY+hauteur/2-2; //point de départ
        oldp=p;
        for(int i=centreIleX-largeur/2;i<centreIleX+largeur/2+1;i++){
            if((Math.random()*montagnes2)>1&&p!=oldp){
                r=(p-oldp)+(int)(Math.random()*2-1);
            }
            else{
                r=(int)((Math.random()*plaine-Math.random()*plaine));
            }
            oldp=p;
            if(p>centreIleY+hauteur/2) p-=Math.abs(r);
            if(p<centreIleY) p+=Math.abs(r);
            else p+=r;
            while(p<=0) p++;
            terrainInitial[p][i]=1;
            if(i==centreIleX-largeur/2)
                basGauche[1]=p;
            if(i==centreIleX+largeur/2)
                basDroite[1]=p;
            for(int j=p;j>centreIleY;j--){
                if(j<terrainInitial.length && j>0)
                    terrainInitial[j][i]=1;
            }
        }
        int rayonGauche=(basGauche[1]-hautGauche[1])/2;
        int rayonDroite=(basDroite[1]-hautDroite[1])/2;

        for(int i=hautGauche[1];i<=basGauche[1];i++){
            for(int j=hautGauche[0];j>=hautGauche[0]-rayonGauche;j--){
                if(Math.sqrt((i-(hautGauche[1]+basGauche[1])/2)*(i-(hautGauche[1]+basGauche[1])/2)+(j-hautGauche[0])*(j-hautGauche[0]))<=rayonGauche)
                    if(0<i && i<terrainInitial.length && 0<j && j<terrainInitial[0].length)
                        terrainInitial[i][j]=1;
            }
        }
        for(int i=hautDroite[1];i<=basDroite[1];i++){
            for(int j=hautDroite[0];j<=hautDroite[0]+rayonDroite;j++){
                if(Math.sqrt((i-(hautDroite[1]+basDroite[1])/2)*(i-(hautDroite[1]+basDroite[1])/2)+(j-hautDroite[0])*(j-hautDroite[0]))<=rayonDroite)
                    if(0<i && i<terrainInitial.length && 0<j && j<terrainInitial[0].length)
                        terrainInitial[i][j]=1;
            }
        }
    }

    public int[][] getTerrainInitial() {
        return terrainInitial;
    }
    public void check(int y){
        for(int i=0;i<terrainInitial[0].length;i++) terrainInitial[y][i]=1;
    }

    public int surfaceBlock(int xs){
        for(int i=0;i<terrainInitial.length;i++){
            if(terrainInitial[i][xs]==0 && terrainInitial[i+1][xs]==1){
                return i;
            }
        }
        return terrainInitial.length/2;
    }
    public int randomIntRel(double intervale){
        return (int) (Math.random()*intervale-Math.random()*intervale);
    }

    public int getNiveauEau() {
        return niveauEau;
    }

    public void generateSea(){
        for(int i=1;i<terrainInitial[0].length-1;i++){
            for(int j=niveauEau;j<terrainInitial.length-2;j++){
                terrainInitial[j][i] = 2;
            }
        }
    }

    public void generateLimite(int x, int y){
        //Ajout de bloc intraversables invisibles sur les bords de la map
        for(int i=0;i<y;i+=y-1){
            for(int j=0;j<x;j++){
                terrainInitial[i][j] = 3;
                if(i==y-1){
                    terrainInitial[i-1][j] = 3;
                }
                else if(i==0){
                    terrainInitial[i+1][j] = 3;
                }
            }
        }
        for(int j=0;j<x;j+=x-1){
            for(int i=0;i<y;i++){
                terrainInitial[i][j] = 3;
                if(j==x-1){
                    terrainInitial[i][j-1] = 3;
                }
                else if(j==0){
                    terrainInitial[i][j+1] = 3;
                }
            }
        }
    }
}
