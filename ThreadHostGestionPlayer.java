import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ThreadHostGestionPlayer extends Thread {
    protected Carte carte;
    protected ListePartageThread Liste_Thread;
    private List<Projectile> ourprojectiles;
    protected Player ourplayer;
    private long taille_map=0;
    private Projectile proj_tmp;

    //ce qui est visible des autres !!!
    private AtomicInteger index = new AtomicInteger(0);
    protected ListeAtomicCoord ourprojectilespartagee= new ListeAtomicCoord(30);//30 projectiles max par joueur
    protected boolean statut_joueur = false; // juste pour savoir si on est vivant ou pas
    protected int equipe=-1;
    protected CoordFloatAtomic coord_joueur;


    ThreadHostGestionPlayer(Carte carte,ListePartageThread Liste_Thread){
        this.carte=carte;
        this.Liste_Thread=Liste_Thread;
        this.taille_map=carte.getTailleReel();
        this.ourprojectiles= new ArrayList<>();
    }
    
    public void setIndex(int index){
        this.index.set(index);
    }

    public boolean getStatus(){
        return statut_joueur;
    }

    public int getEquipe(){
        return equipe;
    }

    private boolean other_player_is_touch_by_proj(Projectile proj){
        boolean touche=false;
        int taille=Liste_Thread.get_size();
        if(taille<=1) return false;
        
        for(int i=0; i<taille;i++){
            if(i!=index.get()){
                ThreadHostToClient tmp=Liste_Thread.recuperer(i);
                if(tmp.getStatus() && (equipe!=tmp.getEquipe())){
                    if(player.is_touch_by(proj)){
                        player.addHealth(proj.getDegat());
                        touche =true;
                    }
                }
            }
        }
        return touche;
        
    }

    private boolean player_touch(){
        for(Player player : players){
            if(player!=ourplayer && (ourplayer.getCouleur()!=player.getEquipe())){
                if(ourplayer.is_touch_in_simu(player)){
                    return true;
                }
            }
        }
        return false;
        
    }

    protected void update_projectile() {
        for (Projectile projectile : ourprojectiles) {
            projectile.simu_move();
            if (!projectile.is_alive() || carte.ca_touche_ou_pas(projectile.get_simu_move(), ourplayer.get_proj_radius()) || other_player_is_touch(projectile)) {
                ourprojectiles.remove(projectile);
                
            } else {
                projectile.move();
            }
        }
        ourprojectilespartagee.iniitialise(ourprojectiles);
    }

    protected void update_player() {
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
        statut_joueur=false;
        ourplayer=null;
    }

    protected void create_player(){
        if(statut_joueur) return;
        Random random = new Random();
        float x,y;
        x = random.nextFloat() * (taille_map-5);
        y = random.nextFloat() * (taille_map-5);
        while(carte.here_obstacle((x+(float)2.5),(y+(float)2.5))){
            x = random.nextFloat() * (taille_map-5);
            y = random.nextFloat() * (taille_map-5);
        }
        ourplayer= new Player(100,x+(float)2.5,y+(float)2.5);
    }


    protected void tire(){
        if(ourplayer != null){
            proj_tmp=ourplayer.tire();
            if(proj_tmp != null){
                ourprojectiles.add(proj_tmp);
                ourprojectilespartagee.ajoute(proj_tmp.getX(), proj_tmp.getY());
            }
        }
    }

    protected boolean is_finish(){
        return Host.is_close || (ourplayer==null && ourprojectiles.size()==0);//a revoir dans le run
    }
    
    public abstract void run();
}
