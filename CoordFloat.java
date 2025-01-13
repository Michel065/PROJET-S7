public class CoordFloat {
    public Float x;
    public Float y;

    public CoordFloat(){
        this.x=(float)0;
        this.y=(float)0;
    }


    public CoordFloat(Float val1,Float val2){
        x=val1;
        y=val2;
    }

    public CoordFloat(CoordFloat tmp){
        x=tmp.x;
        y=tmp.y;
    }

    public void set(Float val1,Float val2){
        x=val1;
        y=val2;
    }

    public void set(CoordFloat c){
        x=c.x;
        y=c.y;
    }
}
