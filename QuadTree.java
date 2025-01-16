import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/*
public class QuadTree {
    private static final int CAPACITE_MAX = 4;
    private static final int PROFONDEUR_MAX = 10;

    private final Zone limites;
    private final int profondeur;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private List<ThreadHostToClient> threadsJoueurs;
    private QuadTree[] sousArbres;

    public QuadTree(Zone limites, int profondeur) {
        this.limites = limites;
        this.profondeur = profondeur;
        this.threadsJoueurs = new ArrayList<>();
        this.sousArbres = null;
    }

    public void inserer(ThreadHostToClient thread) {
        lock.writeLock().lock();
        try {
            if (!limites.contient(thread.getCoordJoueur().getCoordonnees())) {
                return;
            }

            if (sousArbres == null) {
                threadsJoueurs.add(thread);
                if (threadsJoueurs.size() > CAPACITE_MAX && profondeur < PROFONDEUR_MAX) {
                    subdiviser();
                    redistribuerThreads();
                }
            } else {
                int index = obtenirIndex(thread.getCoordJoueur().getCoordonnees());
                if (index != -1) {
                    sousArbres[index].inserer(thread);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<ThreadHostToClient> recuperer(CoordFloat coord, float rayon) {
        lock.readLock().lock();
        try {
            List<ThreadHostToClient> potentiels = new ArrayList<>();
            if (!limites.intersecteCercle(coord, rayon)) {
                return potentiels;
            }

            potentiels.addAll(threadsJoueurs);

            if (sousArbres != null) {
                for (QuadTree sousArbre : sousArbres) {
                    potentiels.addAll(sousArbre.recuperer(coord, rayon));
                }
            }

            return potentiels;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void subdiviser() {
        float largeur = limites.getLargeur() / 2;
        float hauteur = limites.getHauteur() / 2;
        float x = limites.getX();
        float y = limites.getY();

        sousArbres = new QuadTree[4];
        sousArbres[0] = new QuadTree(new Zone(x, y, largeur, hauteur), profondeur + 1);
        sousArbres[1] = new QuadTree(new Zone(x + largeur, y, largeur, hauteur), profondeur + 1);
        sousArbres[2] = new QuadTree(new Zone(x, y + hauteur, largeur, hauteur), profondeur + 1);
        sousArbres[3] = new QuadTree(new Zone(x + largeur, y + hauteur, largeur, hauteur), profondeur + 1);
    }

    private void redistribuerThreads() {
        for (ThreadHostToClient thread : threadsJoueurs) {
            int index = obtenirIndex((thread.getCoordJoueur()).getCoord());
            if (index != -1) {
                sousArbres[index].inserer(thread);
            }
        }
        threadsJoueurs.clear();
    }

    private int obtenirIndex(CoordFloat coord) {
        float milieuX = limites.getX() + limites.getLargeur() / 2;
        float milieuY = limites.getY() + limites.getHauteur() / 2;

        boolean haut = coord.y < milieuY;
        boolean bas = !haut;
        boolean gauche = coord.x < milieuX;
        boolean droite = !gauche;

        if (haut && gauche) return 0;
        if (haut && droite) return 1;
        if (bas && gauche) return 2;
        if (bas && droite) return 3;

        return -1;
    }
}

/* 
public class ExempleUtilisation {
    public static void main(String[] args) {
        Zone zoneCarte = new Zone(0, 0, 100, 100);
        QuadTree arbre = new QuadTree(zoneCarte, 0);

        // Création de threads joueurs avec des positions
        ThreadHostToClient joueur1 = new ThreadHostToClient(new CoordFloat(10, 10));
        ThreadHostToClient joueur2 = new ThreadHostToClient(new CoordFloat(50, 50));
        ThreadHostToClient joueur3 = new ThreadHostToClient(new CoordFloat(70, 20));

        // Insertion des threads dans l'arbre
        arbre.inserer(joueur1);
        arbre.inserer(joueur2);
        arbre.inserer(joueur3);

        // Recherche de joueurs proches d'un point
        CoordFloat positionRecherche = new CoordFloat(60, 50);
        float rayon = 15;

        List<ThreadHostToClient> proches = arbre.recuperer(positionRecherche, rayon);

        // Affichage des résultats
        System.out.println("Joueurs proches : " + proches.size());
    }
}
*/