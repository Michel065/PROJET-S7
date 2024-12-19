import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;
import java.util.Random;
import javafx.stage.Stage;



public class ThreadClientToHost  extends Thread {
    private Socket serveur=null;
    private Stage primaryStage;
    private int port=5001;
    private int rayon_display_en_case=5;
    private String IP="", message_recu="",message_transmit="";
    private PrintWriter serveur_input;
    private BufferedReader serveur_output;

    private final Set<KeyCode> activeKeys = new HashSet<>();

    //pour la carte
    private Carte carte;
    private ListShare<LightRond> players;
    private ListShare<LightRond> projectiles;
    private Player ourPlayer;
    
    ThreadClientToHost(Stage primaryStage,String ip,int port,ListShare<LightRond> pl,ListShare<LightRond> pr){
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
    }

    public Carte get_carte(){
        return carte;
    }

    public int get_rayon_display_en_case(){
        return rayon_display_en_case;
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

    @Override
    public void run() { 
        try{//init
            serveur = new Socket(IP, port);// on init la co
            serveur_input = new PrintWriter(serveur.getOutputStream());
            serveur_output = new BufferedReader(new InputStreamReader(serveur.getInputStream()));
            init();

            send("get carte\n\r");
            Analyse(serveur_output.readLine());
            
            Random random = new Random();
            
            send("put ourplayer color "+random.nextInt(4)+"\n\r");
            send("put ourplayer invincibilite false\n\r");


        }
        catch (IOException e) {
            System.err.println("Erreur\n"+e.getMessage());
            e.printStackTrace();
        }

        System.out.println("initialisation terminé!");
        //notre boucle
        while(!Client.is_close){
            try {
                message_transmit="get ourplayer coord\n\r";
                message_transmit+="get projectiles\n\r";
                message_transmit+="get players\n\r";
                message_transmit=stringifie_action(message_transmit);
                send(message_transmit);
                while (serveur_output.ready()) { //pour eviter des acumulation
                    message_recu=serveur_output.readLine();
                    message_transmit += Analyse(message_recu);
                }
                send(message_transmit);
                
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }catch (IOException e) {
                System.err.println("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
            
        }        
        System.out.println("fermeture du thread: " + Thread.currentThread().getName()+"!");

        send("put ourplayer null\n\r");

    }

    public String Analyse(String requete){
        if(requete.equals("")) return "";//pour eviter d'afficher du vide

        String reponse="",msg_erreur="Commande non reconnue.";
        String[] words = requete.split(" ");

        if (words.length >= 2) {
            String action = words[0].toLowerCase();
            String target = words[1];
            String object="";
            String data="";
            if (words.length >= 3) object = words[2];
            if (words.length >= 4) data = words[3];
            
            //System.out.println(action+" " +object+" "+target+" "+data);
            
            if (action.equals("get")) {
                System.out.println("coucou");


            } else if(action.equals("put")) {
                if (target.equals("carte")) {
                    carte= new Carte(object);
                }else if (target.equals("ourplayer")) {
                    if (object.equals("coord")) {
                        String[] coord = data.split(":");
                        ourPlayer.setPosition(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]));
                    }else if(object.equals("null")){
                        Client.is_close=true;
                    }
                }else if (target.equals("fenetre")) {
                    if (object.equals("rayon")) {
                        rayon_display_en_case=Integer.parseInt(data);
                    }
                }else if (target.equals("projectiles")) {
                    projectiles.clear();
                    int nbr= Integer.parseInt(object);
                    String[] liste_Projectiles=data.split(",");
                    String[] coord;
                    for(int i=0;i<nbr;i++){
                        coord = liste_Projectiles[i].split(":");
                        projectiles.add(new LightRond(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]), ourPlayer.get_proj_radius(), 1));
                    }
                }
                else if (target.equals("players")) {
                    players.clear();
                    int nbr= Integer.parseInt(object);
                    String[] liste_Projectiles=data.split(",");
                    String[] coord;
                    for(int i=0;i<nbr;i++){
                        coord = liste_Projectiles[i].split(":");
                        players.add(new LightRond(Float.parseFloat(coord[0]), Float.parseFloat(coord[1]), ourPlayer.getRadius(), 1));
                    }
                }
                else reponse=msg_erreur;
            }else reponse=msg_erreur;
        }
        if(!reponse.equals(""))reponse+="\n\r";
        return reponse;
    }
}
