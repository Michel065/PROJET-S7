public abstract class Rond {
    protected int equipe=-1;
    protected int health; 
    protected CoordFloat coord;
    protected float speed;
    protected float radius;
    protected String name="Rond";
    protected CoordFloat coord_simu=new CoordFloat();
    protected boolean invincibilite=false;


    public Rond(int health,float radius, float x, float y) {
        this.health = health;
        this.coord=new CoordFloat(x,y);
        this.radius = radius;
        
    }

    public int getHealth() {
        return health;
    }

    public void addHealth(int val) {
        if(!invincibilite)health-=val;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public CoordFloat get_coord() {
        return coord;
    }

    public String getCoordString() {
        return coord.x+":"+coord.y+":"+equipe;
    }

    public void setPosition(int x, int y) {
        coord.set((float)x,(float)y);
    }

    public void setPosition(float var1, float var2) {
        coord.set(var1,var2);
     }

    @Override
    public String toString() {
        return name+"{couleur='" + equipe + "', health=" + health + ", position=(" + coord.x + ", " + coord.y + ")}";
    }

    protected abstract void simu_move();

    protected abstract void move();

    public CoordFloat get_simu_move(){
        return coord_simu;
    }


    public int getEquipe() {
        return equipe;
    }

    public float getRadius() {
        return radius;
    }

    public boolean is_touch_by(CoordFloat coord_mechant,float radius_mechant){

        float xx=Math.abs(coord_mechant.x-coord.x);
        float yy=Math.abs(coord_mechant.y-coord.y);
        return xx*xx+yy*yy<((radius+radius_mechant)*(radius+radius_mechant));
    }

    public boolean is_touch_in_simu(CoordFloat coord_mechant,float radius_mechant){
        float xx=Math.abs(coord_mechant.x-coord_simu.x);
        float yy=Math.abs(coord_mechant.y-coord_simu.y);
        return xx*xx+yy*yy<((radius+radius_mechant)*(radius+radius_mechant));
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

    public boolean in_fentre(CoordFloat centre,int rayon) {
        return Math.abs(centre.x-coord.x)<rayon && Math.abs(centre.y-coord.y)<rayon;
    }

}
