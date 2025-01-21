import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class ThreadClientToHost extends Thread {
    private Socket serveur = null;
    private Stage primaryStage;
    private int port = 5001;
    private int rayon_display_en_case = 5;
    private String IP = "", message_recu = "", message_transmit = "";
    private PrintWriter serveur_input;
    private BufferedReader serveur_output;
    private boolean host_ouvert = true;
    public static boolean player_status=true;
    private long dernier_msg_recu_tmp,dernier_msg_recu=System.nanoTime();
    private Player ourPlayer;

    private final Set<KeyCode> activeKeys = new HashSet<>();

    // Pour la carte
    private Carte carte;
    private ConcurrentLinkedQueue<LightPlayer> players;
    private ConcurrentLinkedQueue<LightProjectile> projectiles;

    
    // Partagé :
    protected ListeAtomicCoord ourprojectilespartagee = new ListeAtomicCoord(30);

    public ListeAtomicCoord get_projectile() {
        return ourprojectilespartagee;
    }

    
    
    ThreadClientToHost(Stage primaryStage, String ip, int port, ConcurrentLinkedQueue<LightPlayer> pl, ConcurrentLinkedQueue<LightProjectile> pr) {
        this.primaryStage = primaryStage;
        this.port = port;
        this.IP = ip;
        this.projectiles = pr;
        this.players = pl;
        this.ourPlayer = new Player(100, 0, 0);   
    }

    public void get_case_centre(Float[] centre) {
        centre[0] = ourPlayer.get_coord().x;
        centre[1] = ourPlayer.get_coord().y;
    }

    public float get_orientation() {
        return ourPlayer.getOrientation();
    }

    private void init_key_input() {
        if (primaryStage.getScene() != null) {
            primaryStage.getScene().setOnKeyPressed(event -> activeKeys.add(event.getCode()));
            primaryStage.getScene().setOnKeyReleased(event -> activeKeys.remove(event.getCode()));
        } else {
            System.err.println("Erreur : la scène n'est pas définie pour le stage");
        }
    }

    public Carte get_carte() {
        return carte;
    }

    public int get_rayon_display_en_case() {
        return rayon_display_en_case;
    }

    private String stringifie_action(String message_sortant) {
        if(!player_status) return "";
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

    @Override
    public void run() { 
        try{
            serveur = new Socket(IP, port);
            serveur_input = new PrintWriter(serveur.getOutputStream());
            serveur_output = new BufferedReader(new InputStreamReader(serveur.getInputStream()));
            init_key_input();

            message_transmit="put ourplayer new\n\r";
            message_transmit+="get carte\n\r";
            message_transmit+="put ourplayer invincibilite false\n\r";

            envoie_et_analyse(message_transmit);

            System.out.println("equipe:"+ourPlayer.getEquipe());
        }
        catch (IOException e) {
            System.err.println("Erreur\n"+e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Initialisation terminée");
        // Notre boucle
        while(!Client.is_close && host_ouvert) {
            try {
                message_transmit = "get ourplayer coord\n\r";
                message_transmit += "get ourplayer orientation\n\r";
                message_transmit += "get projectiles\n\r";
                message_transmit += "get players\n\r";
                message_transmit += "get ourplayer status\n\r";
                message_transmit += stringifie_action(message_transmit);

                envoie_et_analyse(message_transmit);
                is_host_alive(0);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu");
            }
        }        
        System.out.println("Fermeture du thread client to host : " + Thread.currentThread().getName());
        send("put client fermeture\n\r");
        Client.is_close=true;
    }

    public void envoie_et_analyse(String msg){
        try {
            send(msg);
            while (serveur_output.ready()) { // Pour éviter des accumulations
                message_recu = serveur_output.readLine();
                message_transmit += Analyse(message_recu);
                is_host_alive(1);
            }
            send(message_transmit);
            message_transmit="";
        }
        catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void is_host_alive(int val) {
        dernier_msg_recu_tmp = System.nanoTime();
        if(Math.abs(dernier_msg_recu_tmp - dernier_msg_recu) >=2000 * 1000 * 1000) {
            host_ouvert=false;
            Client.is_close=true;
        }
        if(val==1) {
            dernier_msg_recu = dernier_msg_recu_tmp;
        }
    }

    public String Analyse(String requete) {
        if(requete.equals("")) return ""; // Pour éviter d'afficher du vide
        String reponse = "", msg_erreur = "";
        String[] words = requete.split(" ");

        if (words.length >= 2) {
            String action = words[0].toLowerCase();
            String target = words[1];
            String object = "";
            String data = "";
            if (words.length >= 3) object = words[2];
            if (words.length >= 4) data = words[3];
            
            if (action.equals("get")) {
                System.out.println("vide");
            } else if(action.equals("put")) {
                if (target.equals("carte")) {
                    carte = new Carte(object);
                }else if (target.equals("ourplayer")) {
                    if (object.equals("coord")) {
                        String[] coord = data.split(":");
                        ourPlayer.setPosition(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]));
                        //System.out.println("coos du joueur : " + coord[0] + " ; " + coord[1]);
                    }else if(object.equals("null")){
                        Client.is_close = true;
                    }else if(object.equals("orientation")){
                        ourPlayer.setOrientation(Float.parseFloat(data));
                    }
                    else if(object.equals("equipe")){
                        ourPlayer.setEquipe(Integer.parseInt(data));
                    }else if(object.equals("status")){
                        player_status=(Integer.parseInt(data)==1);
                        if(player_status)Client.nbr_fenetre_respawn_ouvert=0;
                    }
                } else if (target.equals("fenetre")) {
                    if (object.equals("rayon")) {
                        rayon_display_en_case = Integer.parseInt(data);
                    }
                } else if (target.equals("projectiles")) {
                    projectiles.clear();
                    int nbr = Integer.parseInt(object);
                    String[] liste_Projectiles = data.split(",");
                    String[] coord;
                    for(int i=0; i < nbr; i++) {
                        coord = liste_Projectiles[i].split(":");
                        projectiles.add(new LightProjectile(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]), Integer.parseInt(coord[2])));
                    }
                }
                else if (target.equals("players")) {
                    players.clear();
                    int nbr = Integer.parseInt(object);
                    String[] liste_Projectiles = data.split(",");
                    String[] data_player;
                    for(int i=0; i < nbr; i++) {
                        data_player = liste_Projectiles[i].split(":");
                        players.add(new LightPlayer(Float.parseFloat(data_player[0]), Float.parseFloat(data_player[1]), Integer.parseInt(data_player[3]), Integer.parseInt(data_player[2])));
                        
                    }
                }
                else if (target.equals("host")) {
                    if (object.equals("fermeture")) {
                        host_ouvert = false;
                    }
                }
                else reponse = msg_erreur;
            } else reponse = msg_erreur;
        }
        if(!reponse.equals("")) reponse += "\n\r";
        return reponse;
    }

    public void respawn_player(){
        if(!player_status){
            message_transmit = "put ourplayer new\n\r";
            message_transmit += "put ourplayer invincibilite false\n\r";
            send(message_transmit);
            message_transmit = "";
        }
    }

}