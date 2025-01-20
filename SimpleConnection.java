import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleConnection {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Voulez-vous être le serveur ou le client ? (serveur/client)");
        String choix = scanner.nextLine();

        if (choix.equalsIgnoreCase("serveur")) {
            startServer();
        } else if (choix.equalsIgnoreCase("client")) {
            startClient();
        } else {
            System.out.println("Choix invalide. Veuillez relancer le programme.");
        }
        scanner.close();
    }

    private static void startServer() {
        try {
            // Forcer le serveur à écouter sur l'IP spécifique
            ServerSocket serverSocket = new ServerSocket(12345);//, 50, InetAddress.getByName("192.168.52.237"));
            System.out.println("Serveur en attente d'une connexion sur l'adresse : 192.168.52.237 et le port 12345...");
    
            // Attente de connexion d'un client
            Socket clientSocket = serverSocket.accept();
            System.out.println("Un client s'est connecté !");
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Erreur dans le serveur : " + e.getMessage());
        }
    }
    

    // Fonction pour démarrer le client
    private static void startClient() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez l'adresse IP du serveur : ");
        String serverIp = scanner.nextLine();

        try (Socket socket = new Socket(serverIp, 12345)) {
            System.out.println("Connexion au serveur réussie !");
        } catch (IOException e) {
            System.err.println("Erreur dans le client : " + e.getMessage());
        }
        scanner.close();
    }
}
