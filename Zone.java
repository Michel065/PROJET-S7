public class Zone {
    private final float x;
    private final float y;
    private final float largeur;
    private final float hauteur;

    public Zone(float x, float y, float largeur, float hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public boolean contient(CoordFloat coord) {
        return coord.x >= x && coord.x <= (x + largeur) &&
               coord.y >= y && coord.y <= (y + hauteur);
    }

    public boolean intersecteCercle(CoordFloat centre, float rayon) {
        float deltaX = Math.max(x - centre.x, Math.max(0, centre.x - (x + largeur)));
        float deltaY = Math.max(y - centre.y, Math.max(0, centre.y - (y + hauteur)));
        return (deltaX * deltaX + deltaY * deltaY) <= (rayon * rayon);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLargeur() {
        return largeur;
    }

    public float getHauteur() {
        return hauteur;
    }
}
