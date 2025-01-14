import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carte {

    private long taille_origine,taille_reel,nbr_obstacle_totale,longeur_list_obstacle;
    private int largeur_case,largeur_matrice,nbr_moyen_obstacle_par_case,nbr_max_obstacle_par_case;
    private double pourcentage_obstacle;
    private int nbr_actuel_obstacle;

    private MatriceCarre<CoordInt> carte;
    private List<Obstacle> obstacles;
    
    public Carte(long taille, double pourcentage_obstacle,int nbr_moyen_obstacle_par_case) {
        this.taille_origine=taille;
        this.nbr_moyen_obstacle_par_case = nbr_moyen_obstacle_par_case;
        this.pourcentage_obstacle = pourcentage_obstacle;
        
        init_info();
        init_liste();
        init_matrice();
        ajoute_bordure();
        
    }

    private void init_info(){
        this.nbr_obstacle_totale=(long) Math.ceil(pourcentage_obstacle*taille_origine*taille_origine);
        this.largeur_case=(int) Math.ceil(taille_origine/Math.sqrt(nbr_obstacle_totale/nbr_moyen_obstacle_par_case));
        this.largeur_matrice=(int) Math.ceil((taille_origine+1)/largeur_case);
        this.taille_reel=(long)(largeur_case*largeur_matrice);
        this.longeur_list_obstacle=largeur_case*largeur_matrice*largeur_matrice;
        this.nbr_max_obstacle_par_case=Math.max(largeur_case*largeur_case,nbr_moyen_obstacle_par_case*2);
        carte=new MatriceCarre<CoordInt>(largeur_matrice);
        obstacles = new ArrayList<>((int)longeur_list_obstacle);
    }

    private void init_liste(){
        for(int i=0;i<nbr_max_obstacle_par_case*largeur_matrice*largeur_matrice;i++){
            obstacles.add(null);
        }
    }

    private void init_matrice(){
        int indice_dans_liste=0;
        for(int x=0;x<largeur_matrice;x++){
            for(int y=0;y<largeur_matrice;y++){
                carte.set(x,y,new CoordInt(indice_dans_liste,indice_dans_liste));
                indice_dans_liste+=(largeur_case*largeur_case);
            }
        }
    }

    public void create_all_initial_obstacle(){
        Random random = new Random();
        int nbr_obstacle=(int)(taille_reel*taille_reel*pourcentage_obstacle);
        for(int i=0;i<nbr_obstacle;i++){
            int x = random.nextInt((int)taille_reel);
            int y = random.nextInt((int)taille_reel);
            while(!ajoute_obstacle(x,y)){
                x = random.nextInt((int)taille_reel);
                y = random.nextInt((int)taille_reel);
            }
        }
        
    }

    private boolean Obstacle_existe_deja(int x,int y,CoordInt couple){
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.getx()==x && tmp.gety()==y){
                return true;
            }
        }
        return false;
    }


    public boolean ajoute_obstacle(int x,int y){
        CoordInt couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille(x,y));
        if(!Obstacle_existe_deja(x,y,couple) && couple.y-couple.x<nbr_max_obstacle_par_case){
            obstacles.set(couple.y, new Obstacle(x, y));
            couple.y++;
            nbr_actuel_obstacle++;
            return true;
        }
        return false;
    }

    public void ajoute_bordure(){
        for(int i=0;i<taille_reel;i++){
            ajoute_obstacle(0,i);
            ajoute_obstacle(i,0);
            ajoute_obstacle((int)taille_reel-1,i);
            ajoute_obstacle(i,(int)taille_reel-1);
        }
    }


    public boolean retire_obstacle(int x,int y){
        CoordInt couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille(x,y));
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.getx()==x && tmp.gety()==y){
                couple.y--;
                obstacles.remove(i);
                nbr_actuel_obstacle--;
                return true;
            }
        }
        return false;
    }

    private float calcul_distance_carre_obstacle(float x, float y, CoordInt obstale){
        float xx=Math.abs(x-(obstale.x));
        float yy=Math.abs(y-(obstale.y));
        return xx*xx+yy*yy;
    }

    private float distance_euclidienne_carre(float x1, float y1, float x2, float y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public boolean ca_touche_ou_pas(CoordFloat coord, float radius) {
        CoordInt tmp1 = get_Coordonnees_De_Reel_Vers_Grille((int) coord.x, (int) coord.y);
        if (tmp1 == null) return true;
        CoordInt couple = carte.get(tmp1);

        float[] distances = new float[4];
        CoordFloat[] sommets = new CoordFloat[4];

        int minimini = 0, mini = -1;

        float cx = coord.x, cy = coord.y;
        float x1, x2, y1, y2;
        float dx, dy;
        float fx, fy;
        float t;
        float px, py;

        // Parcours des obstacles
        for (int i = couple.x; i < couple.y; i++) {
            Obstacle tmp = obstacles.get(i);
            int x = tmp.getx();
            int y = tmp.gety();

            // Calcul des sommets du carré représentant l'obstacle
            sommets[0] = new CoordFloat(x - 0.5f, y - 0.5f);
            sommets[1] = new CoordFloat(x + 0.5f, y - 0.5f);
            sommets[2] = new CoordFloat(x + 0.5f, y + 0.5f);
            sommets[3] = new CoordFloat(x - 0.5f, y + 0.5f);

            // Calcul des distances au carré entre chaque sommet et le centre du cercle
            distances[0] = distance_euclidienne_carre(sommets[0].x, sommets[0].y, cx, cy);
            distances[1] = distance_euclidienne_carre(sommets[1].x, sommets[1].y, cx, cy);
            distances[2] = distance_euclidienne_carre(sommets[2].x, sommets[2].y, cx, cy);
            distances[3] = distance_euclidienne_carre(sommets[3].x, sommets[3].y, cx, cy);

            // Trouver les deux indices des sommets les plus proches du cercle
            minimini = 0; // L'indice du sommet le plus proche du cercle
            mini = -1; // L'indice du deuxième sommet le plus proche du cercle

            if(distances[1] < distances[0]) {
                minimini = 1;
                mini = 0;
            }
            else {
                mini = 1;
            }

            // À ce stade, distances[minimini] < distances[mini], avec minimini et mini des éléments de {0, 1}

            for(int j=2; j<4; j++) {
                if(distances[j] < distances[minimini]) {
                    mini = minimini;
                    minimini = j;
                }
                else if(distances[j] < distances[mini]) {
                    mini = j;
                }
            }

            // sommets[minimini] et sommets[mini] définissent les bords du segment le plus proche du cercle
            
            x1 = sommets[minimini].x;
            x2 = sommets[mini].x;
            y1 = sommets[minimini].y;
            y2 = sommets[mini].y;

            dx = x2 - x1;
            dy = y2 - y1;

            fx = cx - x1;
            fy = cy - y1;

            t = (fx * dx + fy * dy) / (dx * dx + dy * dy); // Ratio de projection du centre du cercle sur le segment

            // Si la projection tombe en dehors du segment, on vérifie les extrémités
            if (t < 0) {
                if (distances[minimini] <= radius * radius) return true;
            } else if (t > 1) {
                if (distances[mini] <= radius * radius) return true;
            } else {
                // Si la projection est dans le segment, on vérifie la distance au point projeté
                px = x1 + t * dx;
                py = y1 + t * dy;

                if (distance_euclidienne_carre(cx, cy, px, py) <= radius * radius) return true;
            }
        }

        return false;
    }

    public boolean here_obstacle(float x,float y){
        CoordInt couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille((int)x,(int)y));
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.is_egual((int)x,(int)y)){
                return true;
            }
        }
        return false;
    }


    public void printinfo(){
        System.out.println("____Carte____");
        System.out.println("largeur d'orgine:"+taille_origine);
        System.out.println("nbr moyen d'obstacle par case de la matrice:"+nbr_moyen_obstacle_par_case);
        System.out.println("le fameux taux:"+pourcentage_obstacle);
        System.out.println("nbr d'obstacle calcule:"+nbr_obstacle_totale);
        System.out.println("largeur d'une case de la matrice:"+largeur_case);
        System.out.println("largeur de la matrice:"+largeur_matrice);
        System.out.println("nbr de cases d'origine:"+taille_origine*taille_origine);
        System.out.println("nouveau nbr de case:"+taille_reel*taille_reel);
        System.out.println("soit reel>origine ?:"+(taille_origine*taille_origine>=taille_reel*taille_reel));
        System.out.println("nbr d'obstacle :"+nbr_actuel_obstacle);
        System.out.println("____________");
    }

    public void printliste_obstacle(){
        System.out.println("____liste____");
        System.out.print("[");
        int i=0;
        for(Obstacle obstacle : obstacles){
            if(obstacle!=null) System.out.print(i+",");
            else System.out.print(" ,");
        }
        
        System.out.println("]");
    }


    public void print_nbr_obstacle_par_case_matrice() {
        System.out.println("Nombre d'obstacles par case dans la matrice :");
        for (int x = 0; x < largeur_matrice; x++) {
            for (int y = 0; y < largeur_matrice; y++) {
                CoordInt couple = carte.get(x, y);
                int nbr_obstacles = couple.y - couple.x; // Nombre d'obstacles dans cette case
                System.out.print(nbr_obstacles + "\t"); // Affiche avec tabulation
            }
            System.out.println(); // Saut de ligne pour chaque ligne de la matrice
        }
    }
    

    public double getLargeurCase() {
        return largeur_case;
    }

    public CoordInt get_Coordonnees_De_Reel_Vers_Grille(int x, int y) {

        int xx=(x / largeur_case),yy=(y / largeur_case);
        if(xx>=largeur_matrice || yy>=largeur_matrice || xx<0 || yy<0) return null;

        return new CoordInt(xx,yy);
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }
    
    public long getTailleReel(){
        return taille_reel;
    }

    public String stringifie(){
        String msg="taille:"+taille_origine+"/pourc_obst:"+pourcentage_obstacle+"/nbrmoyenobstacle:"+nbr_moyen_obstacle_par_case+"/";
        
        for (Obstacle ob : obstacles) {
            if(ob!=null){
                msg+=ob.stringifi()+",";
            }
        }
        msg+="end";
        return msg;
    }

    public Carte(String msg){
        /*
         * le msg doit etre de type taille:val/lepourcentage d'obstacle:val/le nbr de obstales_par case/liste des obstacle
         * avec la liste des obstacle sous former de exemple : 1:2,2:5,2:4,end
         */
        String[]liste_info=msg.split("/");
        
        this.taille_origine=Long.parseLong(liste_info[0].split(":")[1]);
        this.pourcentage_obstacle=Double.parseDouble(liste_info[1].split(":")[1]);
        this.nbr_moyen_obstacle_par_case=Integer.parseInt(liste_info[2].split(":")[1]);

        init_info();
        init_liste();
        init_matrice();

        // Traitement des points dans `liste_info[3]`
        String[] liste_point = liste_info[3].split(",");
        int i = 0;

        while (i < liste_point.length && !liste_point[i].equals("end")) {
            String[] couple = liste_point[i].split(":");
            ajoute_obstacle(Integer.parseInt(couple[0]), Integer.parseInt(couple[1]));
            i++;
        }
    }
}
