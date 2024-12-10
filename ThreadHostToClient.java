import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class ThreadHostToClient extends Thread {
    private Carte carte;
    private ListShare<Player> players;
    private ListShare<Projectile> projectiles;
    private List<Projectile> ourprojectiles;
    private Player ourplayer;
    private Socket clientSocket=null;
    private Random random = new Random();
    private long taille_map=0;


    ThreadHostToClient(Socket client,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        this.clientSocket=client;
        this.carte=carte;
        this.players=players;
        this.projectiles=projectiles;

        this.taille_map=carte.getTailleReel();
    }
    
    public void update_projectile() {
            Iterator<Projectile> iterator = projectiles.iterator();
            float[] coord = new float[2];
            while (iterator.hasNext()) {
                Projectile projectile = iterator.next();
                projectile.simu_move(coord);
                if (carte.ca_touche_ou_pas(coord[0], coord[1], (float) 0.5)) {
                    iterator.remove(); // Supprime directement l'élément de manière sûre
                } else {
                    projectile.move();
                }
            }
        
    }

    private void create_player(){
        int id=0;
        if(players.size()!=0){
            id=players.get(players.size() - 1).getId();
            id++;
        }
        float x,y;
        x = random.nextFloat() * taille_map;
        y = random.nextFloat() * taille_map;
        while(carte.here_obstacle(x,y)){
            x = random.nextFloat() * taille_map;
            y = random.nextFloat() * taille_map;
        }
        
        ourplayer= new Player(id,100,x,y);
        players.add(ourplayer);
        
    }


    public void run() { 
        //imaginer un system de comm avec un vrai client    
        //reception de msg/envoie, ...
        create_player();

        

        
    }
}
