package entity.Item;

import entity.Building;
import entity.Entity;
import entity.util.PathNode;
import org.example.GameEngine;
import org.example.MapSpace;

public class AirShipPatrolFlag extends Building implements PathNode {
    private static int nextId = 1;
    private final int id;

    public AirShipPatrolFlag(GameEngine engine) {
        super(engine);
        this.id = nextId++;
        upPath1 = "/Surveilance/AirShipPatrolFlag/flag1.png";
        upPath2 = "/Surveilance/AirShipPatrolFlag/flag2.png";
        getSpriteImage();
        COLLIDABLE = false;
    }

    @Override
    protected void updateStats() {
        // No stats to update for patrol flags
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void interact(Entity entity) {
        super.interact(entity);
        // When an airship interacts with a patrol flag, it should get its next target
        if (entity instanceof AirShip) {
            AirShip airship = (AirShip) entity;
            // Only set a new target if we've actually reached this flag
            // This prevents multiple target changes when just passing near a flag
            if (airship.collides(this)) {
                airship.setTarget();
            }
        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.airShipPatrolFlags.add(this);
        // Add this patrol flag to the path handler
        if (mapSpace.airshipPathHandler != null) {
            mapSpace.airshipPathHandler.addNode(this);
        }
    }
}
