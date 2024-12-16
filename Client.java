import javafx.stage.Stage;

public class Client {
    private String serverIp;
    private int port;
    private ThreadClientToHost toServer;
    public static boolean is_close = false;

    public Client(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void connect(Stage primaryStage) {
        // Démarrage du thread pour la connexion avec le serveur
        toServer = new ThreadClientToHost(primaryStage, serverIp, port);
        toServer.start();
    }
}
