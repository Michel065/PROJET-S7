import javafx.scene.paint.Color;

public class Player extends Rond {
    private double orientation=0;
    private float max_speed=(float)0.9,speed;

    //info projectile:
    private float proj_speed = (float)0.4;
    private int proj_life = 20;
    private float proj_radius = (float)0.2;
    private int proj_degat=15;

    
    private long cooldown=500*1000*1000;//en ns
    private long start = System.nanoTime(), end;


    public Player(Color coul, int health, float x, float y) {
        super(coul, health, (float)0.5, x, y);
        name="Player";
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

    private void reduce_speed(){
        if(Math.abs(speed)>0.1)
        speed*=0.9;
        else speed=0;
    }

    public void reset_speed(){
        speed=0;
    }

    @Override
    public void simu_move(){
        coord_simu[0]=x+(float)(Math.cos(orientation)*speed);
        coord_simu[1]=y+(float)(Math.sin(orientation)*speed);
    }

    @Override
    public void move(){
        x+=(float)(Math.cos(orientation)*speed);
        y+=(float)(Math.sin(orientation)*speed);
        reduce_speed();
    }

    public double getOrientation() {
        return orientation;
    }

    public Projectile tire(){
        end = System.nanoTime();
        if(end-start >=cooldown){
            start=end;
            return new Projectile(coul, proj_speed,proj_life,proj_radius,proj_degat,x,y, (float) Math.cos(orientation), (float) Math.sin(orientation)); 
        }
        return null;
}

}
