import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private BufferedReader serveur_output;

    private String message="";
    private final Set<KeyCode> activeKeys = new HashSet<>();

    //pour la carte
    private Carte carte;
    
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

        try {
            carte=new Carte(serveur_output.readLine());
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Carte get_carte(){
        return carte;
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
            serveur_output = new BufferedReader(new InputStreamReader(serveur.getInputStream()));

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
