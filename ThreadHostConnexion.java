import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadHostConnexion extends Thread  {
    private int port;
    private Carte carte;
    private ListePartageThread Liste_Thread;
    private int max_client;
    private ServerSocket maSocketEcoute = null;

    ThreadHostConnexion(int port, Carte carte, ListePartageThread Liste_Thread) {
        this.port = port;
        this.carte = carte;
        this.Liste_Thread = Liste_Thread;
        this.max_client = Liste_Thread.get_max_size();
    }
    
    public void run() { 
        try {
            maSocketEcoute = new ServerSocket(port);
            InetAddress[] allAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress addr : allAddresses) {
                System.out.println("Adresse IP disponible : " + addr.getHostAddress());
            }
            maSocketEcoute.setSoTimeout(2000); 
            System.out.println("Serveur pret et ecoute sur le port " + port);
            int premiere_connx = 0;
            while(!(Host.is_close|| (Liste_Thread.vide() && premiere_connx>0) )) {
                System.out.println("nombre de joueur connecte :" + Liste_Thread.get_size() + "!");
                try{
                    Socket clientSocket = null;
                    clientSocket = maSocketEcoute.accept();
                    if(clientSocket != null) premiere_connx++;
                    System.out.println("Connexion de : " + clientSocket.getInetAddress() + " : port " + clientSocket.getPort()); //On précise qui se connecte
                    if(Liste_Thread.get_size()<max_client){
                        ThreadHostToClient ThreadClient = new ThreadHostToClient(clientSocket, carte, Liste_Thread);
                        ThreadClient.start();
                        Liste_Thread.ajouter(ThreadClient);
                    }
                    else {
                        System.out.println("Connexion refuse serveur plein!");
                        PrintWriter client_input = new PrintWriter(clientSocket.getOutputStream());
                        client_input.println("erreur host plein\n\r");
                        client_input.flush();
                    }
                } catch (SocketTimeoutException e) {}		  
            }
            System.out.println("fermeture du thread de co : " + Thread.currentThread().getName() + "!");
        }
        catch (Exception e) { 
            System.err.println("Erreur : "+e);
            e.printStackTrace();
        }
        finally {
            try {
                if(maSocketEcoute != null) maSocketEcoute.close();
            } catch (IOException e) {
                System.err.println("Erreur : "+e);
                e.printStackTrace();
            }
        }
    }
}