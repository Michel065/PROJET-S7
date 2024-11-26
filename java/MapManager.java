import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapManager extends JPanel {
    private Map<String, Case> grid; // Stockage des cases par position
    private List<Projectile> liste; // Liste centrale des projectiles
    private int taille;
    private float speedproj=(float)0.5;

    public MapManager(int taille) {
        this.taille = taille;
        grid = new HashMap<>();
        liste = new ArrayList<>();
    }

    // Générer une clé à partir de coordonnées
    private String generateKey(int x, int y) {
        return x + "," + y;
    }

    // Ajouter une Case à la map
    public void addElement(int x, int y, Case value) {
        String key = generateKey(x, y);
        grid.put(key, value);
    }

    // Ajouter un projectile à une Case
    public void addProjectile(int x, int y, Projectile value) {
        String key = generateKey(x, y);
        Case cell = grid.get(key);

        if (cell == null) {
            cell = new Case(false); // Créer une nouvelle case si elle n'existe pas
            grid.put(key, cell);
        }

        cell.addProjectile(value); // Ajouter le projectile à la case
        liste.add(value);          // Ajouter le projectile à la liste centrale
    }

    // Ajouter un obstacle à une Case
    public void addObstacle(int x, int y) {
        String key = generateKey(x, y);
        Case cell = grid.get(key);

        if (cell == null) {
            cell = new Case(true); // Créer une nouvelle case comme obstacle
            grid.put(key, cell);
        } else {
            cell.setObstacle(true); // Transformer une case existante en obstacle
        }
    }

    // Déplacer les projectiles
    public void move_proj() {
        List<Projectile> projectilesToRemove = new ArrayList<>(); // Liste des projectiles à détruire

        for (Projectile proj : liste) {
            // Simuler le déplacement
            float[] newPos = proj.simu_move();
            String newKey = generateKey((int) newPos[0], (int) newPos[1]);

            // Vérifier si la nouvelle position est un obstacle
            Case destCell = grid.get(newKey);
            if (destCell != null && destCell.isObstacle()) {
                // Supprimer le projectile de la cellule actuelle
                String currentKey = generateKey((int) proj.getX(), (int) proj.getY());
                Case currentCell = grid.get(currentKey);
                if (currentCell != null) {
                    currentCell.removeProjectile(proj);
                }

                // Marquer le projectile pour suppression
                projectilesToRemove.add(proj);
                continue;
            }

            // Déplacer le projectile
            String currentKey = generateKey((int) proj.getX(), (int) proj.getY());
            Case currentCell = grid.get(currentKey);
            if (currentCell != null) {
                currentCell.removeProjectile(proj); // Retirer de l'ancienne cellule
            }

            proj.move();

            // Ajouter le projectile à la nouvelle cellule
            if (destCell == null) {
                destCell = new Case(false); // Créer une nouvelle case si elle n'existe pas
                grid.put(newKey, destCell);
            }
            destCell.addProjectile(proj);
        }

        // Supprimer les projectiles détruits
        liste.removeAll(projectilesToRemove);
    }
    public void setspeedproj(double val){
        speedproj=(float)val;
    }
    // Initialiser la map
    public void init() {
        int nbr_proj = 10;       // Nombre de projectiles
        int nbr_obstacle = 20;  // Nombre d'obstacles

        Random random = new Random();

        // Générer des obstacles
        for (int i = 0; i < nbr_obstacle; i++) {
            int x = random.nextInt(taille);
            int y = random.nextInt(taille);
            addObstacle(x, y); // Ajouter un obstacle
        }

        for(int i=0;i<=taille;i++){
            addObstacle(0, i);
            addObstacle(i, 0);
            addObstacle(i, taille);
            addObstacle(taille, i);
        }

        // Générer des projectiles
        for (int i = 0; i < nbr_proj; i++) {
            int x = random.nextInt(taille);
            int y = random.nextInt(taille);
            // Générer une direction aléatoire non nulle
            int directionX, directionY;
            do {
                directionX = random.nextInt(3) - 1; // Génère -1, 0 ou 1
                directionY = random.nextInt(3) - 1; // Génère -1, 0 ou 1
            } while (directionX == 0 && directionY == 0); // Assure que la direction n'est pas (0, 0)

            Projectile projectile = new Projectile(i,  speedproj, x, y, directionX, directionY);
            addProjectile(x, y, projectile);
        }
    }

    // Dessiner la carte dans la fenêtre
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        int cellSize = 20; // Taille d'une case
    
        // Dessiner la grille
        for (int i = 0; i <= taille; i++) {
            for (int j = 0; j <= taille; j++) {
                g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    
        // Dessiner les éléments
        for (Map.Entry<String, Case> entry : grid.entrySet()) {
            String[] coords = entry.getKey().split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            Case cell = entry.getValue();
    
            if (cell.isObstacle()) {
                g.setColor(Color.BLACK);
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    
        // Dessiner les projectiles avec leurs coordonnées float
        for (Projectile proj : liste) {
            g.setColor(Color.RED);
            int drawX = (int) (proj.getX() * cellSize); // Conversion float -> int pour l'affichage
            int drawY = (int) (proj.getY() * cellSize); // Conversion float -> int pour l'affichage
            g.fillOval(drawX, drawY, cellSize, cellSize);
        }
    
    
    }

    // Méthode principale
    public static void main(String[] args) {
        int taille_map = 20; // Taille de la carte
        MapManager mapManager = new MapManager(taille_map); // Créer une instance de MapManager
        mapManager.setspeedproj(0.1);
        mapManager.init(); // Initialiser la carte

        JFrame frame = new JFrame("Map Manager"); // Fenêtre Swing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500); // Taille de la fenêtre
        frame.add(mapManager); // Ajouter le composant MapManager
        frame.setVisible(true);
        // Boucle principale
        new Timer(20, e -> {
            if (!mapManager.liste.isEmpty()) {
                mapManager.move_proj(); // Déplacer les projectiles
                mapManager.repaint(); // Repeindre la fenêtre
            } else {
                ((Timer) e.getSource()).stop(); // Arrêter le timer quand il n'y a plus de projectiles
                System.out.println("Tous les projectiles ont été détruits !");
            }
        }).start();
    }
}
