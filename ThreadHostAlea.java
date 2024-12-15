import java.util.Random;

import javafx.scene.paint.Color;


public class ThreadHostAlea extends ThreadHostSkull {
    private Random random = new Random();

    ThreadHostAlea(Carte carte,ListShare<Player> players,ListShare<Projectile> projectiles){
        super(carte, players, projectiles);
        coul=Color.LIGHTGREEN;

    }
    
    @Override
    protected void init(){}

    @Override
    protected void action(){
        float val = random.nextFloat();
        if(val<0.1){
            ourplayer.addToSpeed((float)0.4);
        }
        else if(val>0.97){
            ourplayer.addToSpeed((float)-0.4);
        }

        val = random.nextFloat();
        if(val<0.2){
            ourplayer.rotate(10);
        }
        val = random.nextFloat();
        if(val<0.1){
            tire();
        }
    }
}
