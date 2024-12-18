import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadHostToClient extends ThreadHostGestionPlayer {
    private Socket clientSocket=null;
    private BufferedReader client_output;
    private PrintWriter client_input;
    private String message_recu="",message_transmit="";

    ThreadHostToClient(Socket client,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        super(carte, players, projectiles);
        this.clientSocket=client;
    }

    private String recevoir() {
        try {
            String msg="",previous="";
            while (client_output.ready()) { 
                previous=msg;
                msg=client_output.readLine();
            }
            return previous;
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private void send(String msg) {
        if (client_input != null && !msg.isEmpty()) {
            client_input.println(msg);
            client_input.flush();
        }
    }

    @Override
    public void run() { 
        System.out.println("demarage du thread : " + Thread.currentThread().getName()+"!");
        create_player();
        try {
			client_output = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            client_input = new PrintWriter(clientSocket.getOutputStream());

            send(carte.stringifie());

		} catch (IOException e) {
			System.err.println("Erreur\n"+e.getMessage());
			e.printStackTrace();
		}

        while(!is_finish()){
            try {
                while (client_output.ready()) { 
                    message_recu=client_output.readLine();
                    message_transmit += Analyse(message_recu);
                }
                send(message_transmit);
                
                update_projectile();
                update_player();
            
                Thread.sleep(45);
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }
            catch (IOException e) {
                System.err.println("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("fermeture du thread: " + Thread.currentThread().getName()+"!");
        send("$end");
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
            if (words.length == 3) object = words[2];
            if (words.length == 4) data = words[3];

            
            System.out.println(action+" " +object+" "+target);
            if (action.equals("get")) {
                if (target.equals("carte")) {
                    reponse=carte.stringifie();
                }
                else reponse=msg_erreur;


            } else if(action.equals("put")) {
                if (target.equals("ourplayer")) {
                    if (object.equals("avance")) {
                        ourplayer.addToSpeed((float) 0.2);
                    } else if(object.equals("recule")) {
                        ourplayer.addToSpeed((float) -0.2);
                    }else if(object.equals("rotation_gauche")) {
                        ourplayer.rotate(-10);
                    }else if(object.equals("rotation_droite")) {
                        ourplayer.rotate(10);
                    }else if(object.equals("tirer")) {
                        tire();
                    }else if(object.equals("null")) {
                        remode_ourplayer();
                    }else if(object.equals("invincibilite")) {
                        ourplayer.setInvinvibilite(Boolean.parseBoolean(data));
                    }
                    else if(object.equals("color")) {
                        int couleur = Integer.parseInt(data);
                        if(ourplayer.setColor(couleur)){
                            reponse="ourplayer enregistre";
                        }
                        else{
                            reponse="erreur couleur indisponible";
                        }
                    }
                    else reponse=msg_erreur;
                } 
            }else reponse=msg_erreur;
        }
        if(!reponse.equals(""))reponse+="\n\r";
        return reponse;
    }
}

