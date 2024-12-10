public class Player {
    private int id; // Identifiant unique du joueur
    private int health; // Points de vie
    private float x, y; // Position dans la grille

    public Player(int id, int health, float x, float y) {
        this.id = id;
        this.health = health;
        this.x = x;
        this.y = y;
    }

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

    public float getX() {
        return x;
    }

    public float getY() {
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
