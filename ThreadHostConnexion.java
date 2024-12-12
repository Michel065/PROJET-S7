import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.stage.Stage;

public class ThreadHostConnexion extends Thread  {
    private int port;
    private Carte carte;
    private ListShare<Player> players;
    private ListShare<Projectile> projectiles;
    private ServerSocket maSocketEcoute = null;
    private boolean phase_de_test=true;
    private Stage primaryStage;

    


    ThreadHostConnexion(int port,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles,boolean phase_de_test,Stage primaryStage){
        this.port=port;
        this.carte=carte;
        this.players=players;
        this.projectiles=projectiles;
        this.phase_de_test=phase_de_test;
        this.primaryStage=primaryStage;
    }

    public void faux_client(int nbr){
        for(int i=0;i<nbr;i++){
            ThreadHostToClient ThreadClient = new ThreadHostToClient(null, carte,players,projectiles,primaryStage);
            ThreadClient.start();
        }
    }
    
    public void run() { 
        if(phase_de_test){
            faux_client(1);
        }
        else{
            try {
                maSocketEcoute = new ServerSocket(port);

                System.out.println("Serveur pret et ecoute sur le port "+port);

                while(true) {				
                    Socket clientSocket = maSocketEcoute.accept();

                    System.out.println("Connexion de : " + clientSocket.getInetAddress() + " : port " + clientSocket.getPort());//on precise qui ce connecte

                    ThreadHostToClient ThreadClient = new ThreadHostToClient(clientSocket, carte,players,projectiles);
                    ThreadClient.start();
                }
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
}
