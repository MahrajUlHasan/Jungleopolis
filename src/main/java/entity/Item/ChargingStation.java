package entity.Item;

import entity.Building;
import entity.Entity;
import entity.util.PathNode;
import org.example.GameEngine;
import org.example.MapSpace;

public class ChargingStation extends Building implements PathNode {
    private static int nextId = 1;
    private final int id;

    public ChargingStation(GameEngine engine) {
        super(engine);
        this.id = nextId++;
        upPath1 = "/surveilance/ChargingStation/chargingStationUp1.png";
        upPath2 = "/surveilance/ChargingStation/chargingStationUp2.png";
        getSpriteImage();
    }

    @Override
    protected void updateStats() {
        // No stats to update for charging stations
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void interact(Entity entity) {
        super.interact(entity);

        // When a drone interacts with a charging station, recharge its battery
        if (entity instanceof Drone) {
            Drone drone = (Drone) entity;
            drone.rechargeBattery();
        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.chargingStations.add(this);
    }
}
