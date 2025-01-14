import java.util.concurrent.atomic.AtomicInteger;


public class ListePartageThread {
    private ThreadHostToClient[] liste;
    private AtomicInteger size = new AtomicInteger(0);
    private int max_size;

    ListePartageThread(int n) { 
        liste = new ThreadHostToClient[n];
        max_size=n;
    }

    public synchronized boolean ajouter(ThreadHostToClient client) { 
        if(size.get() >= max_size) return false;
        int index = size.getAndIncrement();
        client.setIndex(index);
        liste[index] = client;
        return true;
    }
    

    public synchronized void supprimer(int index) {
        int indexFin = size.decrementAndGet();
        if (index != indexFin) {
            liste[index] = liste[indexFin];
            liste[index].setIndex(index);
            liste[indexFin] = null;
        } else {
            liste[index] = null;
        }
        System.out.println("nbr:"+size.get());
    }
    

    public synchronized ThreadHostToClient recuperer(int index) {
        if (index >= size.get()) return null;
        return liste[index];
    }

    public int get_size(){
        return size.get();
    }

    public int get_max_size(){
        return max_size;
    }

    public boolean vide(){
        return size.get()==0;
    }
    

}
