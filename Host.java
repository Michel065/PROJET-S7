import java.util.List;

public class Host {
    private Carte carte;
    private List<Projectile> projectiles;

    Host(){
        long map = 100; 
        double pourcentageObstacle = 0.15;
        int nbrMoyenObstacleParCase = 5;

        // Initialisation de la carte
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);

        
        // Affichage des informations générales
        carte.printinfo();

        // Création de tous les obstacles initiaux
        System.out.println("\nCréation des obstacles...");
        carte.create_all_initial_obstacle();
        carte.print_nbr_obstacle_par_case_matrice();
        
    }

    public void start(){
        // Affichage graphique
        carte.display(750, 750,true);
    }

    public static void main(String[] args) {
        Host host=new Host();
        host.start();
    }
}
