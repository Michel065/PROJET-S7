import javafx.scene.paint.Color;
import java.time.Instant;
import java.time.Duration;

public class Player extends Rond {
    private double orientation=0;
    private float max_speed=(float)0.9,speed;

    //info projectile:
    float proj_speed = (float)0.4;
    int proj_life = 20;
    float proj_radius = (float)0.4;
    int proj_degat=15;
    int cooldown=500;//en ms
    Instant start=  Instant.now(),end;
    Duration duration;

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
    public void simu_move(float[] val){
        val[0]=x+(float)(Math.cos(orientation)*speed);
        val[1]=y+(float)(Math.sin(orientation)*speed);
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
        end = Instant.now();
        duration = Duration.between(start, end);
        if(duration.toMillis() >=cooldown){
            start=end;
            return new Projectile(coul, proj_speed,proj_life,proj_radius,proj_degat,x,y, (float) Math.cos(orientation), (float) Math.sin(orientation)); 
        }
        return null;
}

}
