import javafx.scene.paint.Color;

public class LightRond {
    private Color coul=Color.GREEN;
    private float health; 
    private float x, y;
    private float radius;


    public LightRond(float x,float y,float radius, float health) {
        this.health = health;
        this.x = x;
        this.y = y;
        this.radius = radius;
        
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

    public Color getCouleur(){
        return coul;
    }
}
