import java.util.concurrent.atomic.AtomicInteger;

public class ListePartageThread {
    private ThreadHostToClient[] liste;
    private AtomicInteger size = new AtomicInteger(0);
    private int max_size;
    private int nbr_equipe=4;
    private int[] personne_par_equipe = new int[nbr_equipe];

    ListePartageThread(int n,int nbr_equipe) { 
        liste = new ThreadHostToClient[n];
        max_size=n;
        this.nbr_equipe=nbr_equipe;
        init_equipe();
    }

    public synchronized boolean ajouter(ThreadHostToClient client) { 
        if(size.get() >= max_size) return false;
        int index = size.getAndIncrement();
        client.setIndex(index);
        liste[index] = client;
        return true;
    }

    public synchronized void supprimer(int index) {
        if(size.get() == 0) return;

        int indexFin = size.decrementAndGet();
        if (index != indexFin) {
            liste[index] = liste[indexFin];
            liste[index].setIndex(index);
            liste[indexFin] = null;
        } else {
            liste[index] = null;
        }
        print_equipe();

    }
    
    public synchronized ThreadHostToClient recuperer(int index) {
        if (index >= size.get()) return null;
        return liste[index];
    }

    public int get_size() {
        return size.get();
    }

    public int get_max_size() {
        return max_size;
    }

    public boolean vide() {
        return size.get()==0;
    }

    private void init_equipe(){
        for(int i =0;i<nbr_equipe;i++){
            personne_par_equipe[i]=0;
        }
    }

    private synchronized void maj_liste_equipe(){
        for(int i=0;i<get_size();i++){
            int equipe=liste[i].getEquipe();
            if(equipe!=-1){
                personne_par_equipe[equipe]+=1;
            }
        }
    }

    private synchronized void print_equipe(){
        for(int i=0;i<get_size();i++){
            System.out.println(liste[i].getEquipe()); 
            
        }
    }

    public int recup_meuilleur_equipe() {
        init_equipe();
        maj_liste_equipe();

        int id_min=0,score=personne_par_equipe[0];
        for(int i =1;i<nbr_equipe;i++){
            if(personne_par_equipe[i]<score){
                id_min=i;
                score=personne_par_equipe[i];
            }
        }

        return id_min;
    }

}
