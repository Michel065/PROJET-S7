public class Player {
    private int id;
    private int health; 
    private float x, y,speed;
    private double orientation=0;

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



    public void rotate(int angle){
        orientation+=(angle*0.01745329253);
    }

    public void addToSpeed(float val){
        speed+=val;
        if(speed>0)
            Math.min(speed,1);
        else if(speed<0)
            Math.max(speed,-1);

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
}
