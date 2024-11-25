public class Projectile {
    private int id; // Identifiant unique du projectile
    private float speed; // Vitesse
    private float x, y; // Position actuelle
    private int directionX, directionY; // Direction de déplacement

    // Constructeur
    public Projectile(int id, float speed, float x, float y, int directionX, int directionY) {
        this.id = id;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.directionX = directionX;
        this.directionY = directionY;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move() {
        this.x = directionX * speed;
        this.y = directionY * speed;
    }

    public float[] simu_move() {
        return new float[]{directionX * speed, directionY * speed};
    }

    @Override
    public String toString() {
        return "Projectile{id='" + id + "', speed=" + speed + ", position=(" + x + ", " + y + ")}";
    }
}
