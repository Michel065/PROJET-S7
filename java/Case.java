import java.util.ArrayList;
import java.util.List;

public class Case {
    private boolean isObstacle; // Indique si c'est un obstacle
    private List<Player> players; // Liste des joueurs dans la cellule
    private List<Projectile> projectiles; // Liste des projectiles dans la cellule

    // Constructeur
    public Case(boolean isObstacle) {
        this.isObstacle = isObstacle;
        this.players = new ArrayList<>();
        this.projectiles = new ArrayList<>();
    }

    // Gestion des obstacles
    public boolean isObstacle() {
        return isObstacle;
    }

    public void setObstacle(boolean isObstacle) {
        this.isObstacle = isObstacle;
    }

    // Gestion des joueurs
    public void addPlayer(Player player) {
        
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    // Gestion des projectiles
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void removeProjectile(Projectile projectile) {
        projectiles.remove(projectile);
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public String toString() {
        return "Case{" +
                "isObstacle=" + isObstacle +
                ", players=" + players +
                ", projectiles=" + projectiles +
                '}';
    }
}
