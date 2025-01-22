public class CoordFloat {
    public float x;
    public float y;

    public CoordFloat() {
        this.x = (float)0;
        this.y = (float)0;
    }

    public CoordFloat(Float val1, Float val2) {
        x = val1;
        y = val2;
    }

    public CoordFloat(CoordFloat tmp) {
        x = tmp.x;
        y = tmp.y;
    }

    public CoordFloat(CoordFloatAtomic tmp) {
        Long tmpp = tmp.getCoordsLong();
        x = Float.intBitsToFloat((int) (tmpp & 0xFFFFFFFFL)); // x
        y = Float.intBitsToFloat((int) (tmpp >>> 32)); // y
    }

    public void set(Float val1,Float val2) {
        x = val1;
        y = val2;
    }

    public void set(CoordFloat c) {
        x = c.x;
        y = c.y;
    }

    public void set(CoordFloatAtomic tmp) {
        set(tmp.getCoordsLong());
    }

    public void set(long tmp) {
        x = Float.intBitsToFloat((int) (tmp & 0xFFFFFFFFL)); // x
        y = Float.intBitsToFloat((int) (tmp >>> 32));   // y
    }

    public void add(Float val1) {
        x += val1;
        y += val1;
    }
}
