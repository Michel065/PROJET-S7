import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadHostToClient extends ThreadHostSkull {
    private Socket clientSocket=null;
    private BufferedReader client_output;
    private PrintWriter client_input;

    private float x,y;



    ThreadHostToClient(Socket client,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        super(carte, players, projectiles);
        this.clientSocket=client;
    }

    private String recevoir() {
        try {
            while (client_output.ready()) { 
                return client_output.readLine();
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private void mouve(String msg) {
        if (msg.contains("Z")) {
            ourplayer.addToSpeed((float) 0.2);
        }
        if (msg.contains("Q")) {
            ourplayer.rotate(-10);
        }
        if (msg.contains("S")) {
            ourplayer.addToSpeed((float) -0.2);
        }
        if (msg.contains("D")) {
            ourplayer.rotate(10);
        }
        if (msg.contains("SPACE")) {
            tire();
        }
        if(msg.equals("$end"))remode_player();
    }

    private void send(String msg) {
        if (client_input != null && !msg.isEmpty()) {
            client_input.println(msg);
            client_input.flush();
        }
    }

    private void envoyer_info(){
        if(x!=ourplayer.getX() || y!=ourplayer.getY()){
            send(ourplayer.getCoordString());
            x=ourplayer.getX();
            y=ourplayer.getY();
        }
    }

    @Override
    protected void action(){

        
        envoyer_info();
        mouve(recevoir());
        
    }

    @Override
    protected void init(){
        try {
			client_output = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            client_input = new PrintWriter(clientSocket.getOutputStream());

            send(carte.stringifie());
            

            x=ourplayer.getX();
            y=ourplayer.getY();

		} catch (IOException e) {
			System.err.println("Erreur\n"+e.getMessage());
			e.printStackTrace();
		}
        
    }   
    
    @Override
    protected void finish(){
        send("$end");
    }
}
