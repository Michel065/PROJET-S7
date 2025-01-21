import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.net.*;
import java.util.*;
import javafx.application.Platform;

public class UI extends Application {

    private Stage stage;

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

        Scene scene = new Scene(root, 300, 400);
        stage.setScene(scene);
        stage.setTitle("Sélection du rôle"); // Ajout d'un titre pour la fenêtre
        stage.show();
    }

    private void showClientSetup() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label ipLabel = new Label("Adresse IP de l'host :");
        TextField ipField = new TextField("127.0.0.1");
        Label portLabel = new Label("Port de l'host :");
        TextField portField = new TextField("5003");
        Button connectButton = new Button("Se connecter");
        Button cancelButton = new Button("Annuler");

        connectButton.setOnAction(e -> {
            String ip = ipField.getText();
            String port = portField.getText();
            if (!ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") || !port.matches("\\d+")) {
                showError("Adresse IP ou port invalide. Veuillez vérifier les informations.");
                return;
            }
            startClient(ip, Integer.parseInt(port));
        });

        cancelButton.setOnAction(e -> showRoleSelection());

        root.getChildren().addAll(ipLabel, ipField, portLabel, portField, connectButton, cancelButton);

        Scene scene = new Scene(root, 300, 400);
        stage.setTitle("Client setup");
        stage.setScene(scene);
    }

    private void showHostSetup() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label portLabel = new Label("Entrez le numéro de port :");
        TextField portField = new TextField("5003");
        Label maxPlayersLabel = new Label("Nombre maximum de joueurs :");
        TextField maxPlayersField = new TextField("10");
        Label mapLabel = new Label("Taille de la map : ");
        TextField mapField = new TextField("20");
        Label obstaclePercentageLabel = new Label("Pourcentage d'obstacles :");
        TextField obstaclePercentageField = new TextField("0.05");
        Label obstaclePerChunkLabel = new Label("Nombre moyen d'obstacles par chunk :");
        TextField obstaclePerChunkField = new TextField("5");
        Button startButton = new Button("Démarrer");
        Button cancelButton = new Button("Annuler");

        startButton.setOnAction(e -> {
            try {
                int port = Integer.parseInt(portField.getText());
                int maxPlayers = Integer.parseInt(maxPlayersField.getText());
                int mapSize = Integer.parseInt(mapField.getText());
                double obstaclePercentage = Double.parseDouble(obstaclePercentageField.getText());
                int obstaclePerChunk = Integer.parseInt(obstaclePerChunkField.getText());
                new Thread(() -> startHost(port, maxPlayers, mapSize, obstaclePercentage, obstaclePerChunk)).start();
            } catch (NumberFormatException ex) {
                showError("Le port doit être un nombre valide.");
            }
        });

        cancelButton.setOnAction(e -> showRoleSelection());

        root.getChildren().addAll(  portLabel, portField,
                                    maxPlayersLabel, maxPlayersField,
                                    mapLabel, mapField,
                                    obstaclePercentageLabel, obstaclePercentageField,
                                    obstaclePerChunkLabel, obstaclePerChunkField,
                                    startButton,
                                    cancelButton);

        Scene scene = new Scene(root, 300, 400);
        stage.setTitle("Host setup");
        stage.setScene(scene);
    }

    private void startHost(int port, int maxPlayers, int mapSize, double obstaclePercentage, int obstaclePerChunk) {
        try {
            Host server = new Host(maxPlayers, mapSize, obstaclePercentage, obstaclePerChunk);
            server.start(port);

            System.out.println("L'hôte est maintenant en écoute sur le port : " + port);

            Platform.runLater(() -> showHostStartedMessage(port, server));
        } catch (Exception e) {
            Platform.runLater(() -> showError("Impossible de démarrer l'hôte sur le port " + port + ". Le port est-il déjà utilisé ?"));
        }
    }

    private void showHostStartedMessage(int port, Host server) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label messageLabel = new Label("Votre adresse IP est : ");
        root.getChildren().add(messageLabel);
        try {
            int i=0;
            InetAddress[] allAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress addr : allAddresses) {
                i++;
                messageLabel = new Label(i+") "+addr.getHostAddress());
                root.getChildren().add(messageLabel);
            }
        }
        catch (Exception e) { 
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        }
        messageLabel = new Label("L'hôte écoute sur le port : " + port);
        Button okButton = new Button("Éteindre serveur");

        okButton.setOnAction(e -> {
            server.stop();
            stage.close();
        });

        root.getChildren().addAll(messageLabel, okButton);

        Scene scene = new Scene(root, 300, 400);
        stage.setTitle("Host démarré");
        stage.setScene(scene);
    }

    private void startClient(String serverAddress, int serverPort) {
        try {
            Client clientApp = new Client(serverAddress, serverPort);
            Stage clientStage = new Stage();
            clientApp.start(clientStage);

            Platform.runLater(() -> stage.close());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la connexion au serveur : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}