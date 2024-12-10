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
        host = new Host(50, 0.01, 15); // Initialisation de la logique
        host.genere_projectile(50);

        // Création de l'interface graphique
        Pane root = new Pane();
        canvas = new Canvas(sizeWindow, sizeWindow);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        primaryStage.setTitle("Carte des Obstacles");
        primaryStage.setScene(new Scene(root, sizeWindow, sizeWindow));
        primaryStage.show();

        // Lancer la boucle d'animation
        startAnimation(primaryStage);
    }

    private void startAnimation(Stage primaryStage) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, sizeWindow, sizeWindow); // Effacer l'écran
                drawObstacles();
                drawProjectiles();
                host.update_projectile(); // Mettre à jour les projectiles
                if (host.is_finish()) {
                    host.genere_x_new_projectile(50);
                    /*stop(); // Arrêter l'animation
                    primaryStage.close(); // Fermer la fenêtre*/
                }
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
        gc.setFill(Color.BLUE);
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / host.map;
        double caseHeight = (double) sizeWindow / host.map;
    
        for (Projectile projectile : host.getProjectiles()) {
            // Calcul des coordonnées pour placer correctement les projectiles
            double drawX = projectile.getX() * caseWidth;
            double drawY = projectile.getY() * caseHeight;
    
            // Taille des projectiles (fixée à une fraction de la case)
            double projectileSize = Math.min(caseWidth, caseHeight) * 0.5;
    
            // Dessiner les projectiles
            gc.fillOval(drawX, drawY, projectileSize, projectileSize);
        }
    }
    

    public static void main(String[] args) {
        Application.launch(DisplayApp.class, args);
    }
}
