import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import java.awt.*;



public class Host {
    private Carte carte;
    private List<Projectile> projectiles;
    private long map;
    private double pourcentageObstacle;
    private int nbrMoyenObstacleParCase;

    Host(){
        map = 20; 
        pourcentageObstacle = 0.15;
        nbrMoyenObstacleParCase = 5;

        // Initialisation de la carte
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);

        
        // Affichage des informations générales
        carte.printinfo();

        // Création de tous les obstacles initiaux
        System.out.println("\nCréation des obstacles...");
        carte.create_all_initial_obstacle();
        carte.print_nbr_obstacle_par_case_matrice();
        
    }

    public void genere_projectile(int nbr){
        projectiles=new ArrayList<>((int)nbr);
        Random random = new Random();
        for(int i=0;i<nbr;i++){
            float x,y,speed;
            int dirx,diry;
            x = random.nextFloat() * map;
            y = random.nextFloat() * map;
            speed = random.nextFloat() * 5;
            dirx = random.nextInt(nbr);
            diry = random.nextInt(nbr);
            float norm = (float) Math.sqrt(dirx * dirx + diry * diry);
            if (norm != 0) {
                dirx /= norm;
                diry /= norm;
            }
            projectiles.add(new Projectile(i, speed, x, y, dirx, diry));
        }
    }

    public void update_projectile(){

    }

    public void print_projectile(){
        for (Projectile pro : projectiles) {
            System.out.println(pro.toString());

        }
    }

    public static void main(String[] args) {
        Host host = new Host();
        host.genere_projectile(10);
        host.print_projectile();

}
