public class Host {
    Host(){}
    
    public static void main(String[] args) {
        int map = 40; 
        double pourcentageObstacle = 0.20;
        int nbrMoyenObstacleParCase = 7;
        int nbrMaxObstacleParCase = 100;

        // Initialisation de la carte
        Carte carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase, nbrMaxObstacleParCase);

        // Affichage des informations générales
        carte.printinfo();

        // Création de tous les obstacles initiaux
        System.out.println("\nCréation des obstacles...");
        carte.create_all_initial_obstacle();
        carte.print_nbr_obstacle_par_case_matrice();

        // Affichage graphique
        carte.display(750, 750,true);
    }
}
