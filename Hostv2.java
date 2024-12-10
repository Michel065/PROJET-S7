import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

public class Hostv2 {
    private Carte carte;
    private List<Projectile> projectiles;
    public long map;



    Hostv2(long map,double pourcentageObstacle,int nbrMoyenObstacleParCase){
        carte = new Carte(map, pourcentageObstacle, nbrMoyenObstacleParCase);
        this.map = carte.getTailleReel(); 
        carte.create_all_initial_obstacle();
    }

    public void genere_projectile(int nbr){
        projectiles=new ArrayList<>((int)nbr);
        Random random = new Random();
        for(int i=0;i<nbr;i++){
            float x,y,speed;
            float dirx,diry;
            x = random.nextFloat() * map;
            y = random.nextFloat() * map;
            while(carte.here_obstacle(x,y)){
                x = random.nextFloat() * map;
                y = random.nextFloat() * map;
            }
            speed = (float)0.2;
            dirx = random.nextInt(nbr);
            diry = random.nextInt(nbr);
            dirx-=nbr/2;
            diry-=nbr/2;
            float norm = (float) Math.sqrt(dirx * dirx + diry * diry);
            if (norm != 0) {
                dirx /= norm;
                diry /= norm;
            }

            projectiles.add(new Projectile(i, speed, x, y, dirx, diry));
        }
    }

    public void update_projectile() {
        synchronized (projectiles) { // Synchronisation pour éviter les conflits d'accès
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
    }
    

    

    public void print_projectile(){
        for (Projectile pro : projectiles) {
            System.out.println(pro.toString());
        }
    }

    public boolean is_finish(){
        return projectiles.size()==0;
    }

    public List<Projectile> getProjectiles(){
        return projectiles;
    }

    public List<Obstacle> getObstacles(){
        return carte.getObstacles();
    }
}
