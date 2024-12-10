import java.util.List;
import java.util.Random;


public class Host {
    
    public long map;
    private Carte carte;
    private ListShare<Projectile> projectiles;
    private ListShare<Player> players;
    private ThreadHostConnexion recepteur;
    private Random random = new Random();



    Host(long map,double pourcentageObstacle,int nbrMoyenObstacleParCase){
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);
        this.map = carte.getTailleReel(); 
        carte.create_all_initial_obstacle();
        this.players= new ListShare<>();
        this.projectiles= new ListShare<>();
    }

    public void add_new_projectile(){
            float x,y,speed;
            float dirx,diry;
            x = random.nextFloat() * map;
            y = random.nextFloat() * map;
            while(carte.here_obstacle(x,y)){
                x = random.nextFloat() * map;
                y = random.nextFloat() * map;
            }
            speed = (float)0.2;
            int nbr =100;
            dirx = random.nextInt(nbr);
            diry = random.nextInt(nbr);
            dirx-=nbr/2;
            diry-=nbr/2;
            float norm = (float) Math.sqrt(dirx * dirx + diry * diry);
            if (norm != 0) {
                dirx /= norm;
                diry /= norm;
            }

            projectiles.add(new Projectile(1, speed, x, y, dirx, diry));
    }

    public void genere_projectile(int nbr){
        projectiles=new ListShare<>();
        genere_x_new_projectile(nbr);
    }

    public void genere_x_new_projectile(int nbr){
        for(int i=0;i<nbr;i++){
            add_new_projectile();
        }
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

    //pour la partie avec des robots
    public void start(int port){
        recepteur= new ThreadHostConnexion(port,carte,players,projectiles,true);
        recepteur.start();
    }

    public void killAllPlayers(){

        while(players.size()>0) {players.remove(0);}
    }
}
