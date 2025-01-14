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

    // Fonction pour démarrer le serveur
    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            InetAddress[] allAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress addr : allAddresses) {
                System.out.println("Adresse IP disponible : " + addr.getHostAddress());
            }
            System.out.println("Serveur en attente d'une connexion sur le port 5000...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Un client s'est connecté !");
            clientSocket.close();
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
