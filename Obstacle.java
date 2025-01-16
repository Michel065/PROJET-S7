public class Obstacle {
    private CoordInt coord;

    public Obstacle(int x, int y) {
        coord = new CoordInt(x,y);
    }

    public int getx() {
        return coord.x;
    }

    public int gety() {
        return coord.y;
    }

    public void setx(int xx) {
        coord.x = xx;
    }

    public void sety(int yy) {
        coord.y = yy;
    }

    public CoordInt get() {
        return coord;
    }  

    public boolean is_egual(int x, int y) {
        return x == coord.x && y == coord.y;
    }

    public String stringifi() {
        return coord.x + ":" + coord.y;
    }
}
