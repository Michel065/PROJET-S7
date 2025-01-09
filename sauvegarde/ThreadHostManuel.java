import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ThreadHostManuel extends ThreadHostSkull {
    private Stage primaryStage;
    private final Set<KeyCode> activeKeys = new HashSet<>();

    ThreadHostManuel(Stage primaryStage, Carte carte, ListShare<Player> players, ListShare<Projectile> projectiles) {
        super(carte, players, projectiles);
        this.primaryStage = primaryStage;
        coul=Color.GREEN;
    }
    
    @Override
    protected void init() {
        if (primaryStage.getScene() != null) {
            primaryStage.getScene().setOnKeyPressed(event -> activeKeys.add(event.getCode()));
            primaryStage.getScene().setOnKeyReleased(event -> activeKeys.remove(event.getCode()));
        } else {
            System.err.println("Erreur : La scène n'est pas définie pour le stage.");
        }
    }

    @Override
    protected void action() {
        if (activeKeys.contains(KeyCode.Z)) {
            ourplayer.addToSpeed((float) 0.2);
        }
        if (activeKeys.contains(KeyCode.Q)) {
            ourplayer.rotate(-10);
        }
        if (activeKeys.contains(KeyCode.S)) {
            ourplayer.addToSpeed((float) -0.2);
        }
        if (activeKeys.contains(KeyCode.D)) {
            ourplayer.rotate(10);
        }
        if (activeKeys.contains(KeyCode.SPACE)) {
            tire();
        }
    }
    
    @Override
    protected void finish(){}
}
