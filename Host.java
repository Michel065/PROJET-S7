public class Host {
    public long map;
    private Carte carte;
    private ListePartageThread Liste_Thread;
    private ThreadHostConnexion recepteur;
    public static boolean is_close=false;

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

    /*
    public static void main(String[] args) {
        System.out.println("param : nbr_max_joueur , largeur_carte , % de remplissage , nbr d'obstacle moyen par case.");
        Host host;
        int port = 5001;
        if( args.length >= 4) {
            System.out.println("Manuel ON ... \nOK");
            host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), Integer.parseInt(args[3])); // Initialisation de la logique
            if(args.length > 4)port = Integer.parseInt(args[4]);
        }
        else{
            System.out.println("Manuel OFF ... \nOK");
            host = new Host(10,20, 0.05, 5); // Initialisation de la logique
        }
        System.out.println("Host start ...\nOK");
        host.start(port);
    }
    */
}