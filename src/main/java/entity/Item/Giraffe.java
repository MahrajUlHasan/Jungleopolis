package entity.Item;

import entity.Carnivore;
import entity.Entity;
import entity.Herbivore;
import entity.Status;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public class Giraffe extends Herbivore {

    public Giraffe(GameEngine engine, MapSpace m) {
        super(engine, m);
        upPath1 = "/Animal/giraffe/up1.png";
        upPath2 = "/Animal/giraffe/up2.png";
        downPath1 = "/Animal/giraffe/down1.png";
        downPath2 = "/Animal/giraffe/down2.png";
        rightPath1 = "/Animal/giraffe/right1.png";
        rightPath2 = "/Animal/giraffe/right2.png";
        leftPath1 = "/Animal/giraffe/left1.png";
        leftPath2 = "/Animal/giraffe/left2.png";
        getSpriteImage();

    }

    @Override
    protected void setStats() {

    }

    @Override
    public void attacked(Carnivore carnivore) {

    }


    @Override
    public void setTarget() {
        super.setTarget();
        // If status is HEARD, the parent class will handle it with ghost entities
        // Only handle other statuses here
        if (status != Status.HEARD) {
            switch (status) {
                case REPRODUCE:
                    target = map.getClosestEntity("Giraffe", this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void doReproduce(Herbivore mate) {
        if (mate instanceof Giraffe) {
            // Create a new giraffe offspring
            Giraffe offspring = new Giraffe(engine, engine.getMap());
            engine.getMap().addEnt(offspring);
            status = Status.HEARD; // Reset status after reproduction
        }
    }


    @Override
    public void interact(Entity entity) {
        // Giraffe-specific action behavior
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.giraffes.add(this);

    }
}