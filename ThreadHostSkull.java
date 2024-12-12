import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;



public abstract class ThreadHostSkull extends Thread {
    private Carte carte;
    private ListShare<Player> players;
    private ListShare<Projectile> projectiles;
    private List<Projectile> ourprojectiles;
    protected Player ourplayer;
    private long taille_map=0;
    protected Color coul=Color.GREEN;


    ThreadHostSkull(Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        this.carte=carte;
        this.players=players;
        this.projectiles=projectiles;
        this.taille_map=carte.getTailleReel();
        this.ourprojectiles= new ArrayList<>();
    }
    
    private boolean player_is_touch(Projectile proj){
        for(Player player : players){
            if(player!=ourplayer && !proj.getCouleur().equals(player.getCouleur())){
                return player.is_touch_by(proj);
            }
        }
        return false;
        
    }

    private void update_projectile() {
        float[] coord = new float[2];
        for (Projectile projectile : ourprojectiles) {
            projectile.simu_move(coord);
            if (!projectile.en_vie() || carte.ca_touche_ou_pas(coord[0], coord[1], projectile.getRadius()) || player_is_touch(projectile)) {
                projectiles.remove(projectile);
                
            } else {
                projectile.move();
            }
        }
    }

    private void update_player() {
        float[] coord = new float[2];
        ourplayer.simu_move(coord);
        if (!carte.ca_touche_ou_pas(coord[0], coord[1], ourplayer.getRadius())) {
            ourplayer.move();
        } 
        else{
            ourplayer.reset_speed();
        }
    }

    private void create_player(){
        Random random = new Random();
        float x,y;
        x = random.nextFloat() * taille_map;
        y = random.nextFloat() * taille_map;
        while(carte.here_obstacle(x,y)){
            x = random.nextFloat() * taille_map;
            y = random.nextFloat() * taille_map;
        }
        
        ourplayer= new Player(coul,100,x,y);
        players.add(ourplayer);
        
    }

    private boolean is_closed(){
        return players.size()==0;
    }

    protected void tire(){
        double orientation = ourplayer.getOrientation();
        Projectile tmp =new Projectile(ourplayer.getCouleur(), (float)0.5, ourplayer.getX(), ourplayer.getY(), (float) Math.cos(orientation), (float) Math.sin(orientation));

        projectiles.add(tmp);
        ourprojectiles.add(tmp);
    }

    public void run() { 
        System.out.println("demarage du thread : " + Thread.currentThread().getName()+"!");
        create_player();
        System.out.println("Mode manuel apres 2s:");
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e) {
            System.out.println("Le thread a été interrompu.");
        }
            System.out.println("start:");

        init();
        while(!is_closed()){
            action();
            update_player();
            update_projectile();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }
        }

        System.out.println("fermeture du thread : " + Thread.currentThread().getName()+"!");

    }

    protected abstract void action();
    protected abstract void init();
}
