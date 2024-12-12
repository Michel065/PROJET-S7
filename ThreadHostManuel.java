import javafx.stage.Stage;

public class ThreadHostManuel extends ThreadHostSkull {
    private Stage primaryStage;

    ThreadHostManuel(Stage primaryStage,Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        super(carte, players, projectiles);
        this.primaryStage=primaryStage;
    }
    
    @Override
    protected void action(){
        primaryStage.getScene().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case Z:
                    ourplayer.addToSpeed((float)0.4);
                    break;
                case Q:
                ourplayer.rotate(-10);

                    break;
                case S:
                    ourplayer.addToSpeed((float)-0.4);
                    break;
                case D:
                    ourplayer.rotate(10);
                    break;
                case SPACE:
                    tire();
                    break;
                default:
                    System.out.println("Autre touche pressée : " + event.getCode());
            }
        });
    }
}
