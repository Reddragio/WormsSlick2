public class GestionTerrain {
    private int[][] terrainInitial; //Le terrain tel qu'il est après la génération
    public GestionTerrain() {
    }

    public void genererTerrain(int x, int y, int type){
        int[][] t=new int[y][x];
        if(type == 1){
            //Generation classique
            int p=(int) y/2;
            int r=0; //modification denivelé
            double montagnes=2.2; //coefficient montagnes
            double plaine=2.5; //coefficient plaines
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
                if(terrainInitial[i][j]==1 && terrainInitial[i][j-1]==0)
                    if(j>ybas){
                    xbas=j;
                    ybas=i;
                    }
            }
        }
        int[] res={xbas,ybas};
        return res;
    }

    public void genererFaille(){
        int epaisseur=7; //l'épaisseur de la faille
        int asperites=3; //irrégularités dans la faille
        int epMini=7; //épaisseur minimum de la faille
        int epMaxi=17; //épaisseur maximum de la faille

        int xInit=pointLePlusBas()[0];
        int yInit=pointLePlusBas()[1];
        boolean sens; //Pour savoir si la faille sera vers la gauche ou la droite: 0= gauche, 1= droite
        if(xInit<terrainInitial[0].length/2) sens=true;
        else sens=false;
        for(int I=-epaisseur;I<epaisseur;I++){
            for(int J=-epaisseur;J<epaisseur;J++){
                if(xInit+J>0 && xInit+J<terrainInitial[0].length&& yInit+I>0 && yInit+I<terrainInitial.length){
                    if(Math.abs(J)+Math.abs(I)<epaisseur+2){
                        terrainInitial[yInit+I][xInit+J]=0;
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
                        if(i-xInit==j-yInit-k){
                        terrainInitial[i][j]=0;
                        }
                }
            }
            else{
                for(int j=xInit;j>2;j--){
                    for(int k=-epaisseur;k<epaisseur;k++) if(xInit-i==j-yInit-k) terrainInitial[i][j]=0;
                }
            }
        }
    }

    public int[][] getTerrainInitial() {
        return terrainInitial;
    }
}
