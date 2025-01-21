public class Host {
    public long map;
    private Carte carte;
    private ListePartageThread Liste_Thread;
    private ThreadHostConnexion recepteur;
    public static boolean is_close = false;

    Host(int nbr_joueur_max, int map, double pourcentageObstacle, int nbrMoyenObstacleParCase) {
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);
        this.map = carte.getTailleReel(); 
        carte.create_all_initial_obstacle();
        Liste_Thread = new ListePartageThread(nbr_joueur_max);
    }    

    // Pour le display
    public boolean is_finish() {
        return Liste_Thread.get_size() == 0;
    }

    public void killAllClient() {
        while(Liste_Thread.get_size() > 0) {Liste_Thread.supprimer(0);}
    }

    public void start(int port) {
        recepteur = new ThreadHostConnexion(port, carte, Liste_Thread);
        recepteur.start();
    }

    public void stop() {
        is_close = true; // Marquer le serveur comme arrêté
        if (recepteur != null) {
            recepteur.interrupt(); // Interrompre le thread d'écoute
        }
        killAllClient(); // Terminer tous les threads clients
        System.out.println("Serveur arrêté");
    }

    public static void main(String[] args){
        Host server = new Host(10, 20, 0.05, 5);
        server.start(80);
    }
}