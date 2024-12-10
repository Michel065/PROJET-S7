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
    private Hostv2 host;
    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) {
        host = new Hostv2(20, 0.04, 1); // Initialisation de la logique
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
        startAnimation();
    }

    private void startAnimation() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, sizeWindow, sizeWindow); // Effacer l'écran
                drawObstacles();
                drawProjectiles();
                host.update_projectile(); // Mettre à jour les projectiles
                if (host.is_finish()) stop(); // Arrêter l'animation si terminé
            }
        }.start();
    }

    private void drawObstacles() {
        gc.setFill(Color.RED);
        for (Obstacle obstacle : host.getObstacles()) {
            if (obstacle != null) {
                int drawX = (int) ((obstacle.getx() + 0.05) * (sizeWindow / host.map));
                int drawY = (int) ((obstacle.gety() + 0.05) * (sizeWindow / host.map));
                gc.fillRect(drawX, drawY, sizeWindow / host.map * 0.9, sizeWindow / host.map * 0.9);
            }
        }
    }

    private void drawProjectiles() {
        gc.setFill(Color.BLUE);
        for (Projectile projectile : host.getProjectiles()) {
            gc.fillOval(projectile.getX() * (sizeWindow / host.map),
                        projectile.getY() * (sizeWindow / host.map),
                        sizeWindow / host.map * 0.5,
                        sizeWindow / host.map * 0.5);
        }
    }

    public static void main(String[] args) {
        Application.launch(DisplayApp.class, args);
    }
}
