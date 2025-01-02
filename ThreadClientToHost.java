import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;



public class ThreadClientToHost  extends Thread {
    private Socket serveur=null;
    private Stage primaryStage;
    private int port=5001;
    private String IP="";
    private PrintWriter serveur_input;
    private BufferedReader serveur_output;

    private String message_sortant="";
    private String message_entrant="";
    private final Set<KeyCode> activeKeys = new HashSet<>();

    //pour la carte
    private Carte carte;
    private ListShare<Projectile> projectiles;
    private ListShare<Player> players;
    private Player ourPlayer;
    
    ThreadClientToHost(Stage primaryStage,String ip,int port,ListShare<Projectile> pr,ListShare<Player>pl){
        this.primaryStage=primaryStage;
        this.port=port;
        this.projectiles=pr;
        this.players=pl;
        this.ourPlayer=new Player(Color.GREEN,100,0,0);
        
    }

    public Float[] get_case_centre() {
        return new Float[] {ourPlayer.getX(), ourPlayer.getY()};
    }

    private void init() {
        if (primaryStage.getScene() != null) {
            primaryStage.getScene().setOnKeyPressed(event -> activeKeys.add(event.getCode()));
            primaryStage.getScene().setOnKeyReleased(event -> activeKeys.remove(event.getCode()));
        } else {
            System.err.println("Erreur : La scène n'est pas définie pour le stage.");
        }

        String msg =readServeur();
        if(!msg.equals(""))
            carte=new Carte(msg);
        else System.out.println("erreur recueration de la carte");
        
    }

    private String readServeur(){
        try {
            return serveur_output.readLine();
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        return"";
    }

    public Carte get_carte(){
        return carte;
    }

    private void stringifie_action() {
        message_sortant="";
        for (KeyCode key : activeKeys) {
            message_sortant+=key+" ";
        }
    }

    private void send() {
        if (serveur_input != null && !message_sortant.isEmpty()) {
            serveur_input.println(message_sortant);
            serveur_input.flush();
        }
    }

    private void send(String msg) {
        if (serveur_input != null && !msg.isEmpty()) {
            serveur_input.println(msg);
            serveur_input.flush();
        }
    }

    private String recevoir() {
        try {
            if (serveur_output.ready()) {
                message_entrant = serveur_output.readLine(); 
                return message_entrant;
            } else {
                message_entrant = "";
                return "";
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
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
                if(recevoir().equals("$end"))Client.is_close=true;

                if (!message_entrant.isEmpty()) {
                    String[] coord = message_entrant.split(":");
                    ourPlayer.setPosition(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]));
                }


                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.err.println("Le thread a été interrompu : " + e.getMessage());
                Thread.currentThread().interrupt(); // Signaler l'interruption
                break; // Sortir de la boucle si le thread est interrompu
            }
        }        
        System.out.println("fermeture du thread: " + Thread.currentThread().getName()+"!");

        send("$end");

    }
}
