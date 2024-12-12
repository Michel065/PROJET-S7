import java.net.Socket;

public class ThreadHostToClient extends ThreadHostSkull {
    private Socket clientSocket=null;

    ThreadHostToClient(Socket client,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        super(carte, players, projectiles);
        this.clientSocket=client;
    }
    
    @Override
    protected void action(){}

    @Override
    protected void init(){}
}
