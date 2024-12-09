import java.util.List;

public class Host {
    private Carte carte;
    private List<Projectile> projectiles;
    Host(){
        int map = 20; 
        double pourcentageObstacle = 0.9;
        int nbrMoyenObstacleParCase = 7;// cmieux si c un carré 
        int nbrMaxObstacleParCase = 9;

        // Initialisation de la carte
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase, nbrMaxObstacleParCase);

        // Affichage des informations générales
        carte.printinfo();

        // Création de tous les obstacles initiaux
        System.out.println("\nCréation des obstacles...");
        carte.create_all_initial_obstacle();
        
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
