import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;



public abstract class ThreadHostSkull extends Thread {
    protected Carte carte;
    private ListShare<Player> players;
    private ListShare<Projectile> projectiles;
    private List<Projectile> ourprojectiles;
    protected Player ourplayer;
    private long taille_map=0;
    private Projectile proj_tmp;
    private List<Projectile> list_projectile_a_supp;


    ThreadHostSkull(Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        this.carte=carte;
        this.players=players;
        this.projectiles=projectiles;
        this.taille_map=carte.getTailleReel();
        this.ourprojectiles= new ArrayList<>();
    }
    
    private boolean other_player_is_touch(Projectile proj){
        boolean touche=false;
        for(Player player : players){
            if(player!=ourplayer && !proj.getCouleur().equals(player.getCouleur())){
                if(player.is_touch_by(proj)){
                    player.addHealth(proj.getDegat());
                    touche =true;
                }
            }
        }
        return touche;
        
    }

    private boolean player_touch(){
        for(Player player : players){
            if(player!=ourplayer && !ourplayer.getCouleur().equals(player.getCouleur())){
                if(ourplayer.is_touch_in_simu(player)){
                    return true;
                }
            }
        }
        return false;
        
    }

    private void update_projectile() {
        list_projectile_a_supp =new ArrayList<>();
        for (Projectile projectile : ourprojectiles) {
            projectile.simu_move();
            if (!projectile.is_alive() || carte.ca_touche_ou_pas(projectile.get_simu_move(), projectile.getRadius()) || other_player_is_touch(projectile)) {
                list_projectile_a_supp.add(projectile);
                
            } else {
                projectile.move();
            }
        }
        ourprojectiles.removeAll(list_projectile_a_supp);
        projectiles.remove(list_projectile_a_supp);

    }

    private void update_player() {
        if(ourplayer !=null){
            if(ourplayer.getHealth()>0){
                ourplayer.simu_move();
                if (!carte.ca_touche_ou_pas(ourplayer.get_simu_move(), ourplayer.getRadius()) && !player_touch() ) {
                    ourplayer.move();
                } 
                else{
                    ourplayer.reset_speed();
                }
            }
            else remode_ourplayer(); 
        }
    }

    protected void remode_ourplayer(){
        players.remove(ourplayer);
        ourplayer=null;
    }

    private void create_player(){
        Random random = new Random();
        float x,y;
        x = random.nextFloat() * (taille_map-5);
        y = random.nextFloat() * (taille_map-5);
        while(carte.here_obstacle((x+(float)2.5),(y+(float)2.5))){
            x = random.nextFloat() * (taille_map-5);
            y = random.nextFloat() * (taille_map-5);
        }
        ourplayer= new Player(100,x+(float)2.5,y+(float)2.5);
        players.add(ourplayer);
        
    }


    protected void tire(){
        if(ourplayer != null){
            proj_tmp=ourplayer.tire();
            if(proj_tmp != null){
                projectiles.add(proj_tmp);
                ourprojectiles.add(proj_tmp);
            }
        }
    }

    private boolean is_finish(){
        return Host.is_close || (ourplayer==null && ourprojectiles.size()==0);
    }

    public void run() { 
        System.out.println("demarage du thread : " + Thread.currentThread().getName()+"!");
        create_player();
        System.out.println("Mode manuel apres 2s:");
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            System.out.println("Le thread a été interrompu.");
        }
        System.out.println("start:");

        init();
        while(!is_finish()){
            action();
            update_projectile();
            update_player();
            try {
                Thread.sleep(45);
            } catch (InterruptedException e) {
                System.out.println("Le thread a été interrompu.");
            }
        }
        finish();
        System.out.println("fermeture du thread: " + Thread.currentThread().getName()+"!");

    }

    protected abstract void action();
    protected abstract void init();
    protected abstract void finish();
}
