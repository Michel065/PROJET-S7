public class Player extends Rond {
    private double orientation = 0;
    private float max_speed=(float)5, speed_x, speed_y;
    private int health = 100, max_health = 100;

    // Infos projectile :
    private float proj_speed = (float)2.5;
    private float proj_life = (float)3.5; // En secondes
    private float proj_radius = (float)0.2;
    private int proj_degat = 15;
    
    private long cooldown = 500 * 1000 * 1000; // En ns
    private long start = System.nanoTime(), end;

    private CoordFloat X= new CoordFloat();// ppoint de contact avec un obstacle

    public Player(int health, float x, float y) {
        super((float)0.45, x, y);
        name = "Player";
        this.health = health;
        invincibilite = true;
    }

    public int getHealth() {
        return health;
    }

    public CoordFloat get_Point_Contact(){
        return X;
    }

    public int get_pourcentage_vie() {
        return (int)Math.ceil(100 * ((float)health / (float)max_health));
    }

    public void addHealth(int val) {
        if(!invincibilite)health -= val;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public boolean is_alive() {
        return health > 0;
    }

    public void rotate(int angle) {
        orientation+=(angle * 0.01745329253);
    }

    public void addToSpeed(float gain_par_seconde) {
        speed_x += Math.cos(orientation) * gain_par_seconde*delta_time;
        speed_y += Math.sin(orientation) * gain_par_seconde*delta_time;

        if(speed_x > 0)
            speed_x = Math.min(speed_x, max_speed);
        else if(speed_x < 0)
            speed_x = Math.max(speed_x, -max_speed);
        if(speed_y > 0)
            speed_y = Math.min(speed_y, max_speed);
        else if(speed_y < 0)
            speed_y = Math.max(speed_y, -max_speed);        
    }

    private void reduce_speed() {
        if(Math.abs(speed_x) > 0.1)
            speed_x *= 1 - (delta_time);
        else speed_x = 0;
        if(Math.abs(speed_y) > 0.1)
            speed_y *= 1 - (delta_time);
        else speed_y = 0;
    }

    public void reset_speed() {
        speed_x = 0;
        speed_y = 0;
        if(Math.abs(Math.cos(orientation)) > Math.abs(Math.sin(orientation)))
            speed_x = 0;
        else
            speed_y = 0;
        
    }

    @Override
    public void simu_move(){
        coord_simu.set(coord.x + (float)(Math.abs(Math.cos(orientation)) * speed_x * delta_time), coord.y + (float)(Math.abs(Math.sin(orientation)) * speed_y * delta_time));
    }

    @Override
    public void move(){
        //System.out.println("en x:"+speed_x+" en y:"+speed_y);
        coord.x += (float)(Math.abs(Math.cos(orientation)) * speed_x * delta_time);
        coord.y += (float)(Math.abs(Math.sin(orientation)) * speed_y * delta_time);
        reduce_speed();
    }

    public void rectifie_move(){
        if(coord_simu.x==X.x){
            coord.x=X.x;            
            float sin=(float)Math.sin(orientation);
            coord.y=X.y+(radius+(float)0.01)*sin/Math.abs(sin);
        }
        else{
            coord.y=X.y;            
            float cos=(float)Math.cos(orientation);
            coord.x=X.x+(radius+(float)0.01)*cos/Math.abs(cos);
        }
        reduce_speed();
    }

    public float getOrientation() {
        return (float)orientation;
    }

    public void setOrientation(float val) {
        orientation = val;
    }

    public Projectile tire(){
        end = System.nanoTime();
        if(end - start >= cooldown){
            start = end;
            return new Projectile(proj_speed, proj_life, proj_radius, proj_degat, coord.x + radius/2 - proj_radius, coord.y + radius/2 - proj_radius, (float)Math.cos(orientation), (float)Math.sin(orientation));
            //return new Projectile(proj_speed, proj_life, proj_radius, proj_degat, coord.x, coord.y, (float)Math.cos(orientation), (float)Math.sin(orientation)); 
        }
        return null;
    }

    public boolean setEquipe(int val) {
        equipe = val;
        return true;
    }

    public void setInvinvibilite(boolean val) {
        invincibilite = val;
    }

    public float get_proj_radius() {
        return proj_radius;
    }
}