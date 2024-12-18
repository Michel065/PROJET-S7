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
        this.ourPlayer=new Player(100,0,0);
        
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


    private String stringifie_action(String message_sortant) {
        if (activeKeys.contains(KeyCode.Z)) {
            message_sortant += "put ourplayer avance\n\r";
        }
        if (activeKeys.contains(KeyCode.S)) {
            message_sortant += "put ourplayer recule\n\r";
        }
        if (activeKeys.contains(KeyCode.Q)) {
            message_sortant += "put ourplayer rotation_gauche\n\r";
        }
        if (activeKeys.contains(KeyCode.D)) {
            message_sortant += "put ourplayer rotation_droite\n\r";
        }
        if (activeKeys.contains(KeyCode.SPACE)) {
            message_sortant += "put ourplayer tirer\n\r";
        }
        return message_sortant;
    }
    

    private void send(String msg) {
        if (serveur_input != null && !msg.isEmpty()) {
            serveur_input.println(msg);
            serveur_input.flush();
        }
    }

    private String recevoir() {
        try {
            String msg="";
            while (serveur_output.ready()) { 
                msg=serveur_output.readLine();
            }
            return msg;
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private void update_our_data(String msg){
        if(msg.equals("$end"))Client.is_close=true;

        /*if (!msg.isEmpty()) {
            String[] coord = msg.split(":");
            ourPlayer.setPosition(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]));
        }*/
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
                String msg=stringifie_action("");
                if(!msg.equals(""))System.out.println(msg);
                send(msg);

                update_our_data(recevoir());

                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println("Le thread a été interrompu : " + e.getMessage());
                Thread.currentThread().interrupt(); // Signaler l'interruption
                break; // Sortir de la boucle si le thread est interrompu
            }
        }        
        System.out.println("fermeture du thread: " + Thread.currentThread().getName()+"!");

        send("put ourplayer null\n\r");

    }
}
