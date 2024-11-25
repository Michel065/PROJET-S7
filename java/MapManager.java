import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapManager {
    private Map<String, Case> grid; // Stockage des cases par position
    private List<Projectile> liste; // Liste centrale des projectiles

    public MapManager() {
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
            float newX = proj.getX() + newPos[0];
            float newY = proj.getY() + newPos[1];
            String newKey = generateKey((int) newX, (int) newY);

            // Vérifier si la nouvelle position est un obstacle
            Case destCell = grid.get(newKey);
            if (destCell != null && destCell.isObstacle()) {
                // Détruire le projectile
                projectilesToRemove.add(proj);
                continue;
            }

            // Déplacer le projectile
            String currentKey = generateKey(proj.getX(), proj.getY());
            Case currentCell = grid.get(currentKey);
            if (currentCell != null) {
                currentCell.removeProjectile(proj); // Retirer de l'ancienne cellule
            }

            proj.setPosition(newX, newY);

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

    // Afficher la map
    public void printMap(int taille_map) {
        // Initialiser une matrice vide
        char[][] mapMatrix = new char[taille_map][taille_map];

        // Remplir la matrice avec des espaces (par défaut)
        for (int i = 0; i < taille_map; i++) {
            for (int j = 0; j < taille_map; j++) {
                mapMatrix[i][j] = ' '; // Par défaut, une cellule est vide
            }
        }

        // Parcourir les éléments de la map et les placer dans la matrice
        for (Map.Entry<String, Case> entry : grid.entrySet()) {
            String[] coords = entry.getKey().split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            Case cell = entry.getValue();

            if (cell.isObstacle()) {
                mapMatrix[y][x] = '*'; // Obstacle
            } else if (!cell.getProjectiles().isEmpty()) {
                mapMatrix[y][x] = '+'; // Projectile présent
            }
        }

        // Afficher la matrice
        for (int i = 0; i < taille_map; i++) {
            for (int j = 0; j < taille_map; j++) {
                System.out.print(mapMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Initialiser la map
    public void init(int taille_map) {
        int nbr_proj = 5;       // Nombre de projectiles
        int nbr_obstacle = 20;  // Nombre d'obstacles

        Random random = new Random();

        // Générer des obstacles
        for (int i = 0; i < nbr_obstacle; i++) {
            int x = random.nextInt(taille_map);
            int y = random.nextInt(taille_map);
            addObstacle(x, y); // Ajouter un obstacle
        }

        // Générer des projectiles
        for (int i = 0; i < nbr_proj; i++) {
            int x = random.nextInt(taille_map);
            int y = random.nextInt(taille_map);
            Projectile projectile = new Projectile(i, (float) 0.5, x, y, 1, 0);
            addProjectile(x, y, projectile);
        }
    }

    // Méthode principale
    public static void main(String[] args) {
        int taille_map = 20;    // Taille de la carte
        MapManager mapManager = new MapManager(); // Créer une instance de MapManager
        mapManager.init(taille_map); // Appeler init sur l'instance

        int x = 0;
        while (x < 10) {
            mapManager.move_proj(); // Déplacer les projectiles
            mapManager.printMap(taille_map); // Afficher la carte
            x++;
        }
    }
}
