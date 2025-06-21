package entity.Item;

import entity.Building;
import entity.Entity;
import entity.util.PathNode;
import org.example.GameEngine;

public class FuelingStation extends Building implements PathNode {
    public FuelingStation(GameEngine engine) {
        super(engine);
    }

    @Override
    protected void updateStats() {

    }

    @Override
    public int getId() {
        return 0;
    }



    @Override
    public void interact(Entity entity) {
        super.interact(entity);

    }
}
