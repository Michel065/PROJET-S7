import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carte {

    private long taille_origine,taille_reel,nbr_obstacle_totale,longeur_list_obstacle;
    private int largeur_case,largeur_matrice,nbr_moyen_obstacle_par_case,nbr_max_obstacle_par_case;
    private double pourcentage_obstacle;

    private MatriceCarre<Tuple> carte;
    private List<Obstacle> obstacles;
    
    public Carte(long taille, double pourcentage_obstacle,int nbr_moyen_obstacle_par_case) {
        this.taille_origine=taille;
        this.nbr_obstacle_totale=(long) Math.ceil(pourcentage_obstacle*taille_origine*taille_origine);
        this.largeur_case=(int) Math.ceil(taille_origine/Math.sqrt(nbr_obstacle_totale/nbr_moyen_obstacle_par_case));
        this.largeur_matrice=(int) Math.ceil((taille_origine+1)/largeur_case);
        this.nbr_moyen_obstacle_par_case = nbr_moyen_obstacle_par_case;
        this.pourcentage_obstacle = pourcentage_obstacle;
        this.taille_reel=(long)(largeur_case*largeur_matrice);
        this.longeur_list_obstacle=largeur_case*largeur_matrice*largeur_matrice;
        this.nbr_max_obstacle_par_case=Math.max(largeur_case*largeur_case,nbr_moyen_obstacle_par_case*2);
        carte=new MatriceCarre<Tuple>(largeur_matrice);
        obstacles = new ArrayList<>((int)longeur_list_obstacle);

        init_liste();
        init_matrice();
        ajoute_bordure();
        
    }

    private void init_liste(){
        for(int i=0;i<nbr_max_obstacle_par_case*largeur_matrice*largeur_matrice;i++){
            obstacles.add(null);
        }
    }

    private void init_matrice(){
        int indice_dans_liste=0;
        for(int x=0;x<largeur_matrice;x++){
            for(int y=0;y<largeur_matrice;y++){
                carte.set(x,y,new Tuple(indice_dans_liste,indice_dans_liste));
                indice_dans_liste+=(largeur_case*largeur_case);
            }
        }
    }

    public void create_all_initial_obstacle(){
        Random random = new Random();
        int nbr_obstacle=(int)(taille_reel*taille_reel*pourcentage_obstacle);
        for(int i=0;i<nbr_obstacle;i++){
            int x = random.nextInt((int)taille_reel);
            int y = random.nextInt((int)taille_reel);
            while(!ajoute_obstacle(x,y)){
                x = random.nextInt((int)taille_reel);
                y = random.nextInt((int)taille_reel);
            }
        }
        
    }

    private boolean Obstacle_existe_deja(int x,int y,Tuple couple){
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.getx()==x && tmp.gety()==y){
                return true;
            }
        }
        return false;
    }


    public boolean ajoute_obstacle(int x,int y){
        Tuple couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille(x,y));
        if(!Obstacle_existe_deja(x,y,couple) && couple.y-couple.x<nbr_max_obstacle_par_case){
            obstacles.set(couple.y, new Obstacle(x, y));
            couple.y++;
            return true;
        }
        return false;
    }

    public void ajoute_bordure(){
        for(int i=0;i<taille_reel;i++){
            ajoute_obstacle(0,i);
            ajoute_obstacle(i,0);
            ajoute_obstacle((int)taille_reel-1,i);
            ajoute_obstacle(i,(int)taille_reel-1);
        }
    }


    public boolean retire_obstacle(int x,int y){
        Tuple couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille(x,y));
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.getx()==x && tmp.gety()==y){
                couple.y--;
                obstacles.remove(i);
                return true;
            }
        }
        return false;
    }

    private float calcul_distance_carre_obstacle(float x, float y, Tuple obstale){
        float xx=Math.abs(x-(obstale.x));
        float yy=Math.abs(y-(obstale.y));
        return xx*xx+yy*yy;
    }

    public boolean ca_touche_ou_pas(float[] coord,float radius){
        Tuple tmp1=get_Coordonnees_De_Reel_Vers_Grille((int)coord[0],(int)coord[1]);
        if(tmp1 == null) return true;
        Tuple couple = carte.get(tmp1);
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            float distance_carre = calcul_distance_carre_obstacle(coord[0],coord[1],tmp.get());
            if(distance_carre<(radius*2)){
                return true;
            }
        }
        return false;
    }

    public boolean here_obstacle(float x,float y){
        Tuple couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille((int)x,(int)y));
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.is_egual((int)x,(int)y)){
                return true;
            }
        }
        return false;
    }


    public void printinfo(){
        System.out.println("____Carte____");
        System.out.println("largeur d'orgine:"+taille_origine);
        System.out.println("nbr moyen d'obstacle par case de la matrice:"+nbr_moyen_obstacle_par_case);
        System.out.println("le fameux taux:"+pourcentage_obstacle);
        System.out.println("nbr d'obstacle calcule:"+nbr_obstacle_totale);
        System.out.println("largeur d'une case de la matrice:"+largeur_case);
        System.out.println("largeur de la matrice:"+largeur_matrice);
        System.out.println("nbr de cases d'origine:"+taille_origine*taille_origine);
        System.out.println("nouveau nbr de case:"+taille_reel*taille_reel);
        System.out.println("soit reel>origine ?:"+(taille_origine*taille_origine>=taille_reel*taille_reel));
        System.out.println("____________");
    }

    public void printliste_obstacle(){
        System.out.println("____liste____");
        System.out.print("[");
        int i=0;
        for(Obstacle obstacle : obstacles){
            if(obstacle!=null) System.out.print(i+",");
            else System.out.print(" ,");
        }
        
        System.out.println("]");
    }


    public void print_nbr_obstacle_par_case_matrice() {
        System.out.println("Nombre d'obstacles par case dans la matrice :");
        for (int x = 0; x < largeur_matrice; x++) {
            for (int y = 0; y < largeur_matrice; y++) {
                Tuple couple = carte.get(x, y);
                int nbr_obstacles = couple.y - couple.x; // Nombre d'obstacles dans cette case
                System.out.print(nbr_obstacles + "\t"); // Affiche avec tabulation
            }
            System.out.println(); // Saut de ligne pour chaque ligne de la matrice
        }
    }
    

    public double getLargeurCase() {
        return largeur_case;
    }

    public Tuple get_Coordonnees_De_Reel_Vers_Grille(int x, int y) {

        int xx=(x / largeur_case),yy=(y / largeur_case);
        if(xx>=largeur_matrice || yy>=largeur_matrice || xx<0 || yy<0) return null;

        return new Tuple(xx,yy);
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }
    
    public long getTailleReel(){
        return taille_reel;
    }
}
