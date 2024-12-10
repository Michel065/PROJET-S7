import java.net.Socket;
import java.util.ArrayList;
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
        this.ourprojectiles= new ArrayList<>();
    }
    
    public void update_projectile() {
        float[] coord = new float[2];
        for (Projectile projectile : ourprojectiles) {
            projectile.simu_move(coord);
            if (carte.ca_touche_ou_pas(coord[0], coord[1], (float) 0.5)) {
                projectiles.remove(projectile);
                
            } else {
                projectile.move();
            }
        }
    }

    public void update_player() {
        float[] coord = new float[2];
        ourplayer.simu_move(coord);
        if (!carte.ca_touche_ou_pas(coord[0], coord[1], (float) 0.6)) {
            ourplayer.move();
        } 
    }

    private void create_player(){
        int id=0;
        if(players.size()!=0){
            System.out.println("id:"+id);
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

    private boolean is_closed(){
        return players.size()==0;
    }

    private void tire(){
        double orientation = ourplayer.getOrientation();
        Projectile tmp =new Projectile(1, (float)0.5, ourplayer.getX(), ourplayer.getY(), (float) Math.cos(orientation), (float) Math.sin(orientation));

        projectiles.add(tmp);
        ourprojectiles.add(tmp);
    }

    private void action_alea(){
        float val = random.nextFloat();
        if(val<0.1){
            //System.out.println("thread : " + Thread.currentThread().getName() +" avance!");
            ourplayer.addToSpeed((float)0.4);
        }
        else if(val>0.97){
            //System.out.println("thread : " + Thread.currentThread().getName() +" recule!");
            ourplayer.addToSpeed((float)-0.4);
        }

        val = random.nextFloat();
        if(val<0.2){
            //System.out.println("thread : " + Thread.currentThread().getName() +" tourne +10!");
            ourplayer.rotate(10);
        }
        /*
        else if(val>0.8){
            //System.out.println("thread : " + Thread.currentThread().getName() +" tourne -10!");
            ourplayer.rotate(-10);
        }*/

        val = random.nextFloat();
        if(val<0.1){
            //System.out.println("thread : " + Thread.currentThread().getName() +" tire un projectile!");
            tire();
        }

    }

    public void run() { 
        //imaginer un system de comm avec un vrai client    
        //reception de msg/envoie, ...

        System.out.println("demarage du thread : " + Thread.currentThread().getName()+"!");
        create_player();

        while(!is_closed()){
            action_alea();
            update_player();
            update_projectile();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Gère l'interruption du thread (obligatoire)
                System.out.println("Le thread a été interrompu.");
            }
        }

        System.out.println("fermeture du thread : " + Thread.currentThread().getName()+"!");

    }
}
