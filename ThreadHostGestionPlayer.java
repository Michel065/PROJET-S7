import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ThreadHostGestionPlayer extends Thread {
    protected Carte carte;
    protected ListePartageThread Liste_Thread;
    private List<Projectile> ourprojectiles;
    protected Player ourplayer; // Rayon de base est toujours à 0.5
    private long taille_map = 0;
    private Projectile proj_tmp;
    private float radius_player = (float)0.5;
    protected long  current_time, last_time;
    protected float delta_time; // En secondes

    // Variable de tmp allouée une seule fois
    private CoordFloat tmp_coord_Float = new CoordFloat();

    // Ce qui est visible des autres !!!
    protected AtomicInteger index = new AtomicInteger(0);
    protected AtomicInteger degat_en_attente = new AtomicInteger(0);
    protected ListeAtomicCoord ourprojectilespartagee = new ListeAtomicCoord(30); // 30 projectiles max par joueur
    protected boolean statut_joueur = false; // Juste pour savoir si on est vivant ou pas
    protected int equipe = -1;
    protected CoordFloatAtomic coord_joueur= new CoordFloatAtomic();
    protected AtomicInteger pourcentage_vie= new AtomicInteger();

    ThreadHostGestionPlayer(Carte carte, ListePartageThread Liste_Thread) {
        this.carte = carte;
        this.Liste_Thread = Liste_Thread;
        this.taille_map = carte.getTailleReel();
        this.ourprojectiles = new ArrayList<>();
    }

    public ListeAtomicCoord get_projectile() {
        return ourprojectilespartagee;
    }

    public int get_pourcentage_vie() {
        return pourcentage_vie.get();
    }
    
    public void setIndex(int index) {
        this.index.set(index);
    }

    public void add_degat_joueur(int index) {
        this.degat_en_attente.addAndGet(index);
    }

    public boolean getStatus() {
        return statut_joueur;
    }

    public int getEquipe() {
        return equipe;
    }

    public CoordFloatAtomic getCoordJoueur() {
        return coord_joueur;
    }

    private boolean other_player_is_touch_by_proj(Projectile proj) {
        // boolean touche = false;
        int taille = Liste_Thread.get_size();
        if(taille <= 1) return false;
        for(int i=0; i < taille; i++) {
            if(i != index.get()) {
                ThreadHostToClient tmp = Liste_Thread.recuperer(i);
                if(tmp.getStatus() && (equipe != tmp.getEquipe())) {
                    tmp_coord_Float.set(tmp.getCoordJoueur());
                    if(proj.is_touch_by(tmp_coord_Float, radius_player)) {
                        tmp.add_degat_joueur(proj.getDegat());
                        // touche = true;
                        return true;
                    }
                }
            }
        }
        // return touche;
        return false;
    }

    private boolean player_touch() {
        int taille = Liste_Thread.get_size();
        if(taille <= 1) return false;
        for(int i=0; i < taille; i++) {
            if(i != index.get()) {
                ThreadHostToClient tmp = Liste_Thread.recuperer(i);
                if(tmp.getStatus() && (equipe!=tmp.getEquipe())) {
                    tmp_coord_Float.set(tmp.getCoordJoueur());
                    if(ourplayer.is_touch_in_simu(tmp_coord_Float, radius_player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void update_projectiles() {
        List<Projectile> a_remove= new ArrayList<>();
        float rayon_proj;
        if(ourplayer != null)rayon_proj=ourplayer.get_proj_radius();//sinon on risque des erreurs
        else rayon_proj=(float)0.2;

        for (Projectile projectile : ourprojectiles) {
            projectile.setDeltaTime(delta_time);
            projectile.simu_move();
            if (!projectile.is_alive() || carte.test_collision_rond_obstacle(projectile.get_simu_move(),rayon_proj) || other_player_is_touch_by_proj(projectile)) { 
                a_remove.add(projectile);
            } else {
                projectile.move();
            }
        }
        ourprojectiles.removeAll(a_remove);
        ourprojectilespartagee.iniitialise(ourprojectiles);
    }

    protected void update_player() {
        if(ourplayer != null) {
            
            ourplayer.addHealth(degat_en_attente.getAndSet(0));
            pourcentage_vie.set(ourplayer.get_pourcentage_vie());
            if(ourplayer.getHealth()>0){
                ourplayer.setDeltaTime(delta_time);
                ourplayer.simu_move();
                if (!carte.test_collision_rond_obstacle(ourplayer.get_simu_move(), ourplayer.getRadius()) && !player_touch()) {
                    ourplayer.move();
                    coord_joueur.setCoords(ourplayer.getCoord());
                } 
                else {
                    ourplayer.reset_speed();
                }
            }
            else remode_ourplayer(); 
        }
    }

    protected void remode_ourplayer() {
        statut_joueur = false;
        ourplayer = null;
    }

    protected void create_player() {
        if(statut_joueur) return;
        Random random = new Random();

        /*
        float x, y; // Générés aléatoirement entre 0 et (taille_map - 5)
        x = random.nextFloat() * (taille_map - 5);
        y = random.nextFloat() * (taille_map - 5);

        while(carte.here_obstacle((x + (float)2.5), (y + (float)2.5))) {
            x = random.nextFloat() * (taille_map - 5);
            y = random.nextFloat() * (taille_map - 5);
        }
        ourplayer = new Player(100, x + (float)2.5, y + (float)2.5);
        ourplayer = new Player(100, x, y);
        */
       
        System.out.println("Taille map : " + taille_map);
        // Zone de jeu : (1, 1) - (taille_map - 2, taille_map - 2)
        int x, y;
        x = 1 + random.nextInt((int)taille_map - 3);
        y = 1 + random.nextInt((int)taille_map - 3);

        while(carte.here_obstacle(x + 0.5f, y + 0.5f)) {
            System.out.println("Tentative de génération de joueur en x:" + x + "; y:" + y + "avortée");
            x = 2 + random.nextInt((int)taille_map - 4);
            y = 2 + random.nextInt((int)taille_map - 4);
        }
        ourplayer = new Player(100, x + 0.5f, y + 0.5f);
        System.out.println("Player généré en x:" + x + "; y:" + y);
        statut_joueur = true;
        coord_joueur.setCoords(ourplayer.getCoord());
    }

    protected void tire() {
        if(ourplayer != null) {
            proj_tmp = ourplayer.tire();
            if(proj_tmp != null) {
                ourprojectiles.add(proj_tmp);
                ourprojectilespartagee.ajoute(proj_tmp.get_coord());
            }
        }
    }

    protected boolean is_finish() {
        return Host.is_close || (ourplayer == null && ourprojectiles.size() == 0); // À revoir dans le run
    }

    public abstract void run();
}
