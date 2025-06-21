package entity.Item;

import entity.Carnivore;
import entity.Entity;
import entity.Herbivore;
import entity.Status;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public class Buffalo extends Herbivore {



    public Buffalo(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m);
        upPath1 = "/Animal/buffalo/up1.png";
        upPath2 = "/Animal/buffalo/up2.png";
        downPath1 = "/Animal/buffalo/down1.png";
        downPath2 = "/Animal/buffalo/down2.png";
        rightPath1 = "/Animal/buffalo/right1.png";
        rightPath2 = "/Animal/buffalo/right2.png";
        leftPath1 = "/Animal/buffalo/left1.png";
        leftPath2 = "/Animal/buffalo/left2.png";
        getSpriteImage();
    }


    @Override
    public void attacked(Carnivore carnivore) {
        health -= carnivore.getAttackPower();
        if (health <= 0) {
            die();
        }

    }


    @Override
    public void setTarget() {
        super.setTarget();
        // If status is HEARD, the parent class will handle it with ghost entities
        // Only handle other statuses here
        if (status != Status.HEARD) {
            switch(status) {
                case REPRODUCE:
                    target = map.getClosestEntity("Buffalo", this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void doReproduce(Herbivore mate) {
        if (mate instanceof Buffalo) {
            Buffalo offspring = new Buffalo(engine, engine.getMap(), this.keyHandler, this.mouseHandler);
            engine.getMap().addEnt(offspring);
            status = Status.HEARD; // Reset status after reproduction
        }
    }


    @Override
    public void move() {
        /// if target is too close don't move
        super.move();
    }




    @Override
    public void interact(Entity entity) {
        super.interact(entity);

    }

    @Override
    public void updateStats() {
        super.updateStats();
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.buffalos.add(this);

    }

    @Override
    public void die() {
        super.die();
        map.buffalos.remove(this);
    }

    @Override
    protected void setStats() {
        health = 200;
        ///TODO : add stats
    }


    @Override
    public void setDefaultValues() {
        super.setDefaultValues();
        setStats();
//        maxHp = 100;
//        maxAge = 500;
//        cost = 100;
    }
}