import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadHostToClient extends ThreadHostGestionPlayer {

    private Socket clientSocket=null;
    private BufferedReader client_output;
    private PrintWriter client_input;
    private int rayon_display_en_case=5;
    private String message_recu="",message_transmit="";

    ThreadHostToClient(Socket client,Carte carte,ListePartageThread Liste_Thread){
        super(carte, Liste_Thread);
        this.clientSocket=client;
    }

    private void send(String msg) {
        if (client_input != null && !msg.isEmpty()) {
            client_input.println(msg);
            client_input.flush();
        }
    }

    private void creation_interface_client(){
        try {
			client_output = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            client_input = new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Erreur\n"+e.getMessage());
			e.printStackTrace();
		}

    } 

    @Override
    public void run() { 
        System.out.println("demarage du thread : " + Thread.currentThread().getName()+"!");
        create_player();
        creation_interface_client();

        send("put fenetre rayon "+rayon_display_en_case+"\n\r");
        while(!is_finish()){
            try {
                message_transmit="";
                while (client_output.ready()) { //pour eviter des acumulation
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
        send("put ourplayer null\n\r");
    }


    public String Analyse(String requete){
        if(requete.equals("")) return "";//pour eviter d'afficher du vide

        String reponse="",msg_erreur=""; //"Commande non reconnue.";
        String[] words = requete.split(" ");

        if (words.length >= 2) {
            String action = words[0].toLowerCase();
            String target = words[1];
            String object="";
            String data="";
            if (words.length >= 3) object = words[2];
            if (words.length >= 4) data = words[3];

            if (action.equals("get")) {
                if (target.equals("carte")) {
                    reponse="put carte "+carte.stringifie();
                }else if (target.equals("ourplayer")) {
                    if (object.equals("coord")) {
                        
                        reponse="put ourplayer coord "+ourplayer.getCoordString();
                    }else if (object.equals("orientation")) {
                        reponse="put ourplayer orientation "+(float)ourplayer.getOrientation();
                    }
                }
                else if (target.equals("projectiles")) {                    
                    String suite="";
                    int x=0;
                    for(Projectile pro:projectiles){
                        if(pro.in_fentre(ourplayer, rayon_display_en_case)){
                            suite+=pro.getCoordString()+",";
                            x++;
                        }
                    }
                    reponse="put projectiles "+x+" "+suite;
                }else if (target.equals("players")) {                    
                    String suite="";
                    int x=0;
                    for(Player pl:players){
                        if(pl.in_fentre(ourplayer, rayon_display_en_case)){
                            suite+=pl.getCoordString()+",";
                            x++;
                        }
                    }
                    reponse="put players "+x+" "+suite;
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
                        System.out.println("couleur chois!");
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

