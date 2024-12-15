import java.util.List;
import javafx.stage.Stage;



public class Host {
    
    public long map;
    private Carte carte;
    private ListShare<Projectile> projectiles;
    private ListShare<Player> players;
    private ThreadHostConnexion recepteur;
    public static boolean is_close=false;



    Host(long map,double pourcentageObstacle,int nbrMoyenObstacleParCase){
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

    //pour la partie avec des robots
    public void start(Stage primaryStage){
        int nbr=1;
        ThreadHostManuel recepteur= new ThreadHostManuel(primaryStage,carte,players,projectiles);
        recepteur.start();
        for(int i=0;i<nbr;i++){
            ThreadHostAlea rece= new ThreadHostAlea(carte,players,projectiles);
            rece.start();
        }
    }

    public void start(int port,boolean bo){
        if(bo){
            recepteur= new ThreadHostConnexion(port,carte,players,projectiles);
            recepteur.start();
        }
        else{
            ThreadHostAlea rece= new ThreadHostAlea(carte,players,projectiles);
            rece.start();
        }
    }

    
}
