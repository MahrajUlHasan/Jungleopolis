package entity;

import entity.Item.Grass;
import org.example.GameEngine;
import org.example.MapSpace;

/**
 * Will be an Abstract class so don't initialize instances of this class except for testing
 */
public abstract class Herbivore extends Animal {
    public Herbivore(GameEngine engine, MapSpace m) {
        super(engine, m);
    }


    @Override
    public void setTarget() {
        super.setTarget();
        switch (status)
        {
            case HUNT :
                target = map.getClosestEntity("Grass", this);
                break;
            default:
                break;
        }
    }

    protected void reproduce(Herbivore mate) {
        // Check if we've reached the maximum number of herbivores
        if (map.herbivores.size() >= 60) {
            // Maximum reached, don't spawn a new herbivore
            return;
        }

        // Call the specific animal's reproduction method
        doReproduce(mate);
    }

    // Each specific herbivore type will implement this method
    protected abstract void doReproduce(Herbivore mate);


    public abstract void  attacked(Carnivore carnivore) ;

    public void feed(Grass grass) {
    }

    public void eatGrass(Grass grass) {
        hunger+= 10;
        if (hunger >= hungerCap) {
            hunger = hungerCap;
            status = Status.SLEEP;
        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.herbivores.add(this);
    }

    @Override
    public void die() {
        super.die();
        map.herbivores.remove(this);

    }

    @Override
    protected void setStats() {
    }

    @Override
    public void interact(Entity entity) {
        super.interact(entity);
         if(entity instanceof Herbivore && status == Status.REPRODUCE)
        {
            Herbivore h = (Herbivore) entity;
            h.reproduce(this);
            status = Status.THIRST;
        }
    }
}
