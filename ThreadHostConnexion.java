import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadHostConnexion extends Thread  {
    private int port;
    private Carte carte;
    private ListShare<Player> players;
    private ListShare<Projectile> projectiles;
    private ServerSocket maSocketEcoute = null;

    


    ThreadHostConnexion(int port,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        this.port=port;
        this.carte=carte;
        this.players=players;
        this.projectiles=projectiles;
    }
    
    public void run() { 
        try {
            maSocketEcoute = new ServerSocket(port);
            maSocketEcoute.setSoTimeout(2000); 

            System.out.println("Serveur pret et ecoute sur le port "+port);
            int premiere_connx=0;
            while(!(Host.is_close )) {	//|| (players.size()==0 && premiere_connx>0)
                System.out.println("Serveur pret et ecoute sur le port "+players.size()+" "+premiere_connx);
                try{
                    Socket clientSocket=null;
                    clientSocket = maSocketEcoute.accept();
                    if(clientSocket!=null) premiere_connx++;
                    System.out.println("Connexion de : " + clientSocket.getInetAddress() + " : port " + clientSocket.getPort());//on precise qui ce connecte

                    ThreadHostToClient ThreadClient = new ThreadHostToClient(clientSocket, carte,players,projectiles);
                    ThreadClient.start();
                } catch (SocketTimeoutException e) {}		
                
            }
            System.out.println("fermeture du thread de co : " + Thread.currentThread().getName()+"!");
            
        }
        catch (Exception e) { 
            System.err.println("Erreur : "+e);
            e.printStackTrace();
        }
        finally {
            try {
                if(maSocketEcoute!=null) maSocketEcoute.close();
            } catch (IOException e) {
                System.err.println("Erreur : "+e);
                e.printStackTrace();
            }
        }
    }
}
