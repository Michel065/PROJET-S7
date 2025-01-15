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
    private Socket clientSocket;

    private static boolean serverStarted = false;

    public ThreadHostConnexion(Socket clientSocket, Carte carte, ListePartageThread Liste_Thread, int port) {
        this.port = port;
        this.clientSocket = clientSocket;
        this.carte = carte;
        this.Liste_Thread = Liste_Thread;
        this.max_client = Liste_Thread.get_max_size();
    }
    
    public void run() {
        try {
            if (serverStarted) {
                System.out.println("Le serveur est déjà en cours d'exécution sur le port " + port);
                return; // Si le serveur est déjà lancé, on arrête l'exécution de ce thread
            }

            System.out.println("code d'erreur : 1");

            maSocketEcoute = new ServerSocket(port);
            serverStarted = true; // Marquer le serveur comme démarré
            InetAddress[] allAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress addr : allAddresses) {
                System.out.println("Adresse IP disponible : " + addr.getHostAddress());
            }
            maSocketEcoute.setSoTimeout(2000);
            System.out.println("Serveur prêt et écoute sur le port " + port);
            int premiere_connx = 0;

            while (!(Host.is_close || (Liste_Thread.vide() && premiere_connx > 0))) {
                System.out.println("Nombre de joueurs connectés : " + Liste_Thread.get_size() + " !");

                try {
                    Socket clientSocket = maSocketEcoute.accept();
                    if (clientSocket != null) premiere_connx++;
                    System.out.println("Connexion de : " + clientSocket.getInetAddress() + " : port " + clientSocket.getPort());

                    if (Liste_Thread.get_size() < max_client) {
                        ThreadHostToClient threadClient = new ThreadHostToClient(clientSocket, carte, Liste_Thread);
                        threadClient.start();
                        Liste_Thread.ajouter(threadClient);
                    } else {
                        System.out.println("Connexion refusée, serveur plein!");
                        PrintWriter client_input = new PrintWriter(clientSocket.getOutputStream());
                        client_input.println("Erreur: hôte plein\n\r");
                        client_input.flush();
                    }
                } catch (SocketTimeoutException e) {
                    // Continue to listen for clients
                }
            }

            System.out.println("Fermeture du thread de connexion : " + Thread.currentThread().getName());
        } catch (Exception e) {
            System.err.println("Erreur : " + e);
            e.printStackTrace();
        } finally {
            try {
                if (maSocketEcoute != null) {
                    maSocketEcoute.close();
                    System.out.println("Port " + port + " libéré.");
                }
                serverStarted = false; // Remettre à false une fois que le serveur est arrêté
            } catch (IOException e) {
                System.err.println("Erreur de fermeture : " + e);
                e.printStackTrace();
            }

            // Pause pour permettre au port d'être libéré avant un autre démarrage
            try {
                Thread.sleep(2000); // Attend 2 secondes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}