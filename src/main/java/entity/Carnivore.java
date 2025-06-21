package entity;

import org.example.GameEngine;
import org.example.MapSpace;


/**
 * Will be an Abstract class so don't initialize instances of this class except for testing
 */
public abstract class Carnivore extends Animal {


    protected int attackPower;


    public Carnivore(GameEngine engine, MapSpace m) {
        super(engine, m);

    }


    @Override
    public void setTarget() {
        super.setTarget();
        switch (status) {
            case HUNT:
                target = map.getClosestEntity("Herbivore", this);
                break;
            default:
                break;
        }
    }

    public void attack(Herbivore target) {
        if (target.isAlive()) {
            target.attacked(this);
            hunger += 10;
            if (hunger >= hungerCap) {
                hunger = hungerCap;
                status = Status.SLEEP;
            }
        }
    }


    public int getAttackPower() {
        return attackPower;

    }

    @Override
    public void interact(Entity entity) {
        super.interact(entity);
        if(entity instanceof Herbivore)
        {
            Herbivore prey = (Herbivore) entity;
            attack(prey);
        }
        else if(entity instanceof Carnivore && status == Status.REPRODUCE)
        {
            Carnivore c = (Carnivore) entity;
            c.reproduce(this);
            status = Status.HEARD;
        }
    }

    protected void reproduce(Carnivore mate) {
        // Check if we've reached the maximum number of carnivores
        if (map.carnivores.size() >= 60) {
            // Maximum reached, don't spawn a new carnivore
            return;
        }

        // Call the specific animal's reproduction method
        doReproduce(mate);
    }

    // Each specific carnivore type will implement this method
    protected abstract void doReproduce(Carnivore mate);

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.carnivores.add(this);
    }

    @Override
    public void die() {
        super.die();
        map.carnivores.remove(this);
    }
}
