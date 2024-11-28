import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carte {
    private int taille,taille_reel,nbr_max_obstacle_par_case;
    private double pourcentage_obstacle,ratio_convertion;
    private MatriceCarre<Tuple> carte;
    private List<Obstacle> obstacles;
    
    public Carte(int taille_reel, double pourcentage_obstacle,int nbr_moyen_obstacle_par_case,int nbr_max_obstacle_par_case) {
        this.taille_reel=taille_reel;
        this.taille = (int) Math.ceil(Math.sqrt((taille_reel * taille_reel * pourcentage_obstacle) / nbr_moyen_obstacle_par_case));

        this.nbr_max_obstacle_par_case=nbr_max_obstacle_par_case;

        this.pourcentage_obstacle = pourcentage_obstacle;
        ratio_convertion = (double) this.taille_reel / this.taille;

        carte=new MatriceCarre<Tuple>(taille);

        obstacles = new ArrayList<>(nbr_max_obstacle_par_case*taille*taille);
    }

    public void create_all_initial_obstacle(){
        Random random = new Random();
        int nbr_obstacle=(int)(taille_reel*taille_reel*pourcentage_obstacle);
        for(int i=0;i<nbr_obstacle;i++){
            int x = random.nextInt(taille_reel);
            int y = random.nextInt(taille_reel);
            while(!ajoute_obstacle(x,y)){
                x = random.nextInt(taille_reel);
                y = random.nextInt(taille_reel);
            }
        }
    }

    private boolean ajoute_obstacle(int x,int y){
        return true;
    }

    public void printinfo(){
        System.out.println("____Carte____");
        System.out.println("taille d'origine:"+taille_reel);
        System.out.println("taille Mat:"+taille);
        System.out.println("largeur case:"+ (int)Math.ceil(getLargeurCase()));

        System.out.println("nbr Obstacle par case:"+nbr_max_obstacle_par_case);
    }

    public double getLargeurCase() {
        return ratio_convertion=(double) taille_reel / taille;
    }

    public Tuple get_Coordonnees_De_Reel_Vers_Grille(int x, int y) {
        return new Tuple((int) Math.floor(x / ratio_convertion),(int) Math.floor(y / ratio_convertion));
    }
    
    // Méthode principale
    public static void main(String[] args) {
        int map = 20; 
        Carte carte = new Carte(map, 0.1,5,5); 
        carte.printinfo();
        

    }
}
