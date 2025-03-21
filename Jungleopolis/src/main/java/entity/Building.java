package entity;

import org.example.GameEngine;
import org.example.MapSpace;

public class Building extends StaticEntity {

    public Building(GameEngine engine) {
        super(engine);

    }

    public void spawn(MapSpace mapSpace) {
        mapSpace.entities.add(this);
        mapSpace.buildings.add(this);
    }
}
