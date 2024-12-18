import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class DisplayApp extends Application {
    private final int sizeWindow = 750;
    private Host host;
    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) {
        host = new Host(30, 0.05, 5); // Initialisation de la logique
        host.start(5001);
        //host.start(primaryStage);
        // Création de l'interface graphique
        Pane root = new Pane();
        canvas = new Canvas(sizeWindow, sizeWindow);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        primaryStage.setTitle("Carte des Obstacles");
        primaryStage.setScene(new Scene(root, sizeWindow, sizeWindow));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("La fermeture ...");
            Host.is_close=true;
        });

        startAnimation(primaryStage);
    }

    private void startAnimation(Stage primaryStage) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, sizeWindow, sizeWindow); // Effacer l'écran

                drawObstacles();
                drawProjectiles();
                drawPlayers();
                /*
                if (host.is_finish()) {
                    host.genere_x_new_projectile(50);
                    stop(); // Arrêter l'animation
                    primaryStage.close(); // Fermer la fenêtre
                }*/
            }
        }.start();
    }

    private void drawObstacles() {
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / host.map;
        double caseHeight = (double) sizeWindow / host.map;
    
        // Dessiner la bordure
        gc.setFill(Color.RED);
        for (int x = 0; x < host.map; x++) {
            for (int y = 0; y < host.map; y++) {
                // Si c'est une bordure (cases sur les bords)
                if (x == 0 || y == 0 || x == host.map - 1 || y == host.map - 1) {
                    double drawX = x * caseWidth;
                    double drawY = y * caseHeight;
                    gc.fillRect(drawX, drawY, caseWidth, caseHeight);
                }
            }
        }
    
        // Dessiner les obstacles dans le reste de la grille
        gc.setFill(Color.RED);
        for (Obstacle obstacle : host.getObstacles()) {
            if (obstacle != null) {
                double drawX = obstacle.getx() * caseWidth;
                double drawY = obstacle.gety() * caseHeight;
                gc.fillRect(drawX, drawY, caseWidth, caseHeight);
            }
        }
    }
    
    

    private void drawProjectiles() {
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / host.map;
        double caseHeight = (double) sizeWindow / host.map;
        
        ListShare<Projectile> li=host.getProjectiles();
        if(li != null){
            //System.out.println("la taille:" + li.size());
            for (Projectile projectile : li) {
                synchronized(projectile){
                    
                    Color projectileColor = projectile.getCouleur(); 
                    gc.setFill(projectileColor);
                    // Calcul des coordonnées pour placer correctement les projectiles
                    double drawX = projectile.getX() * caseWidth;
                    double drawY = projectile.getY() * caseHeight;
            
                    // Taille des projectiles (fixée à une fraction de la case)
                    double projectileSize = Math.min(caseWidth, caseHeight) * projectile.getRadius()*2;
            
                    // Dessiner les projectiles
                    gc.fillOval(drawX, drawY, projectileSize, projectileSize);
                }
            }
        }
    }

    private void drawPlayers() {
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / host.map;
        double caseHeight = (double) sizeWindow / host.map;
        ListShare<Player> li = host.getPlayers();
        if (li != null) {
            for (Player player : li) {
                synchronized (player) {
                    // Couleur et dessin du joueur
                    Color playerColor = player.getCouleur(); 
                    gc.setFill(playerColor);
    
                    double drawX = player.getX() * caseWidth;
                    double drawY = player.getY() * caseHeight;
    
                    double playerSize = Math.min(caseWidth, caseHeight) * player.getRadius() * 2;
    
                    // Dessiner le joueur
                    gc.fillOval(drawX, drawY, playerSize, playerSize);
    
                    // Dessiner la barre de vie au-dessus du joueur
                    double healthPercentage = player.getHealth() / 100.0; // Assumes health is out of 100
                    double barWidth = playerSize * healthPercentage;
                    double barHeight = playerSize * 0.2; // 20% of player size for the height
    
                    gc.setFill(Color.RED); // Background color for health bar
                    gc.fillRect(drawX, drawY - barHeight - 2, playerSize, barHeight);
    
                    gc.setFill(Color.GREEN); // Foreground color for health
                    gc.fillRect(drawX, drawY - barHeight - 2, barWidth, barHeight);
                }
            }
        }
    }
    
    

    public static void main(String[] args) {
        Application.launch(DisplayApp.class, args);
    }
}
