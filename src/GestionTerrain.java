public class GestionTerrain {
    private int[][] terrainInitial; //Le terrain tel qu'il est après la génération
    public GestionTerrain() {
    }

    public void genererTerrain(int x, int y, int type){
        int[][] t=new int[y][x];
        if(type == 1){
            //Generation classique
            int p=(int) y/2; //point de départ
            int r=0;
            double montagnes=3; //coefficient montagnes
            double plaine=2.5; //dénivelé
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
            for(int i=0;i<t[0].length;i++){
                t[t.length-1][i]=2;
                t[t.length-2][i]=2;
                t[t.length-3][i]=2;
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
        //Ajout de bloc intraversables invisibles sur les bords de la map
        for(int i=0;i<y;i+=y-1){
            for(int j=0;j<x;j++){
                t[i][j] = 3;
            }
        }
        for(int j=0;j<x;j+=x-1){
            for(int i=0;i<y;i++){
                t[i][j] = 3;
            }
        }
        terrainInitial=t;
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
        for(int i=terrainInitial.length-2;i<2;i--){
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
        int epaisseur=7; //l'épaisseur de la faille
        int asperites=3; //irrégularités dans la faille
        int epMini=7; //épaisseur minimum de la faille
        int epMaxi=17; //épaisseur maximum de la faille
        int entree=3; //taille de l'embouchure de la faille
        int lastx=50;
        int lasty=50;
        int[] plusbas=pointLePlusBas();


        int xInit=plusbas[0];
        int yInit=plusbas[1];
        boolean sens; //Pour savoir si la faille sera vers la gauche ou la droite: 0= gauche, 1= droite
        if(xInit<terrainInitial[0].length/2) sens=true;
        else sens=false;
        for(int I=-epaisseur-entree;I<epaisseur+entree;I++){
            for(int J=-epaisseur-entree;J<epaisseur+entree;J++){
                if(xInit+J>0 && xInit+J<terrainInitial[0].length&& yInit+I>0 && yInit+I<terrainInitial.length){
                    if(Math.sqrt(I*I+J*J)<epaisseur+entree){
                        terrainInitial[yInit+I-epaisseur][xInit+J]=0;
                    }
                }
            }
        }
        for(int i=yInit;i<terrainInitial.length-Math.random()*40;i++){
            int randomIntRel=(int) (Math.random()*asperites-Math.random()*asperites);
            if(epaisseur+randomIntRel>epMini&&epaisseur+randomIntRel<epMaxi) epaisseur+=randomIntRel;
            if(sens){
                for(int j=xInit;j<terrainInitial[0].length-xInit-2;j++){
                    for(int k=-epaisseur;k<epaisseur;k++)
                        if(j-xInit==i-yInit-k){
                        terrainInitial[i][j]=0;
                        lastx=j;
                        lasty=i;
                        }
                }
            }
            else{
                for(int j=xInit;j>2;j--){
                    for(int k=-epaisseur;k<epaisseur;k++)
                        if(xInit-j==i-yInit-k){
                        terrainInitial[i][j]=0;
                            lastx=j;
                            lasty=i;
                        }
                }
            }
            lastx=(int) (lastx-epaisseur/2);
            for(int k=0;k<epaisseur;k++){
                for(int l=0;l<epaisseur/2;l++){
                    if(lasty+l<terrainInitial.length&&lastx +k-1<terrainInitial[0].length) {
                        terrainInitial[lasty + l][lastx + k - l] = 0;
                        terrainInitial[lasty + l][lastx - k + l] = 0;
                    }
                }
            }
        }
    }

    public void genererIles(){
        int largeur=60;
        int hauteur=30;
        int asperites=3;
        int difY=3;
        int difX=2;
        int centreIleY=(int) (Math.random()*pointLePlusHaut()[1]);
        while(centreIleY-hauteur<=0)
            centreIleY=(int) (Math.random()*pointLePlusHaut()[1]);
        int centreIleX=pointLePlusBas()[0];
        for(int y=centreIleY-hauteur/2;y<centreIleY+hauteur/2;y++){
            for(int x=centreIleX-largeur/2;x<centreIleX+largeur/2;x++){
                if(y<terrainInitial.length && x<terrainInitial[0].length)
                terrainInitial[y][x]=1;
            }
        }
    }

    public int[][] getTerrainInitial() {
        return terrainInitial;
    }
}
