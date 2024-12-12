import javafx.scene.paint.Color;

public class Player {
    private Color coul;
    private int health; 
    private float x, y,speed;
    private double orientation=0;
    private float max_speed=(float)0.9;
    private float radius=(float)0.6;

    public Player(Color coul, int health, float x, float y) {
        this.coul = coul;
        this.health = health;
        this.x = x;
        this.y = y;
        
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
        return "Player{id='" + coul + "', health=" + health + ", position=(" + x + ", " + y + ")}";
    }



    public void rotate(int angle){
        orientation+=(angle*0.01745329253);
    }

    public void addToSpeed(float val){
        speed+=val;
        if(speed>0)
            Math.min(speed,max_speed);
        else if(speed<0)
            Math.max(speed,-max_speed);

    }

    public void simu_move(float[] val){
        val[0]=x+(float)(Math.cos(orientation)*speed);
        val[1]=y+(float)(Math.sin(orientation)*speed);
    }

    private void reduce_speed(){
        if(Math.abs(speed)>0.1)
        speed*=0.9;
        else speed=0;
    }

    public void reset_speed(){
        speed=0;
    }

    public void move(){
        x+=(float)(Math.cos(orientation)*speed);
        y+=(float)(Math.sin(orientation)*speed);
        reduce_speed();
    }

    public double getOrientation() {
        return orientation;
    }

    public Color getCouleur() {
        return coul;
    }

    public float getRadius() {
        return radius;
    }

    public boolean is_touch_by(Projectile pro){
        float xx=Math.abs(pro.getX()-x);
        float yy=Math.abs(pro.getY()-y);
        return xx*xx+yy*yy<((radius+pro.getRadius())*(radius+pro.getRadius()));
    }

    

    

}
