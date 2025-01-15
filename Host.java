import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Host {
    public long map;
    private Carte carte;
    private ListePartageThread Liste_Thread;
    private ThreadHostConnexion recepteur;
    public static boolean is_close = false;

    // Constructeur initial
    Host(int nbr_joueur_max, int map, double pourcentageObstacle, int nbrMoyenObstacleParCase) {
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);
        this.map = carte.getTailleReel(); 
        carte.create_all_initial_obstacle();
        Liste_Thread = new ListePartageThread(nbr_joueur_max);
    }

    // Pour vérifier si le jeu est terminé (aucun joueur n'est connecté)
    public boolean is_finish() {
        return Liste_Thread.get_size() == 0;
    }

    // Arrêter tous les clients connectés
    public void killAllClient() {
        while (Liste_Thread.get_size() > 0) {
            Liste_Thread.supprimer(0);
        }
    }

    public void start(int port) {
        try {
            // Crée un ServerSocket pour écouter sur le port spécifié
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("L'hôte attend une connexion sur le port " + port + "...");
            
            // Accepte une connexion entrante (bloque ici jusqu'à ce qu'un client se connecte)
            Socket clientSocket = serverSocket.accept(); 
            System.out.println("Un joueur s'est connecté !");

            // Crée un thread pour gérer cette connexion client
            ThreadHostConnexion recepteur = new ThreadHostConnexion(clientSocket, carte, Liste_Thread, port);
            recepteur.start();
        } catch (IOException e) {
            System.out.println("Erreur lors du démarrage du serveur : " + e.getMessage());
            showAlert("Erreur", "Impossible de démarrer le serveur. Le port est-il déjà utilisé ?");
        }
    }

    // Méthode de démarrage principal
    public static void main(String[] args) {
        /*
            // Récupérer les paramètres ou utiliser des valeurs par défaut
            if (args.length >= 4) {
                System.out.println("manuel ON ... \nOK");
                host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), Integer.parseInt(args[3])); // Initialisation de la logique
                if (args.length > 4) port = Integer.parseInt(args[4]);
            } else {
                System.out.println("manuel OFF ... \nOK");
                host = new Host(10, 20, 0.05, 5); // Initialisation de la logique avec des valeurs par défaut
            }
        */

        System.out.println("manuel OFF ... \nOK");
        Host host = new Host(10, 20, 0.05, 5); // Initialisation de la logique avec des valeurs par défaut

        // Affichage de la confirmation et lancement de l'hôte
        System.out.println("L'hôte démarre...");
        System.out.println("code d'erreur : 9");
    }

    // Méthode pour afficher des alertes d'erreur (utilisée dans le catch des exceptions)
    private static void showAlert(String title, String message) {
        // Cette méthode peut être utilisée pour afficher une alerte à l'utilisateur si vous souhaitez ajouter une gestion d'alertes dans l'interface graphique (par exemple, via JavaFX)
        System.out.println(title + ": " + message);
    }
}
