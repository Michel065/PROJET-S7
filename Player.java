public class Player {
    private int id; // Identifiant unique du joueur
    private int health; // Points de vie
    private int x, y; // Position dans la grille

    // Constructeur
    public Player(int id, int health, int x, int y) {
        this.id = id;
        this.health = health;
        this.x = x;
        this.y = y;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Player{id='" + id + "', health=" + health + ", position=(" + x + ", " + y + ")}";
    }
}
