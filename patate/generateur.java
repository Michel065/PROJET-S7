import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class generateur extends Application {
    private static int nbr_client = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Param 1 : Le nombre de clients");
            return;
        }
        nbr_client = Integer.parseInt(args[0]);
        
        Host host = new Host(10,3, 20, 0.05, 5);
        host.start(5001);

        // Lancer la plateforme JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        for (int i = 0; i < nbr_client; i++) {
            System.out.println("Création du client : " + i);

            int finalI = i;
            new Thread(() -> {
                try {
                    Client tmp = new Client("127.0.0.1", 5001);
                    System.out.println("Client " + finalI + " démarré.");

                    // Utilisation de Platform.runLater pour JavaFX
                    Platform.runLater(() -> {
                        Stage stage = new Stage();
                        tmp.start(stage);
                    });
                } catch (Exception e) {
                    System.err.println("Erreur pour le client " + finalI + ": " + e.getMessage());
                }
            }).start();
        }
    }
}
