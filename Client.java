import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Client extends Application {
    public static boolean is_close=false;
    private final int sizeWindow = 750;

    @Override
    public void start(Stage primaryStage) {
        // Création de l'interface graphique
        Pane root = new Pane();

        primaryStage.setTitle("Fenêtre Vide");
        primaryStage.setScene(new Scene(root, sizeWindow, sizeWindow));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("La fermeture ...");
            Host.is_close=true;
        });
        //on init
        ThreadClientToHost to_serveur= new ThreadClientToHost(primaryStage,"127.0.0.1",5001);
        to_serveur.start();
    
    }

    public static void main(String[] args) {
        Application.launch(Client.class, args); // Correction ici
    }
}
