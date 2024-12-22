
public class LightRond {
    private int coul;
    private float health; 
    private float x, y;
    private float radius;


    public LightRond(float x,float y,float radius, float health,int coul) {
        this.health = health;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.coul=coul;
        
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getHealth(){
        return health;
    }

    public float getRadius(){
        return radius;
    }

    public int getCouleur(){
        return coul;
    }
}
