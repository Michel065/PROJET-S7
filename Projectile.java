import javafx.scene.paint.Color;

public class Projectile {
    private Color coul;
    private float speed; // Vitesse
    private float x, y,directionX, directionY;
    private int life=20;
    private float radius=(float)0.4;

    // Constructeur
    public Projectile(Color coul, float speed, float x, float y, float directionX, float directionY) {
        this.coul = coul;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.directionX = directionX;
        this.directionY = directionY;
    }
    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move() {
        this.x += directionX * speed;
        this.y += directionY * speed;
        life--;
    }

    public void simu_move(float[] coord) {
        coord[0] = x + directionX * speed;
        coord[1] = y + directionY * speed;
    }

    @Override
    public String toString() {
        return "Projectile{id='" + coul + "', speed=" + speed + ", position=(" + x + ", " + y + "), direction=(" + directionX + ", " + directionY + ")}";
    }

    public boolean en_vie(){
        return life>0;
    }

    public Color getCouleur() {
        return coul;
    }

    public float getRadius(){
        return radius;
    }
}
