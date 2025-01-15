public class LightRond {
    private int coul;
    private float health_en_pourcentage; 
    private float x, y;
    private float radius;

    public LightRond(float x, float y, float radius, float health_en_pourcentage, int coul) {
        this.health_en_pourcentage=health_en_pourcentage/(float)100;
        this.x = x - radius;
        this.y = y - radius;
        this.radius = radius;
        this.coul=coul;
    }

    public float getX() {
        return x + radius;
    }

    public float getY() {
        return y + radius;
    }

    public float getRadius() {
        return radius;
    }

    public int getCouleur() {
        return coul;
    }
    
    public float get_vie_pourcentage() {
        return health_en_pourcentage;
    }
}