import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;



public class Host_Graph {
    private Carte carte;
    private List<Projectile> projectiles;
    private long map;
    private double pourcentageObstacle;
    private int nbrMoyenObstacleParCase;
    private BufferedImage obstacleLayer;
    private double caseWidthInOrigin;
    private double caseHeightInOrigin;



    Host_Graph(long map,double pourcentageObstacle,int nbrMoyenObstacleParCase){
        
        this.pourcentageObstacle = pourcentageObstacle;
        this.nbrMoyenObstacleParCase = nbrMoyenObstacleParCase;
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);
        this.map = carte.getTailleReel(); 
        carte.create_all_initial_obstacle();
        createObstacleLayer(750);
    }

    public void genere_projectile(int nbr){
        projectiles=new ArrayList<>((int)nbr);
        Random random = new Random();
        for(int i=0;i<nbr;i++){
            float x,y,speed;
            float dirx,diry;
            x = random.nextFloat() * map;
            y = random.nextFloat() * map;
            while(carte.here_obstacle(x,y)){
                x = random.nextFloat() * map;
                y = random.nextFloat() * map;
            }
            speed = (float)0.2;
            dirx = random.nextInt(nbr);
            diry = random.nextInt(nbr);
            dirx-=nbr/2;
            diry-=nbr/2;
            float norm = (float) Math.sqrt(dirx * dirx + diry * diry);
            if (norm != 0) {
                dirx /= norm;
                diry /= norm;
            }

            projectiles.add(new Projectile(i, speed, x, y, dirx, diry));
        }
    }

    public void update_projectile() {
        List<Projectile> toRemove = new ArrayList<>(); 
        for (Projectile projectile : projectiles) {
            float[] coord = projectile.simu_move();
            if (carte.ca_touche_ou_pas(coord[0], coord[1], (float)0.5)) {
                toRemove.add(projectile);
            } else {
                projectile.move();
            }
        }
        projectiles.removeAll(toRemove);
    }

    public void display(JPanel panel, boolean showGrid) {
        panel.repaint();
    }
    
    

    public void print_projectile(){
        for (Projectile pro : projectiles) {
            System.out.println(pro.toString());
        }
    }

    public boolean is_finish(){
        return projectiles.size()==0;
    }

    public JPanel createDisplayPanel(int window, boolean showGrid) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Dessiner les obstacles depuis le cache
                if (obstacleLayer != null) {
                    g2d.drawImage(obstacleLayer, 0, 0, null);
                }

                for (Projectile projectile : projectiles) {
                    g.setColor(Color.BLUE);
                    int drawX = (int) (projectile.getX() * caseWidthInOrigin);
                    int drawY = (int) (projectile.getY() * caseHeightInOrigin);
                    g.fillOval(drawX, drawY, (int) (caseWidthInOrigin * 0.5), (int) (caseHeightInOrigin * 0.5));
                }
            }
        };
    }

    private void createObstacleLayer(int window) {
        this.caseWidthInOrigin = window / (double) carte.getTailleReel();
        this.caseHeightInOrigin = window / (double) carte.getTailleReel();
        double taille_case_pour = 0.9;
        
        obstacleLayer = new BufferedImage(window, window, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = obstacleLayer.createGraphics();
        g2d.setColor(Color.RED);
    
        List<Obstacle> obstacles = carte.getObstacles();
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null) {
                int drawX = (int) ((obstacle.getx() + 0.05 + 0.04 * (1 - taille_case_pour) * 10) * this.caseWidthInOrigin);
                int drawY = (int) ((obstacle.gety() + 0.05 + 0.04 * (1 - taille_case_pour) * 10) * this.caseHeightInOrigin);
                g2d.fillRect(drawX, drawY, (int) (this.caseWidthInOrigin * taille_case_pour), (int) (this.caseHeightInOrigin * taille_case_pour));
            }
        }
        g2d.dispose();
    }
    
    
    

    public static void main(String[] args) {
        int size_window=750;
        Host_Graph host = new Host_Graph(20,0.04,2);
        host.genere_projectile(50);
        //host.print_projectile();
    
        // Création initiale de la fenêtre et du panneau
        JFrame frame = new JFrame("Carte des Obstacles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(size_window, size_window);
        frame.setLocationRelativeTo(null);
    
        JPanel panel = host.createDisplayPanel(size_window, true);
        frame.add(panel);
        frame.setVisible(true);
    
        // Timer pour actualiser
        Timer timer = new Timer(20, e -> {
            host.update_projectile();
            host.display(panel, true); // Redessine le panneau existant
            if(host.is_finish()) System.exit(0);
        });
    
        timer.start();
    }
    

}
