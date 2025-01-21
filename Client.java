import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.animation.AnimationTimer;

import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Client extends Application {
    private final int sizeWindow = 700;
    private GraphicsContext gc;

    private String serverIp;
    private int rayon_display_en_case = 5;
    private int port;
    private ThreadClientToHost toServer;
    public static boolean is_close = false;
    private Carte carte;

    private ConcurrentLinkedQueue<LightPlayer> players = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<LightProjectile> projectiles = new ConcurrentLinkedQueue<>();
    private Float[] centre = new Float[2];

    private Color[] colors = {Color.PURPLE, Color.GREEN, Color.BLUE, Color.YELLOW};

    private double caseWidth;
    public static int nbr_fenetre_respawn_ouvert = 0;

    public Client() {
        this.serverIp = "192.168.129.237";
        this.port = 5003;
        System.out.println("Démarrage : " + serverIp + " \\" + port);
    }

    public Client(String serverAddress, int serverPort) {
        this.serverIp = serverAddress;
        this.port = serverPort;
    }

    public void connect(Stage primaryStage) {
        toServer = new ThreadClientToHost(primaryStage, serverIp, port, players, projectiles);
        toServer.start();
    }

    private void recupCarteFromThread() {
        while (carte == null) {
            try {
                Thread.sleep(100);
                carte = toServer.get_carte();
                rayon_display_en_case = toServer.get_rayon_display_en_case();
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu");
            }
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        // Création de l'interface graphique
        Pane root = new Pane();
    
        // Initialisation du Canvas et du GraphicsContext
        Canvas canvas = new Canvas(sizeWindow, sizeWindow);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
    
        primaryStage.setTitle("Fenêtre Client");
        primaryStage.setScene(new Scene(root, sizeWindow, sizeWindow));
        primaryStage.show();
    
        connect(primaryStage);
    
        // Gestion de la fermeture
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture du client");
            is_close = true;
        });
    
        // Démarrage du client et de l'animation
        recupCarteFromThread();
        startAnimation(primaryStage);
    }

    private void startAnimation(Stage primaryStage) {
        new AnimationTimer() {
            int val = sizeWindow / 2;
            @Override
            public void handle(long now) {
                gc.setTransform(1, 0, 0, 1, val, val); 
                gc.save();
                gc.clearRect(-val, -val, sizeWindow, sizeWindow);
                toServer.get_case_centre(centre);
                double orientation = toServer.get_orientation();
                gc.rotate(-90 - Math.toDegrees(orientation));
                caseWidth = (double) sizeWindow / (rayon_display_en_case * 2);

                /*
                // Ajouter les axes en couleur (débug))
                double arrowWidth = caseWidth;
                double arrowHeight = caseWidth / 4;

                gc.setFill(Color.GREEN); 
                gc.fillRect(-arrowWidth, -arrowHeight / 2, arrowWidth, arrowHeight);

                gc.setFill(Color.BLUE); 
                gc.fillRect(-arrowHeight / 2, -arrowWidth, arrowHeight, arrowWidth);
                */    

                drawObstacles();
                drawProjectiles();
                drawPlayers();
                gc.restore();

                if (Client.is_close) {
                    stop(); 
                    primaryStage.close(); 
                }
                if(!ThreadClientToHost.player_status && nbr_fenetre_respawn_ouvert == 0) {
                    open_respawn_window();
                    System.out.println("creation fenetre respawn:");
                    nbr_fenetre_respawn_ouvert++;
                }
            }
        }.start();
    }

    private void drawObstacles() {
        if (carte == null || gc == null) {
            return;
        }
    
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        float centreX = centre[0];
        float centreY = centre[1];
    
        // Décalage à appliquer pour centrer précisément le joueur
        double offsetX = (centreX - (int)centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int)centreY) * caseWidth;
    
        // Calcul de la zone visible autour du centre
        int startX = Math.max(0, (int) Math.floor(centreX - rayon_display_en_case - 3));
        int startY = Math.max(0, (int) Math.floor(centreY - rayon_display_en_case - 3));
        int endX = Math.min((int)carte.getTailleReel(), (int)(centreX + rayon_display_en_case + 3));
        int endY = Math.min((int)carte.getTailleReel(), (int)(centreY + rayon_display_en_case + 3));
    
        // Dessiner les obstacles visibles dans la zone spécifiée
        gc.setFill(Color.RED);
        for (int x=startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (carte.here_obstacle(x, y)) {
                    // Calcul des positions en pixels en appliquant le décalage
                    double drawX = (x - (int)centreX + rayon_display_en_case) * caseWidth - offsetX;
                    double drawY = (y - (int)centreY + rayon_display_en_case) * caseWidth - offsetY;
                    gc.fillRect(drawX - sizeWindow / 2, drawY - sizeWindow / 2, caseWidth, caseWidth);
                }
            }
        }
    }

    private void drawProjectiles() {
        if (carte == null || gc == null) {
            return;
        }
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        float centreX = centre[0];
        float centreY = centre[1];
    
        // Décalage à appliquer pour centrer précisément le joueur
        double offsetX = (centreX - (int) centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int) centreY) * caseWidth;
    
        // Dessiner les LightRond
        for (LightProjectile proj : projectiles) {
            synchronized (proj) {
                //System.out.println(rond.getX());
                //System.out.println("coord x:" + rond.getX() + " coord y:" + rond.getY() + " coord radius:" + rond.getRadius());
                Color projectileColor = colors[proj.getEquipe()];
                gc.setFill(projectileColor);
    
                // Calcul des coordonnées pour placer correctement le rond en tenant compte du décalage
                double drawX = (proj.getX() - (int) centreX + rayon_display_en_case) * caseWidth - offsetX;
                double drawY = (proj.getY() - (int) centreY + rayon_display_en_case) * caseWidth - offsetY;
    
                // Taille du rond (fixée à une fraction de la case)
                double projectileSize = Math.min(caseWidth, caseWidth) * proj.getRadius() * 2;
    
                // Dessiner le rond
                gc.fillOval(drawX - sizeWindow / 2, drawY - sizeWindow / 2, projectileSize, projectileSize);
            }
        }
    }

    private void drawPlayers() {
        if (carte == null || gc == null) {
            return;
        }
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        float centreX = centre[0];
        float centreY = centre[1];
    
        // Décalage à appliquer pour centrer précisément le joueur
        double offsetX = (centreX - (int) centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int) centreY) * caseWidth;
        
        Color playerColor;
        // Dessiner les LightRond
        for (LightPlayer player : players) {
            synchronized (player) {
                //System.out.println("coord x:" + rond.getX() + " coord y:" + rond.getY() + " coord radius:" + rond.getRadius());
                if(player.getEquipe()==-1)
                    playerColor = Color.WHITE;
                else
                    playerColor = colors[player.getEquipe()];
                gc.setFill(playerColor);
                
                // Calcul des coordonnées pour placer correctement le rond en tenant compte du décalage
                double drawX = (player.getX() - (int) centreX + rayon_display_en_case) * caseWidth - offsetX;
                double drawY = (player.getY() - (int) centreY + rayon_display_en_case) * caseWidth - offsetY;
                
                // Taille du rond (fixée à une fraction de la case)
                double projectileSize = Math.min(caseWidth, caseWidth) * player.getRadius() * 2;
    
                // Dessiner le rond
                gc.fillOval(drawX - sizeWindow / 2, drawY - sizeWindow / 2, projectileSize, projectileSize);
                
                drawX=drawX- sizeWindow / 2+ player.getRadius()*caseWidth;
                drawY=drawY- sizeWindow / 2+ player.getRadius()*caseWidth;
                one_create_life_bar(drawX,drawY,player.get_vie_pourcentage());

            }
        }
    }

    public void one_create_life_bar(double centerX, double centerY, float pourcentage) {
        if (gc == null) {
            return;
        }
        double largeur_blanc = 0.9 * (1 - pourcentage) * caseWidth;
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX-largeur_blanc/2, centerY-largeur_blanc/2, largeur_blanc, largeur_blanc);
    }
    
    public static void main(String[] args) {
        Application.launch(Client.class, args);
    }

    public void open_respawn_window() {
        Platform.runLater(() -> {
            Stage respawnStage = new Stage();
            respawnStage.setTitle("Vous êtes mort !");

            Label label = new Label("Revenez dans la bataille !");
            Button btnRespawn = new Button("Respawn");
            Button btnFF = new Button("Abandonner");

            btnRespawn.setOnAction(event -> {
                respawnStage.close();
                toServer.respawn_player();
            });

            btnFF.setOnAction(event -> {
                respawnStage.close();
                Client.is_close = true;
            });

            // Gestion de la fermeture
            primaryStage.setOnCloseRequest(event -> {
                Client.is_close = true;
            });

            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            layout.getChildren().addAll(label, btnRespawn, btnFF);

            Scene scene = new Scene(layout, 300, 200);
            respawnStage.setScene(scene);

            respawnStage.show();
        });
    }
}
