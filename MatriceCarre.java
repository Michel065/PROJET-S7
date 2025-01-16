public class MatriceCarre<A> {
    private A[][] mat;

    @SuppressWarnings("unchecked")
    public MatriceCarre(int largeur) {
        mat = (A[][]) new Object[largeur][largeur]; 
    }

    public A get(int x, int y) {
        return mat[x][y];
    }

    public A get(CoordInt val) {
        return mat[val.x][val.y];
    }
    
    public void set(int x, int y, A val) {
        mat[x][y]=val;
    }
}
