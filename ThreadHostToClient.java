import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadHostToClient extends ThreadHostGestionPlayer {

    private Socket clientSocket = null;
    private BufferedReader client_output;
    private PrintWriter client_input;
    private int rayon_display_en_case = 5;
    private String message_recu = "", message_transmit = "";
    private long dernier_msg_recu = System.nanoTime(), dernier_msg_recu_tmp, current_time, last_time, delta_time;

    ThreadHostToClient(Socket client, Carte carte, ListePartageThread Liste_Thread) {
        super(carte, Liste_Thread);
        this.clientSocket = client;
    }

    private void send(String msg) {
        if (client_input != null && !msg.isEmpty()) {
            client_input.println(msg);
            client_input.flush();
            msg="";
        }
    }

    private void creation_interface_client() {
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
        System.out.println("demarage du thread : " + Thread.currentThread().getName() + "!");
        create_player();
        creation_interface_client();

        send("put fenetre rayon " + rayon_display_en_case + "\n\r");
        while(!is_finish()) {
            try {
                current_time = System.nanoTime();
                delta_time = last_time-last_time;
                last_time = current_time;

                message_transmit = "";
                while (client_output.ready()) { // Pour eviter des acumulation
                    this.message_recu = client_output.readLine();
                    message_transmit += Analyse(this.message_recu);
                    is_client_alive(1);
                }

                send(message_transmit);
                update_projectile();
                update_player();

                is_client_alive(0);
                Thread.sleep(45);
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }
            catch (IOException e) {
                System.err.println("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("fermeture du thread: " + Thread.currentThread().getName() + "!");
        send("put ourplayer null\n\r");
        Liste_Thread.supprimer(index.get());
    }

    public void is_client_alive(int val) {
        dernier_msg_recu_tmp = System.nanoTime();
        if(Math.abs(dernier_msg_recu_tmp - dernier_msg_recu) >=2000*1000*1000) {
            Liste_Thread.supprimer(index.get());
            statut_joueur=false;
            ourplayer=null;
        }
        if(val==1) {
            dernier_msg_recu=dernier_msg_recu_tmp;
        }
    }

    public String Analyse(String requete){
        if(requete.equals("")) return ""; // Pour éviter d'afficher du vide

        String reponse = "", msg_erreur = ""; // "Commande non reconnue.";
        String[] words = requete.split(" ");

        if (words.length >= 2) {
            String action = words[0].toLowerCase();
            String target = words[1];
            String object = "";
            String data = "";
            if (words.length >= 3) object = words[2];
            if (words.length >= 4) data = words[3];

            if (action.equals("get")) {
                if (target.equals("carte")) {
                    reponse = "put carte " + carte.stringifie();
                }else if (target.equals("ourplayer")) {
                    if (object.equals("coord")) {
                        reponse="put ourplayer coord " + ourplayer.getCoordString();
                    } else if (object.equals("orientation")) {
                        reponse="put ourplayer orientation " + (float)ourplayer.getOrientation();
                    }
                }
                else if (target.equals("projectiles")) {                    
                    String suite = "";
                    int x = 0;
                    CoordFloat coord_tmp = new CoordFloat();
                    int taille = Liste_Thread.get_size();
                    if(taille >= 1) {
                        for(int i=0; i < taille; i++) {
                            ThreadHostToClient tmp = Liste_Thread.recuperer(i);
                            ListeAtomicCoord projs = tmp.get_projectile();
                            int size_proj = projs.get_size();
                            for(int id_proj=0; id_proj < size_proj; id_proj++) {
                                projs.get(i, coord_tmp);
                                if(objet_dans_fentre_client(coord_tmp)) {
                                    suite += coord_tmp.x + ":" + coord_tmp.y + ":" + tmp.getEquipe() + ",";
                                    x++;
                                }
                            }
                        }
                    }
                    reponse = "put projectiles " + x + " " + suite;
                } else if (target.equals("players")) {                    
                    String suite = "";
                    int x = 0;
                    CoordFloat coord_tmp = new CoordFloat();
                    int taille = Liste_Thread.get_size();
                    if(taille >= 1) {
                        for(int i=0; i < taille; i++) {
                            ThreadHostToClient tmp = Liste_Thread.recuperer(i);
                            if(tmp.getStatus()) {
                                coord_tmp.set(tmp.getCoordJoueur());
                                if(objet_dans_fentre_client(coord_tmp)) {
                                    suite += coord_tmp.x + ":" + coord_tmp.y + ":" + tmp.getEquipe() + ",";
                                    x++;
                                }
                            }
                        }
                    }
                    reponse = "put players " + x + " " + suite;
                }
                else reponse = msg_erreur;
            } else if(action.equals("put")) {
                if (target.equals("ourplayer")) {
                    if (object.equals("avance")) {
                        ourplayer.addToSpeed((float)0.2);
                    } else if(object.equals("recule")) {
                        ourplayer.addToSpeed((float)-0.2);
                    } else if(object.equals("rotation_gauche")) {
                        ourplayer.rotate(-10);
                    } else if(object.equals("rotation_droite")) {
                        ourplayer.rotate(10);
                    } else if(object.equals("tirer")) {
                        tire();
                    } else if(object.equals("null")) {
                        remode_ourplayer();
                    } else if(object.equals("invincibilite")) {
                        ourplayer.setInvinvibilite(Boolean.parseBoolean(data));
                    }
                    else if(object.equals("equipe")) {
                        equipe = Integer.parseInt(data);
                        System.out.println("equipe choisi!");
                        if(ourplayer.setEquipe(equipe)) {
                            reponse = "ourplayer enregistre";
                        }
                        else {
                            reponse = "erreur couleur indisponible";
                        }
                    }
                    else reponse = msg_erreur;
                } 
            } else reponse = msg_erreur;
        }
        if(!reponse.equals("")) reponse += "\n\r";
        return reponse;
    }

    public boolean objet_dans_fentre_client(CoordFloat rond) {
        CoordFloat centre = ourplayer.getCoord();
        return Math.abs(centre.x - rond.x) < rayon_display_en_case && Math.abs(centre.y - rond.y) < rayon_display_en_case;
    }
}