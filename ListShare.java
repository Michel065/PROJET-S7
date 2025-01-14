import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListShare<T> implements Iterable<T> {
    private List<T> liste;

    public ListShare(List<T> liste) {
        this.liste = liste;
    }

    public ListShare() {
        this.liste = new ArrayList<>();
    }

    public void add(T obj) {
        synchronized(liste){
            liste.add(obj);
        }
    }

    public T get(int i) {
        synchronized(liste) {
            return liste.get(i);
        }
    }

    public void remove(T obj) {
        synchronized(liste) {
            liste.remove(obj);
        }
    }

    public void remove(int index) {
        synchronized(liste) { 
            liste.remove(index);
        }
    }

    public void remove(List<T> lis) {
        synchronized(liste) {
            liste.removeAll(lis);
        }
    }

    public int size() {
        synchronized(liste) {
            return liste.size();
        }
    }

    public void clear() {
        synchronized(liste){
            liste.clear();
        }
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return liste.iterator();
    }
}