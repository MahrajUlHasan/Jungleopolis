package entity.Item;

import entity.DynamicEntity;
import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;

public class Person extends DynamicEntity {

    public Person(GameEngine game , MapSpace m) {
        super(game, m);
    }

    @Override
    protected void updateStats() {

    }

    @Override
    public void setTarget() {

    }



    @Override
    public void interact(Entity entity) {

    }

    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entitiesToAdd.add(this);
        mapSpace.people.add(this);
    }

    protected void die() {
        alive = false;
        map.people.remove(this);
        map.entitiesToRemove.add(this);
        map.dynamicEntities.remove(this);
    }

}
