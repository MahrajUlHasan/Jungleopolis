package entity.Item;

import entity.Animal;
import entity.Building;
import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;

public class Pond extends Building {
    public Pond(GameEngine engine) {
        super(engine);
        upPath1 = "/Water/water.png";
        upPath2 = "/Water/water.png";
        getSpriteImage();

    }

    @Override
    protected void updateStats() {
        super.updateStats();

    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.ponds.add(this);
    }

    @Override
    public void interact(Entity entity) {
        super.interact(entity);
        if(entity instanceof Animal) {
            Animal animal = (Animal)entity;
            animal.drinkWater();
        }
    }



}
