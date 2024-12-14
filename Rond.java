import javafx.scene.paint.Color;

public abstract class Rond {
    protected Color coul;
    protected int health; 
    protected float x, y,speed;
    protected float radius;
    protected String name="Rond";
    protected float [] coord_simu=new float[2];


    public Rond(Color coul, int health,float radius, float x, float y) {
        this.coul = coul;
        this.health = health;
        this.x = x;
        this.y = y;
        this.radius = radius;
        
    }

    public int getHealth() {
        return health;
    }

    public void addHealth(int val) {
        health-=val;
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
        return name+"{couleur='" + coul + "', health=" + health + ", position=(" + x + ", " + y + ")}";
    }

    protected abstract void simu_move();

    protected abstract void move();

    public float[] get_simu_move(){
        return coord_simu;
    }


    public Color getCouleur() {
        return coul;
    }

    public float getRadius() {
        return radius;
    }

    public boolean is_touch_by(Rond rond){
        float xx=Math.abs(rond.getX()-x);
        float yy=Math.abs(rond.getY()-y);
        return xx*xx+yy*yy<((radius+rond.getRadius())*(radius+rond.getRadius()));
    }

    public boolean is_touch_in_simu(Rond rond){
        float xx=Math.abs(rond.getX()-coord_simu[0]);
        float yy=Math.abs(rond.getY()-coord_simu[1]);
        return xx*xx+yy*yy<((radius+rond.getRadius())*(radius+rond.getRadius()));
    }

    public boolean is_alive(){
        return health>0;
    }
    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

}
