import java.util.concurrent.atomic.AtomicLong;

public class CoordFloatAtomic {
    private AtomicLong coord = new AtomicLong(0);

    public CoordFloatAtomic() {}

    public CoordFloatAtomic(Float x, Float y) {
        setCoords(x, y);
    }

    public CoordFloatAtomic(CoordFloat tmp) {
        setCoords(tmp.x, tmp.y);
    }

    public void setCoords(Float x, Float y) {
        long encoded = ((long) Float.floatToIntBits(y) << 32) | (Float.floatToIntBits(x) & 0xFFFFFFFFL);
        coord.set(encoded);
    }

    public void setCoords(CoordFloat tmp) {
        setCoords(tmp.x, tmp.y);
    }

    public void getCoords(Float[] coordArray) {
        long encoded = coord.get();
        coordArray[0] = Float.intBitsToFloat((int) (encoded & 0xFFFFFFFFL)); // x
        coordArray[1] = Float.intBitsToFloat((int) (encoded >>> 32));       // y
    }

    public Long getCoordsLong() {
        return coord.get();
    }
}
