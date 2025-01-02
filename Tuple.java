public class Tuple {
    public int x;
    public int y;
    public Tuple(int val1,int val2){
        x=val1;
        y=val2;
    }

    public Tuple(Tuple tmp){
        x=tmp.x;
        y=tmp.y;
    }
}
