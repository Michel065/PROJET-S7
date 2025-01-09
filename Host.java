import java.util.List;



public class Host {
    
    public long map;
    private Carte carte;
    private ListShare<Projectile> projectiles;
    private ListShare<Player> players;
    private ThreadHostConnexion recepteur;
    public static boolean is_close=false;



    Host(int map,double pourcentageObstacle,int nbrMoyenObstacleParCase){
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);
        this.map = carte.getTailleReel(); 
        carte.create_all_initial_obstacle();
        this.players= new ListShare<>();
        this.projectiles= new ListShare<>();
    }    

    public void print_projectile(){
        for (Projectile pro : projectiles) {
            System.out.println(pro.toString());
        }
    }


    //pour le display
    public boolean is_finish(){
        return players.size()==0;
    }

    public ListShare<Projectile> getProjectiles(){
        return projectiles;
    }

    public List<Obstacle> getObstacles(){
        return carte.getObstacles();
    }

    public ListShare<Player> getPlayers(){
        return players;
    }

    public void killAllPlayers(){
        while(players.size()>0) {players.remove(0);}
    }

    public void start(int port){
        recepteur= new ThreadHostConnexion(port,carte,players,projectiles);
        recepteur.start();
    }

    public static void main(String[] args) {
        Host host;
        if( args.length>0){
            System.out.println("manuel ON ... \nOK");
            host = new Host(Integer.parseInt(args[0]), Double.parseDouble(args[1]), Integer.parseInt(args[2])); // Initialisation de la logique
        }
        else{
            System.out.println("manuel OFF ... \nOK");
            host = new Host(20, 0.05, 5); // Initialisation de la logique
        }
        System.out.println("Host start ...\nOK");
        host.start(12345);

    }
}
