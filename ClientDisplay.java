import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientDisplay extends Application {
    private final int sizeWindow = 750;
    private Client client;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation de la logique Client
        client = new Client("127.0.0.1", 5001);

        // Création de l'interface graphique
        Pane root = new Pane();
        primaryStage.setTitle("Fenêtre Vide");
        primaryStage.setScene(new Scene(root, sizeWindow, sizeWindow));
        primaryStage.show();

        // Connexion au serveur via la logique Client
        client.connect(primaryStage);

        // Gestion de la fermeture
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("La fermeture ...");
            Client.is_close = true;
        });
    }

    public static void main(String[] args) {
        Application.launch(ClientDisplay.class, args);
    }
}
