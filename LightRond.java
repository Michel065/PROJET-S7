public class LightRond {
    private int equipe;
    private float health_en_pourcentage; 
    private CoordFloat coord;
    private float radius;

    public LightRond(float x, float y, float radius, int health_en_pourcentage, int equipe) {
        this.health_en_pourcentage = health_en_pourcentage / (float)100;
        coord = new CoordFloat(x, y);
        this.radius = radius;
        this.equipe=equipe;
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

    public int getEquipe() {
        return equipe;
    }
    
    public float get_vie_pourcentage() {
        return health_en_pourcentage;
    }
}