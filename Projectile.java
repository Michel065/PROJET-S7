import javafx.scene.paint.Color;

public class Projectile extends Rond {
    private float directionX, directionY;
    private int degat;

    // Constructeur
    public Projectile(Color coul, float speed,int life, float radius,int proj_degat, float x, float y, float directionX, float directionY) {
        super(life, radius, x, y);
        this.coul=coul;
        this.speed = speed;
        this.degat=proj_degat;
        this.directionX=directionX;
        this.directionY=directionY;
    }

    @Override
    public void move() {
        this.x += directionX * speed;
        this.y += directionY * speed;
        health--;
    }

    @Override
    public void simu_move() {
        coord_simu[0] = x + directionX * speed;
        coord_simu[1] = y + directionY * speed;
        
        
    }

    @Override
    public String toString() {
        return name+"{" +
                "couleur=" + coul +
                ", speed=" + speed +
                ", life=" + health +
                ", radius=" + radius +
                ", degat=" + degat +
                ", position=(" + x + ", " + y + ")" +
                ", direction=(" + directionX + ", " + directionY + ")" +
                '}';
    }

    public int getDegat(){
        return degat;
    }
}
