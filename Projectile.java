
public class Projectile extends Rond {
    private float directionX, directionY;
    private int degat;
    private long creation_time;
    private float duree_de_vie;

    // Constructeur
<<<<<<< HEAD
    public Projectile(float speed, int life, float radius, int proj_degat, float x, float y, float directionX, float directionY) {
        super(life, radius, x, y);
        this.speed = speed;
        this.degat = proj_degat;
        this.directionX = directionX;
        this.directionY = directionY;
=======
    public Projectile(float speed,float duree_de_vie, float radius,int proj_degat, float x, float y, float directionX, float directionY) {
        super(radius, x, y);
        this.duree_de_vie=duree_de_vie*1000*1000*1000;
        this.speed = speed;
        this.degat=proj_degat;
        this.directionX=directionX;
        this.directionY=directionY;
        this.creation_time=System.nanoTime();
>>>>>>> origin/Gabriel
    }

    @Override
    public void move() {
        coord.x += directionX * speed * delta_time;
        coord.y += directionY * speed * delta_time;
    }

    @Override
    public void simu_move() {
        coord_simu.set(coord.x + directionX * speed * delta_time, coord.y + directionY * speed * delta_time);      
    }

    @Override
    public String toString() {
        return name+"{" +
                ", speed=" + speed +
                ", radius=" + radius +
                ", degat=" + degat +
                ", position=(" + coord.x + ", " + coord.y + ")" +
                ", direction=(" + directionX + ", " + directionY + ")" +
                '}';
    }

    public int getDegat(){
        return degat;
    }
<<<<<<< HEAD
}
=======

    @Override
    public boolean is_alive() {
        return Math.abs(creation_time-System.nanoTime())<duree_de_vie;
    }
}
>>>>>>> origin/Gabriel
