import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Optional;

import java.net.*;
import java.io.*;

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
    private Float[] centre = new Float[2];

    private String serverAddress;
    private int serverPort;

    private Color[] colors = {Color.PURPLE, Color.GREEN, Color.BLUE, Color.YELLOW};

    public Client(String serverAddress, int serverPort) {
        this.serverIp = serverAddress;
        this.port = serverPort;
        this.players = new ListShare<>();
        this.projectiles = new ListShare<>();
    }

    public void connect(String serverAddress, int serverPort) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connexion réussie au serveur " + serverAddress + " sur le port " + serverPort);
            // Continuez avec la logique de connexion...
        } catch (ConnectException e) {
            System.err.println("Connexion refusée : le serveur n'est pas disponible.");
            showError("Impossible de se connecter au serveur. Vérifiez que le serveur est en cours d'exécution.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur réseau : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*
    public void connect(Stage primaryStage) {
        toServer = new ThreadClientToHost(primaryStage, serverIp, port, players, projectiles);
        toServer.start();
    }
    */

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

    /*
    private void startClient(String ip, int port) {
        System.out.println("Connexion au serveur " + ip + " sur le port : " + port);
        // Appel à la méthode main de UI (et non pas à Client.main)
        UI.main(new String[]{});
    }
    */

    public void startClient() {
        try {
            // Attendez que la carte soit correctement récupérée
            while (carte == null) {
                System.out.println("Attente de la réception de la carte...");
                Thread.sleep(1000); // Attendez un peu avant de vérifier à nouveau
            }
            
            // Démarrer l'interface graphique après que la carte a été reçue
            UI.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du client : " + e);
            e.printStackTrace();
        }
    }

    private void showHostSetup(Stage primaryStage) {
        // Code pour la configuration de l'hôte
        TextInputDialog portDialog = new TextInputDialog("5001");
        portDialog.setTitle("Configuration de l'Hôte");
        portDialog.setHeaderText("Configurer l'Hôte");
        portDialog.setContentText("Entrez le numéro de port :");

        Optional<String> portResult = portDialog.showAndWait();
        portResult.ifPresent(port -> {
            try {
                int portNumber = Integer.parseInt(port);
                System.out.println("Démarrage de l'hôte sur le port " + portNumber);
                // Insérez la logique pour démarrer l'hôte ici
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le port doit être un nombre valide !");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sélection du mode");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label label = new Label("Choisissez un mode :");
        Button hostButton = new Button("Host");
        Button clientButton = new Button("Client");

        // hostButton.setOnAction(e -> showHostSetup());

        hostButton.setOnAction(e -> showHostSetup(primaryStage));
        clientButton.setOnAction(e -> {
            // Demander les informations IP et port
            TextInputDialog ipDialog = new TextInputDialog("127.0.0.1");
            ipDialog.setTitle("Adresse IP");
            ipDialog.setHeaderText("Entrez l'adresse IP de l'host :");
            ipDialog.setContentText("IP :");

            Optional<String> ipResult = ipDialog.showAndWait();
            ipResult.ifPresent(ip -> {
                TextInputDialog portDialog = new TextInputDialog("5001");
                portDialog.setTitle("Port");
                portDialog.setHeaderText("Entrez le port de l'host :");
                portDialog.setContentText("Port :");

                Optional<String> portResult = portDialog.showAndWait();
                portResult.ifPresent(port -> {
                    try {
                        int portNumber = Integer.parseInt(port);
                        startClient();
                    } catch (NumberFormatException ex) {
                        showAlert("Erreur", "Le port doit être un nombre valide !");
                    }
                });
            });
        });

        root.getChildren().addAll(label, hostButton, clientButton);
        System.out.println("code d'erreur : 13");
        Scene scene = new Scene(root, 300, 200);
        System.out.println("code d'erreur : 12");
        primaryStage.setScene(scene);
        System.out.println("code d'erreur : 11");
        primaryStage.show();
        System.out.println("code d'erreur : 10");
    }

    private void drawObstacles() {
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
        double offsetX = (centreX - (int)centreX) * caseWidth; // Partie fractionnaire * taille d'une case
        double offsetY = (centreY - (int)centreY) * caseHeight;
    
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
                    double drawY = (y - (int)centreY + rayon_display_en_case) * caseHeight - offsetY;
                    gc.fillRect(drawX - sizeWindow / 2, drawY - sizeWindow / 2, caseWidth, caseHeight);
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
                //System.out.println(rond.getX());
                //System.out.println("coord x:" + rond.getX() + " coord y:" + rond.getY() + " coord radius:" + rond.getRadius());
                Color projectileColor = colors[rond.getCouleur()];
                gc.setFill(projectileColor);
    
                // Calcul des coordonnées pour placer correctement le rond en tenant compte du décalage
                double drawX = (rond.getX() - (int) centreX + rayon_display_en_case) * caseWidth - offsetX;
                double drawY = (rond.getY() - (int) centreY + rayon_display_en_case) * caseHeight - offsetY;
    
                // Taille du rond (fixée à une fraction de la case)
                double projectileSize = Math.min(caseWidth, caseHeight) * rond.getRadius() * 2;
    
                // Dessiner le rond
                gc.fillOval(drawX - sizeWindow / 2, drawY - sizeWindow / 2, projectileSize, projectileSize);
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
                gc.fillOval(drawX - sizeWindow / 2, drawY - sizeWindow / 2, projectileSize, projectileSize);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("code d'erreur : 2");
        UI.main(args); // Appel à la méthode main de UI
        // launch(args);
        System.out.println("code d'erreur : 3");
    }
}