import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

public class ListeAtomicCoord {
    private AtomicLongArray  atomicArray;
    private AtomicInteger size = new AtomicInteger(0);

    public ListeAtomicCoord(int size) {
        atomicArray = new AtomicLongArray(size);
    }

    public int get_size() {
        return size.get();
    }

    public boolean iniitialise(List<Projectile> liste) {
        if(atomicArray.length() < liste.size()) {
            System.out.println("trop de valeur");
            return false;
        }

        size.set(0);
        CoordFloat tmp;
        for(Projectile r : liste) {
            tmp = r.get_coord();
            ajoute(tmp.x, tmp.y);
        }
        return true;
    }

    public float getX(int index) {
        return Float.intBitsToFloat((int)(atomicArray.get(index) & 0xFFFFFFFFL));
    }

    public float getY(int index) {
        return Float.intBitsToFloat((int)(atomicArray.get(index) >>> 32));
    }

    public void get(int index, CoordFloat val) {
        val.set(atomicArray.get(index));
    }

    public void ajoute(float x, float y) {
        int index = size.getAndIncrement();
        atomicArray.set(index, ((long) Float.floatToIntBits(y) << 32) | (Float.floatToIntBits(x) & 0xFFFFFFFFL)); 
    }

    public void ajoute(CoordFloat tmp) {
        int index = size.getAndIncrement();
        atomicArray.set(index, ((long) Float.floatToIntBits(tmp.y) << 32) | (Float.floatToIntBits(tmp.x) & 0xFFFFFFFFL)); 
    }
}
