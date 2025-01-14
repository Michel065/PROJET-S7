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


    public Player(int health, float x, float y) {
        super(health, (float)0.5, x, y);
        name="Player";
        invincibilite=true;
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
        coord_simu.set(coord.x+(float)(Math.cos(orientation)*speed),coord.y+(float)(Math.sin(orientation)*speed));
    }

    @Override
    public void move(){
        coord.x+=(float)(Math.cos(orientation)*speed);
        coord.y+=(float)(Math.sin(orientation)*speed);
        reduce_speed();
    }

    public float getOrientation() {
        return (float)orientation;
    }

    public void setOrientation(float val) {
        orientation=val;
    }

    public Projectile tire(){
        end = System.nanoTime();
        if(end-start >=cooldown){
            start=end;
            return new Projectile(proj_speed,proj_life,proj_radius,proj_degat,coord.x+radius-proj_radius,coord.y+radius-proj_radius , (float) Math.cos(orientation), (float) Math.sin(orientation)); 
        }
        return null;
    }

    public boolean setEquipe(int val) {
        equipe=val;
        return true;
    }

    public void setInvinvibilite(boolean val){
        invincibilite=val;
    }

    public float get_proj_radius(){
        return proj_radius;
    }
}