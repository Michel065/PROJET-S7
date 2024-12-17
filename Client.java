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
    private int largeur_display_en_case = 10;
    private int rayon_display_en_case = largeur_display_en_case/2;
    private int port;
    private ThreadClientToHost toServer;
    public static boolean is_close = false;
    private Carte carte;
    private ListShare<Projectile> projectiles;
    private ListShare<Player> players;

    public Client() {
        this.serverIp = "127.0.0.1";
        this.port = 5001;
        this.players = new ListShare<>();
        this.projectiles = new ListShare<>();
    }

    public void connect(Stage primaryStage) {
        toServer = new ThreadClientToHost(primaryStage, serverIp, port,projectiles,players);
        toServer.start();
    }

    private void recupCarteFromThread() {
        while (carte == null) {
            try {
                Thread.sleep(100);
                carte = toServer.get_carte();
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }
        }
    }

    // Méthode pour démarrer la logique principale
    public void startClient() {
        recupCarteFromThread();
        if (carte != null) {
            carte.printinfo();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Création de l'interface graphique
        Pane root = new Pane();

        // Initialisation du Canvas et de GraphicsContext
        Canvas canvas = new Canvas(sizeWindow, sizeWindow);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        primaryStage.setTitle("Fenêtre Vide");
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

                drawObstacles();

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
        double caseWidth = (double) sizeWindow / largeur_display_en_case;
        double caseHeight = caseWidth; // Cases carrées
    
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        Float[] centre = toServer.get_case_centre();
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
    
    

    public static void main(String[] args) {
        Application.launch(Client.class, args);
    }
}
