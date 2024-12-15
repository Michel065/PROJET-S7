import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;



public class ThreadClientToHost  extends Thread {
    private Socket serveur=null;
    private Stage primaryStage;
    private int port=5001;
    private String IP="";
    private PrintWriter serveur_input;
    private String message="";
    private final Set<KeyCode> activeKeys = new HashSet<>();
    
    ThreadClientToHost(Stage primaryStage,String ip,int port ){
        this.primaryStage=primaryStage;
        this.port=port;
        this.IP=ip;
        
    }

    private void init() {
        if (primaryStage.getScene() != null) {
            primaryStage.getScene().setOnKeyPressed(event -> activeKeys.add(event.getCode()));
            primaryStage.getScene().setOnKeyReleased(event -> activeKeys.remove(event.getCode()));
        } else {
            System.err.println("Erreur : La scène n'est pas définie pour le stage.");
        }
    }

    private void stringifie_action() {
        message="";
        for (KeyCode key : activeKeys) {
            message+=key+" ";
        }
    }

    private void send() {
        if (serveur_input != null && !message.isEmpty()) {
            serveur_input.println(message);
            serveur_input.flush();
        }
    }

    @Override
    public void run() { 
        try{
            serveur = new Socket(IP, port);// on init la co
            serveur_input = new PrintWriter(serveur.getOutputStream());
        }
        catch (IOException e) {
            System.err.println("Erreur\n"+e.getMessage());
            e.printStackTrace();
        }
            
        init();

        while(!Client.is_close){
            try {
                stringifie_action();
                send();



                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Le thread a été interrompu : " + e.getMessage());
                Thread.currentThread().interrupt(); // Signaler l'interruption
                break; // Sortir de la boucle si le thread est interrompu
            }
        }        
        System.out.println("fermeture du thread: " + Thread.currentThread().getName()+"!");

    }
}
