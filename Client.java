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
    private ListShare<LightRond> players;
    private ListShare<LightRond> projectiles;
    private Float[] centre=new Float[2];

    private Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    public Client() {
        this.serverIp = "127.0.0.1";
        this.port = 5001;
        this.players = new ListShare<>();
        this.projectiles = new ListShare<>();
    }

    public void connect(Stage primaryStage) {
        toServer = new ThreadClientToHost(primaryStage, serverIp, port,players,projectiles);
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
        gc.translate(sizeWindow / 2, sizeWindow / 2);
        gc.rotate(-90);
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
                int val=sizeWindow/2;
                gc.clearRect(-val,-val, sizeWindow, sizeWindow); // Effacer l'écran

                toServer.get_case_centre(centre);

                gc.save();
                float orientation = toServer.get_orientation();
                gc.rotate(-Math.toDegrees(orientation));
                drawObstacles();
                drawProjectiles();
                drawPlayers();

                gc.restore();

                if (Client.is_close) {
                    stop(); 
                    primaryStage.close(); 
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
        int startX = Math.max(0, (int) Math.floor(centreX - rayon_display_en_case-3));
        int startY = Math.max(0, (int) Math.floor(centreY - rayon_display_en_case-3));
        int endX = Math.min((int)carte.getTailleReel(), (int)(centreX + rayon_display_en_case+3));
        int endY = Math.min((int)carte.getTailleReel(), (int)(centreY + rayon_display_en_case+3));
    
        // Dessiner les obstacles visibles dans la zone spécifiée
        gc.setFill(Color.RED);
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (carte.here_obstacle(x, y)) {
                    // Calcul des positions en pixels en appliquant le décalage
                    double drawX = (x - (int)centreX + rayon_display_en_case) * caseWidth - offsetX;
                    double drawY = (y - (int)centreY + rayon_display_en_case) * caseHeight - offsetY;
                    gc.fillRect(drawX-sizeWindow/2, drawY-sizeWindow/2, caseWidth, caseHeight);
                }
            }
        }
    }
    
    
    

    private void drawProjectiles() {
        if (carte == null || gc == null) {
            return;
        }
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / (rayon_display_en_case * 2);
        double caseHeight = caseWidth; // Cases carrées
    
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        float centreX = centre[0];
        float centreY = centre[1];
    
        // Décalage à appliquer pour centrer précisément le joueur
        double offsetX = (centreX - (int) centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int) centreY) * caseHeight;
    
        // Dessiner les LightRond
        for (LightRond rond : projectiles) {
            synchronized (rond) {
                //System.out.println("coord x:" + rond.getX() + " coord y:" + rond.getY() + " coord radius:" + rond.getRadius());
                Color projectileColor = colors[rond.getCouleur()];
                gc.setFill(projectileColor);
    
                // Calcul des coordonnées pour placer correctement le rond en tenant compte du décalage
                double drawX = (rond.getX() - (int) centreX + rayon_display_en_case) * caseWidth - offsetX;
                double drawY = (rond.getY() - (int) centreY + rayon_display_en_case) * caseHeight - offsetY;
    
                // Taille du rond (fixée à une fraction de la case)
                double projectileSize = Math.min(caseWidth, caseHeight) * rond.getRadius() * 2;
    
                // Dessiner le rond
                gc.fillOval(drawX-sizeWindow/2, drawY-sizeWindow/2, projectileSize, projectileSize);
            }
        }
    }

    private void drawPlayers() {
        if (carte == null || gc == null) {
            return;
        }
    
        // Taille de chaque case en fonction de la taille de la fenêtre et de la carte
        double caseWidth = (double) sizeWindow / (rayon_display_en_case * 2);
        double caseHeight = caseWidth; // Cases carrées
    
        // Récupérer les coordonnées exactes (fractionnaires) du centre de la zone à afficher
        float centreX = centre[0];
        float centreY = centre[1];
    
        // Décalage à appliquer pour centrer précisément le joueur
        double offsetX = (centreX - (int) centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int) centreY) * caseHeight;
    
        // Dessiner les LightRond
        for (LightRond rond : players) {
            synchronized (rond) {
                //System.out.println("coord x:" + rond.getX() + " coord y:" + rond.getY() + " coord radius:" + rond.getRadius());
                Color projectileColor = colors[rond.getCouleur()];
                gc.setFill(projectileColor);
    
                // Calcul des coordonnées pour placer correctement le rond en tenant compte du décalage
                double drawX = (rond.getX() - (int) centreX + rayon_display_en_case) * caseWidth - offsetX;
                double drawY = (rond.getY() - (int) centreY + rayon_display_en_case) * caseHeight - offsetY;
    
                // Taille du rond (fixée à une fraction de la case)
                double projectileSize = Math.min(caseWidth, caseHeight) * rond.getRadius() * 2;
    
                // Dessiner le rond
                gc.fillOval(drawX-sizeWindow/2, drawY-sizeWindow/2, projectileSize, projectileSize);
            }
        }
    }
    
    public static void main(String[] args) {

        //Host host = new Host(20, 0.05, 5); // Initialisation de la logique
        //host.start(5001);
        Application.launch(Client.class, args);
    }
}
