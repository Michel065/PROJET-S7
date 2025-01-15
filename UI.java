import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.*;
import java.util.*;

import javafx.application.Platform;

public class UI extends Application {

    private Stage stage;
    private String[] args = new String[0];

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage; // Initialisation du stage
        showRoleSelection();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showRoleSelection() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label label = new Label("Choisissez votre rôle :");
        Button hostButton = new Button("Host");
        Button clientButton = new Button("Client");

        hostButton.setOnAction(e -> showHostSetup());
        clientButton.setOnAction(e -> showClientSetup());

        root.getChildren().addAll(label, hostButton, clientButton);

        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene); // Utilisation correcte du stage
        stage.setTitle("Sélection du rôle"); // Ajout d'un titre pour la fenêtre
        stage.show();
    }

    private void showClientSetup() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label ipLabel = new Label("Adresse IP de l'host :");
        TextField ipField = new TextField();
        Label portLabel = new Label("Port de l'host :");
        TextField portField = new TextField();
        Button connectButton = new Button("Se connecter");

        connectButton.setOnAction(e -> {
            String ip = ipField.getText();
            String port = portField.getText();
            if (!ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") || !port.matches("\\d+")) {
                showError("Adresse IP ou port invalide. Veuillez vérifier les informations.");
                return;
            }
            startClient(ip, Integer.parseInt(port));
        });

        root.getChildren().addAll(ipLabel, ipField, portLabel, portField, connectButton);

        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
    }

    private void showHostSetup() {
        // Récupérer l'adresse IP locale
        String localIp = getLocalIPAddress();

        // Créer un TextInputDialog pour le port
        TextInputDialog portDialog = new TextInputDialog("5001");
        portDialog.setTitle("Configuration de l'Hôte");
        portDialog.setHeaderText("Configurer l'Hôte");

        // Ajouter l'adresse IP locale dans le message
        portDialog.setContentText("Entrez le numéro de port : (Votre adresse IP est : " + localIp + ")");

        Optional<String> portResult = portDialog.showAndWait();
        portResult.ifPresent(port -> {
            try {
                int portNumber = Integer.parseInt(port);
                System.out.println("Démarrage de l'hôte sur le port " + portNumber);

                // Lancer l'hôte dans un thread séparé
                new Thread(() -> startHost(portNumber)).start();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Le port doit être un nombre valide !");
            }
        });
    }

    private void startHost(int port) {
        try {
            Host server = new Host(10, 20, 0.05, 5);
            server.start(port);

            System.out.println("L'hôte est maintenant en écoute sur le port : " + port);

            // Affiche un message à l'utilisateur et ferme la fenêtre principale
            Platform.runLater(() -> showAlert("Hôte démarré", "L'hôte écoute sur le port " + port + "."));
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Erreur", "Impossible de démarrer l'hôte sur le port " + port + ". Le port est-il déjà utilisé ?"));
        }
    }

    private void startClient(String serverAddress, int serverPort) {
        try {
            // Créez une nouvelle instance de Client manuellement
            Client clientApp = new Client(serverAddress, serverPort);
            Stage clientStage = new Stage();
            clientApp.start(clientStage);

            // Ferme la fenêtre actuelle une fois que la connexion est réussie
            Platform.runLater(() -> stage.close());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la connexion au serveur : " + e.getMessage());
        }
    }

    private String getLocalIPAddress() {
        try {
            // Liste toutes les interfaces réseau de la machine
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Vérifie que l'interface est active et non une interface loopback
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    // Liste les adresses IP associées à l'interface
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        // Si l'adresse est une adresse IPv4 (parce qu'IPv6 peut aussi être récupéré)
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Erreur lors de la récupération de l'adresse IP";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Ajouter un événement de fermeture
            alert.setOnHidden(e -> {
                stage.close(); // Ferme la fenêtre principale
            });

            alert.showAndWait();
        });
    }
}