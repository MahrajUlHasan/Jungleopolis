package entity.Item;

import entity.Carnivore;
import entity.Status;
import org.example.GameEngine;
import org.example.MapSpace;

import java.util.Random;

public class Lion extends Carnivore {

    @Override
    public void updateStats() {
        super.updateStats();
    }

    public Lion(GameEngine engine, MapSpace m) {
        super(engine, m);
        upPath1 = "/Animal/up1.png";
        upPath2 = "/Animal/up2.png";
        downPath1 = "/Animal/down1.png";
        downPath2 = "/Animal/down2.png";
        rightPath1 = "/Animal/right1.png";
        rightPath2 = "/Animal/right2.png";
        leftPath1 = "/Animal/left1.png";
        leftPath2 = "/Animal/left2.png";
        getSpriteImage();
        setStats();
        maxHp = 100;
        maxAge = 500;
        cost = 100;
        attackPower = 10;
    }

    @Override
    public void setTarget() {
        super.setTarget();
        // If status is HEARD, the parent class will handle it with ghost entities
        // Only handle other statuses here
        if (status != Status.HEARD) {
            switch (status) {
                case REPRODUCE:
                    target = map.getClosestEntity("Lion", this);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void doReproduce(Carnivore mate) {
        if (mate instanceof Lion) {
            Lion offspring = new Lion(engine, engine.getMap());
            engine.getMap().addEnt(offspring);
            status = Status.HEARD; // Reset status after reproduction
        }
    }

    protected void placeOffspring(Lion offspring) {
        Random rand = new Random();
        int distance = (int) (engine.tileSize * 1.5);
        int angle = 0;
        while (engine.getMap().isBlocked(offspring, 0, 0)) {
            int r = rand.nextInt() % 360;
            int closeX = (int) (distance * Math.cos((float) r));
            int closeY = (int) (distance * Math.sin((float) r));
            offspring.setX(closeX);
            offspring.setY(closeY);
        }
    }

    @Override
    protected void setStats() {
        attackPower = 10;
        health = 100;
        ///TODO : add stats
    }


    @Override
    public void setDefaultValues() {
        super.setDefaultValues();
        setStats();
        maxHp = 100;
        maxAge = 500;
        cost = 100;
        attackPower = 10;
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.lions.add(this);
    }

    @Override
    public void die() {
        super.die();
        map.lions.remove(this);
    }
}
