import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Client extends Application {
    private final int sizeWindow = 750;
    private GraphicsContext gc;

    private String serverIp;
    private int rayon_display_en_case = 5;
    private int port;
    private ThreadClientToHost toServer;
    public static boolean is_close = false;
    private Carte carte;
    private ListShare<LightRond> Rond;
    private Float[] centre;

    public Client() {
        this.serverIp = "127.0.0.1";
        this.port = 5001;
        this.Rond = new ListShare<>();
    }

    public void connect(Stage primaryStage) {
        toServer = new ThreadClientToHost(primaryStage, serverIp, port,Rond);
        toServer.start();
    }

    private void recupCarteFromThread() {
        while (carte == null) {
            try {
                Thread.sleep(100);
                carte = toServer.get_carte();
                rayon_display_en_case = toServer.get_rayon_display_en_case();
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }
        }
    }

    // Méthode pour démarrer la logique principale
    public void startClient() {
        recupCarteFromThread();
        
    }

    @Override
    public void start(Stage primaryStage) {
        // Création de l'interface graphique
        Pane root = new Pane();

        // Initialisation du Canvas et de GraphicsContext
        Canvas canvas = new Canvas(sizeWindow, sizeWindow);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        primaryStage.setTitle("Fenêtre Client");
        primaryStage.setScene(new Scene(root, sizeWindow, sizeWindow));
        primaryStage.show();

        connect(primaryStage);

        // Gestion de la fermeture
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("La fermeture ...");
            is_close = true;
        });

        startClient();

        startAnimation(primaryStage);
    }

    private void startAnimation(Stage primaryStage) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, sizeWindow, sizeWindow); // Effacer l'écran

                centre = toServer.get_case_centre();
                drawObstacles();
                drawRond();
                //drawProjectiles();
                //drawPlayers();

                if (Client.is_close) {
                    stop(); // Arrêter l'animation
                    primaryStage.close(); // Fermer la fenêtre
                }
            }
        }.start();
    }

    private void drawObstacles() {
        if (carte == null || gc == null) {
            return;
        }
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / (rayon_display_en_case*2);
        double caseHeight = caseWidth; // Cases carrées
    
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        float centreX = centre[0];
        float centreY = centre[1];
    
        // Décalage à appliquer pour centrer précisément le joueur
        double offsetX = (centreX - (int)centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int)centreY) * caseHeight;
    
        // Calcul de la zone visible autour du centre
        int startX = Math.max(0, (int) Math.floor(centreX - rayon_display_en_case));
        int startY = Math.max(0, (int) Math.floor(centreY - rayon_display_en_case));
        int endX = Math.min((int)carte.getTailleReel(), (int)(centreX + rayon_display_en_case+1));
        int endY = Math.min((int)carte.getTailleReel(), (int)(centreY + rayon_display_en_case+1));
    
        // Dessiner les obstacles visibles dans la zone spécifiée
        gc.setFill(Color.RED);
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (carte.here_obstacle(x, y)) {
                    // Calcul des positions en pixels en appliquant le décalage
                    double drawX = (x - (int)centreX + rayon_display_en_case) * caseWidth - offsetX;
                    double drawY = (y - (int)centreY + rayon_display_en_case) * caseHeight - offsetY;
                    gc.fillRect(drawX, drawY, caseWidth, caseHeight);
                }
            }
        }
    }
    

    private void drawRond() {
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / (rayon_display_en_case*2);
        double caseHeight = (double) sizeWindow / (rayon_display_en_case*2);

        for (LightRond rond : Rond) {
            synchronized(rond){
                Color projectileColor = rond.getCouleur(); 
                gc.setFill(projectileColor);
                // Calcul des coordonnées pour placer correctement les projectiles
                double drawX = rond.getX() * caseWidth;
                double drawY = rond.getY() * caseHeight;
        
                // Taille des projectiles (fixée à une fraction de la case)
                double projectileSize = Math.min(caseWidth, caseHeight) * rond.getRadius()*2;
        
                // Dessiner les projectiles
                gc.fillOval(drawX, drawY, projectileSize, projectileSize);
            }
        }
    }


    /*private void drawProjectiles() {
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / (rayon_display_en_case*2);
        double caseHeight = (double) sizeWindow / (rayon_display_en_case*2);

        for (Projectile projectile : projectiles) {
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

    private void drawPlayers() {
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / (rayon_display_en_case*2);
        double caseHeight = (double) sizeWindow / (rayon_display_en_case*2);
        for (Player player : players) {
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
    }*/



    public static void main(String[] args) {
        Application.launch(Client.class, args);
    }
}
