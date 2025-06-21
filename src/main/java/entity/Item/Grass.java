package entity.Item;

import entity.Building;
import entity.Entity;
import entity.Herbivore;
import org.example.GameEngine;
import org.example.MapSpace;

public class Grass extends Building {

    public Grass(GameEngine engine) {
        super(engine);
        upPath1 = "/Greenery/grass.png";
        upPath2 = "/Greenery/grass.png";
        getSpriteImage();
        COLLIDABLE = false;
//        setSprite(0,-10 ,engine.tileSize,engine.tileSize+10);
    }

    @Override
    protected void updateStats() {

    }


    @Override
    public void interact(Entity ent)
    {
        super.interact(ent);
        if(ent instanceof Herbivore)
        {
            Herbivore herb = (Herbivore)ent;
            herb.eatGrass(this);

        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.grasses.add(this);

    }
}
