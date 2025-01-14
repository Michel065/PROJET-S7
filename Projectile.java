
public class Projectile extends Rond {
    private float directionX, directionY;
    private int degat;

    // Constructeur
    public Projectile(float speed, int life, float radius, int proj_degat, float x, float y, float directionX, float directionY) {
        super(life, radius, x, y);
        this.speed = speed;
        this.degat = proj_degat;
        this.directionX = directionX;
        this.directionY = directionY;
    }

    @Override
    public void move() {
        coord.x += directionX * speed;
        coord.y += directionY * speed;
        health--;
    }

    @Override
    public void simu_move() {
        coord_simu.set(coord.x + directionX * speed, coord.y + directionY * speed);        
    }

    @Override
    public String toString() {
        return name+"{" +
                ", speed=" + speed +
                ", life=" + health +
                ", radius=" + radius +
                ", degat=" + degat +
                ", position=(" + coord.x + ", " + coord.y + ")" +
                ", direction=(" + directionX + ", " + directionY + ")" +
                '}';
    }

    public int getDegat(){
        return degat;
    }
}