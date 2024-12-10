import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import java.awt.*;



public class Host_Graph {
    private Carte carte;
    private List<Projectile> projectiles;
    private long map;
    private double pourcentageObstacle;
    private int nbrMoyenObstacleParCase;

    Host_Graph(){
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

    public void display(JPanel panel, boolean showGrid) {
        panel.repaint(); // Rafraîchit uniquement le panneau
    }
    
    

    public void print_projectile(){
        for (Projectile pro : projectiles) {
            System.out.println(pro.toString());

        }
    }

    public JPanel createDisplayPanel(int window, boolean showGrid) {
        List<Obstacle> obstacles = carte.getObstacles();
        int tailleReel = (int) carte.getTailleReel();
    
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
    
                double caseWidthInOrigin = window / (double) tailleReel;
                double caseHeightInOrigin = window / (double) tailleReel;
                double taille_case_pour = 0.9;
    
                // Dessiner les obstacles
                for (Obstacle obstacle : obstacles) {
                    if (obstacle != null) {
                        g.setColor(Color.RED);
                        int drawX = (int) ((obstacle.getx() + 0.05 + 0.04 * (1 - taille_case_pour) * 10) * caseWidthInOrigin);
                        int drawY = (int) ((obstacle.gety() + 0.05 + 0.04 * (1 - taille_case_pour) * 10) * caseHeightInOrigin);
                        g.fillRect(drawX, drawY, (int) (caseWidthInOrigin * taille_case_pour), (int) (caseHeightInOrigin * taille_case_pour));
                    }
                }
    
                // Dessiner les projectiles
                for (Projectile projectile : projectiles) {
                    g.setColor(Color.BLUE);
                    int drawX = (int) (projectile.getX() * caseWidthInOrigin);
                    int drawY = (int) (projectile.getY() * caseHeightInOrigin);
                    g.fillOval(drawX, drawY, (int) (caseWidthInOrigin * 0.5), (int) (caseHeightInOrigin * 0.5));
                }
            }
        };
    }
    

    public static void main(String[] args) {
        Host_Graph host = new Host_Graph();
        host.genere_projectile(10);
        host.print_projectile();
    
        // Création initiale de la fenêtre et du panneau
        JFrame frame = new JFrame("Carte des Obstacles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 750);
        frame.setLocationRelativeTo(null);
    
        JPanel panel = host.createDisplayPanel(750, true);
        frame.add(panel);
        frame.setVisible(true);
    
        // Timer pour actualiser
        Timer timer = new Timer(100, e -> {
            host.update_projectile(); // Mise à jour des projectiles
            host.display(panel, true); // Redessine le panneau existant
        });
    
        timer.start();
    }
    

}
