import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadHostToClient extends ThreadHostSkull {
    private Socket clientSocket=null;
    private String message="";
    private BufferedReader client_input;


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

    @Override
    protected void action(){
        recevoir();

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

    @Override
    protected void init(){
        try {
			client_input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Erreur\n"+e.getMessage());
			e.printStackTrace();
		}
        
    }    
}
