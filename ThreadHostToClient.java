import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadHostToClient extends ThreadHostSkull {
    private Socket clientSocket=null;
    private String message="";
    private BufferedReader client_input;
    private PrintWriter client_output;



    ThreadHostToClient(Socket client,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        super(carte, players, projectiles);
        this.clientSocket=client;
    }

    private void recevoir() {
        try {
            if (client_input.ready()) { // Vérifie s'il y a un message disponible
                message = client_input.readLine(); // Lit le message si disponible
            } else {
                message = ""; // Aucun message, on passe à autre chose
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mouve() {
        if (message.contains("Z")) {
            ourplayer.addToSpeed((float) 0.2);
        }
        if (message.contains("Q")) {
            ourplayer.rotate(-10);
        }
        if (message.contains("S")) {
            ourplayer.addToSpeed((float) -0.2);
        }
        if (message.contains("D")) {
            ourplayer.rotate(10);
        }
        if (message.contains("SPACE")) {
            tire();
        }
    }

    private void envoyer() {

    }

    @Override
    protected void action(){
        envoyer();
        recevoir();
        mouve();
        
    }

    @Override
    protected void init(){
        try {
			client_input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            client_output = new PrintWriter(clientSocket.getOutputStream());

            //on vcommence par envoyer les proj et info importante

            // on recup la taille de la fenetre
            

		} catch (IOException e) {
			System.err.println("Erreur\n"+e.getMessage());
			e.printStackTrace();
		}
        
    }    
}
