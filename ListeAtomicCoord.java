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

    public boolean iniitialise(List<Rond> liste){
        if(atomicArray.length()<liste.size()){
            System.out.println("trop de valeur");
            return false;
        }
        size.set(0);
        for(Rond r: liste){
            ajoute(r.getX(),r.getY());
        }
        return true;
    }

    public float getX(int index){
        return Float.intBitsToFloat( (int)(atomicArray.get(index) & 0xFFFFFFFFL));
    }

    public float getY(int index){
        return Float.intBitsToFloat((int) (atomicArray.get(index) >>> 32));
    }

    public void ajoute(float x, float y) {
        int index=size.getAndIncrement();
        atomicArray.set(index, ((long) Float.floatToIntBits(y) << 32) | (Float.floatToIntBits(x) & 0xFFFFFFFFL)); 
    }
}
