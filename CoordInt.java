public class CoordInt {
    public int x;
    public int y;
    public CoordInt(int val1,int val2){
        x=val1;
        y=val2;
    }

    public CoordInt(CoordInt tmp){
        x=tmp.x;
        y=tmp.y;
    }
}
