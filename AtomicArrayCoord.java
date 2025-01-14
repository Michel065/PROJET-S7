import java.util.concurrent.atomic.AtomicLongArray;
// AAAAAAAAABB
public class AtomicArrayCoord {
    private AtomicLongArray  atomicArray;

    // Constructeur avec une taille fixe
    public AtomicArrayCoord(int size) {
        atomicArray = new AtomicLongArray(size);
    }

    // Méthode pour définir une valeur à un index
    public void set(int index, int value) {
        atomicArray.set(index, value);
    }

    // Méthode pour obtenir une valeur à un index
    public long get(int index) {
        return atomicArray.get(index);
    }

    // Méthode pour incrémenter une valeur atomiquement
    public void increment(int index) {
        atomicArray.incrementAndGet(index); // Incrémente et retourne la nouvelle valeur
    }

    // Méthode pour décrémenter une valeur atomiquement
    public void decrement(int index) {
        atomicArray.decrementAndGet(index); // Décrémente et retourne la nouvelle valeur
    }

    // Méthode pour obtenir la taille de l'array
    public int size() {
        return atomicArray.length();
    }

    public static void main(String[] args) {
        AtomicArrayCoord array = new AtomicArrayCoord(10);

        // Initialisation des valeurs
        array.set(0, 5);
        array.set(1, 10);

        // Lecture et écriture concurrentes
        System.out.println("Valeur à l'index 0 : " + array.get(0));
        array.increment(0);
        System.out.println("Valeur incrémentée à l'index 0 : " + array.get(0));
    }
}
