import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carte {
    private int taille,taille_reel,nbr_max_obstacle_par_case;
    private double pourcentage_obstacle,ratio_convertion;
    private MatriceCarre<Tuple> carte;
    private List<Obstacle> obstacles;
    
    public Carte(int taille_reel, double pourcentage_obstacle,int nbr_moyen_obstacle_par_case,int nbr_max_obstacle_par_case) {
        this.taille_reel=taille_reel;
        this.taille = (int) Math.ceil(Math.sqrt((taille_reel * taille_reel * pourcentage_obstacle) / nbr_moyen_obstacle_par_case));

        this.nbr_max_obstacle_par_case=nbr_max_obstacle_par_case;

        this.pourcentage_obstacle = pourcentage_obstacle;
        ratio_convertion = (double) this.taille_reel / this.taille;

        carte=new MatriceCarre<Tuple>(taille);

        obstacles = new ArrayList<>(nbr_max_obstacle_par_case*taille*taille);
        init_liste();

        init_matrice();
        
    }

    private void init_liste(){
        for(int i=0;i<nbr_max_obstacle_par_case*taille*taille;i++){
            obstacles.add(null);
        }
    }

    private void init_matrice(){
        int indice_dans_liste=0;
        for(int x=0;x<taille;x++){
            for(int y=0;y<taille;y++){
                carte.set(x,y,new Tuple(indice_dans_liste,indice_dans_liste));
                indice_dans_liste+=nbr_max_obstacle_par_case;
            }
        }
    }

    public void create_all_initial_obstacle(){
        Random random = new Random();
        int nbr_obstacle=(int)(taille_reel*taille_reel*pourcentage_obstacle);
        for(int i=0;i<nbr_obstacle;i++){
            int x = random.nextInt(taille_reel);
            int y = random.nextInt(taille_reel);
            while(!ajoute_obstacle(x,y)){
                x = random.nextInt(taille_reel);
                y = random.nextInt(taille_reel);
            }
        }
    }

    private boolean Obstacle_existe_deja(int x,int y,Tuple couple){
        for(int i =couple.x;i<couple.y;i++){
            Obstacle tmp=obstacles.get(i);
            if(tmp.x==x && tmp.y==y){
                return true;
            }
        }
        return false;
    }


    private boolean ajoute_obstacle(int x,int y){
        Tuple couple = carte.get(get_Coordonnees_De_Reel_Vers_Grille(x,y));
        
        if(couple.y-couple.x<nbr_max_obstacle_par_case && !Obstacle_existe_deja(x,y,couple)){
            obstacles.set(couple.y, new Obstacle(x, y));
            couple.y++;
            return true;
        }
        return false;
    }

    private boolean retire_obstacle(int x,int y,int id){

        return true;
    }

    public void printinfo(){
        System.out.println("____Carte____");
        System.out.println("taille d'origine:"+taille_reel);
        System.out.println("taille Mat:"+taille);
        System.out.println("largeur case:"+ (int)Math.ceil(getLargeurCase()));

        System.out.println("nbr Obstacle par case:"+nbr_max_obstacle_par_case);
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
        for (int x = 0; x < taille; x++) {
            for (int y = 0; y < taille; y++) {
                Tuple couple = carte.get(x, y);
                int nbr_obstacles = couple.y - couple.x; // Nombre d'obstacles dans cette case
                System.out.print(nbr_obstacles + "\t"); // Affiche avec tabulation
            }
            System.out.println(); // Saut de ligne pour chaque ligne de la matrice
        }
    }
    

    public double getLargeurCase() {
        return ratio_convertion=(double) taille_reel / taille;
    }

    public Tuple get_Coordonnees_De_Reel_Vers_Grille(int x, int y) {
        return new Tuple((int) Math.floor(x / ratio_convertion),(int) Math.floor(y / ratio_convertion));
    }
    
    public void display(int windowWidth, int windowHeight, boolean showGrid) {
        // Création de la fenêtre
        JFrame frame = new JFrame("Carte des Obstacles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(windowWidth, windowHeight); // Taille personnalisée
    
        // Centrer la fenêtre sur l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - windowWidth) / 2;
        int y = (screenSize.height - windowHeight) / 2;
        frame.setLocation(x, y);
    
        // Création d'un panneau personnalisé pour dessiner
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
    
                // Taille d'une case dans la fenêtre et taille des cercles
                double caseWidthInOrigin = windowWidth / (double) taille_reel;
                double caseHeightInOrigin = windowHeight / (double) taille_reel;

                // Dessiner les lignes de la matrice si demandé
                if (showGrid) {
                    g2d.setColor(Color.BLACK); // Couleur des lignes
                    for (int i = 0; i <= taille; i++) {
                        // Lignes verticales
                        int x = (int) (i * (windowWidth / (double) taille));
                        g2d.drawLine(x, 0, x, windowHeight);
    
                        // Lignes horizontales
                        int y = (int) (i * (windowHeight / (double) taille));
                        g2d.drawLine(0, y, windowWidth, y);
                    }
                }
    
                double taille_case_pour=0.9;
                // Dessiner les obstacles
                for (Obstacle obstacle : obstacles) {
                    if (obstacle != null) {
                        // Mise à l'échelle des coordonnées des obstacles
                        g.setColor(Color.RED);
    
                        // Convertir la position float en position pour l'affichage
                        int drawX = (int) ((obstacle.x+0.05+0.04*(1-taille_case_pour)*10) * caseWidthInOrigin); 
                        int drawY = (int) ((obstacle.y+0.05+0.04*(1-taille_case_pour)*10) * caseHeightInOrigin);

                        // Dessiner un carré de la taille de la case
                        g.fillRect(drawX, drawY, (int) (caseWidthInOrigin*taille_case_pour), (int)(caseHeightInOrigin*taille_case_pour));

                    }
                }
            }
        };
    
        frame.add(panel);
        frame.setVisible(true);
    }
}
