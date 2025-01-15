public class LightRond {
    private int coul;
    private float health_en_pourcentage; 
    private CoordFloat coord;
    private float radius;

    public LightRond(float x, float y, float radius, float health_en_pourcentage, int coul) {
        this.health_en_pourcentage=health_en_pourcentage/(float)100;
        coord=new CoordFloat(x,y);
        this.radius = radius;
        this.coul=coul;
    }

    public float getX() {
        return coord.x;
    }

    public float getY() {
        return coord.y;
    }

    public CoordFloat getCoord() {
        return coord;
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