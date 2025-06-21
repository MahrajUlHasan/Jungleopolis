package entity.Item;

import entity.*;
import org.example.*;
import entity.util.Vision;

import java.awt.*;

public class Ranger extends Person implements Vision {
    protected int attackPower = 30;
    protected int visRadius = 7 * engine.tileSize;
    protected long purchasedMonth;
    private Animal animalTarget = null;


    public Ranger(GameEngine game, MapSpace m, KeyHandler keyH, MouseHandler mH) {
        super(game, m);
        upPath1 = "/People/Rangers/up1.png";
        upPath2 = "/People/Rangers/up2.png";
        downPath1 = "/People/Rangers/down1.png";
        downPath2 = "/People/Rangers/down2.png";
        rightPath1 = "/People/Rangers/right1.png";
        rightPath2 = "/People/Rangers/right2.png";
        leftPath1 = "/People/Rangers/left1.png";
        leftPath2 = "/People/Rangers/left2.png";
        getSpriteImage();
        COLLIDABLE = false;

    }

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public void setDefaultValues() {
        setRandPos();
        velocity = 2;
        purchasedMonth = engine.getMonthsPassed();
        direction = Direction.UP;
        width = engine.tileSize;
        height = engine.tileSize;


    }



    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entities.add(this);
        mapSpace.dynamicEntities.add(this);
        mapSpace.rangers.add(this);
    }

    @Override
    protected void die() {
        super.die();
        map.rangers.remove(this);
    }

    @Override
    public void setTarget() {
        if(animalTarget != null)
        {
            if(animalTarget.isAlive()) {
                target = animalTarget;
                return;
            }
            else {
                animalTarget = null;
            }
        }

        target = map.getClosestSelectedAnimalAndPoacher(this);
        if (target instanceof Poacher) {
            Poacher poacher = (Poacher) target;
//            poacher.setUnderPersue(true);
//            System.out.println("Ranger is persuing poacher");

        }

    }


    @Override
    public void interact(Entity entity) {
        if (entity instanceof Animal) {
            target = entity;
            Animal animal = (Animal) entity;
            attack(animal);

            if (!animal.isAlive()) {
                //TODO earn money
                engine.updateCash(500);
            }
        } else if (entity instanceof Poacher) {
            Poacher poacher = (Poacher) entity;
            attack(poacher);

            if (!poacher.isAlive()) {
                engine.updateCash(1000);
            }
        }
    }

    @Override
    public void move() {
        super.move();
        if(engine.canUpdateStats) {
            interact(target);
        }
    }

    public void attack(Entity entity) {

        if (entity instanceof Animal) {
            Animal animal = (Animal) entity;
            if (animal.isAlive()) {
                animal.attacked(this);
            }

        } else if (entity instanceof Poacher) {
            Poacher poacher = (Poacher) entity;
            if (poacher.isAlive()) {
                poacher.attacked(this);
            }

        }

    }

    @Override
    public int getVisRadius() {
        return visRadius;
    }

    @Override
    public Entity[] inVision() {
        return map.poachers.stream()
                .filter(poacher -> {
                    int dx = poacher.getX() - this.getX();
                    int dy = poacher.getY() - this.getY();
                    return dx * dx + dy * dy <= getVisRadius() * getVisRadius();
                })
                .toArray(Entity[]::new);
    }


    @Override
    public void markVisible() {
        for (Entity entity : inVision()) {
            entity.setVisible();
        }
    }


    @Override
    public void update() {
        super.update();
        map.markVisiblePoachers();
        if (inVision().length != 0) {
            performAction(inVision()[0]);
        }


        if (purchasedMonth != engine.getMonthsPassed()) {
            die();
        }

        // If I’m currently chasing a poacher who just went invisible, drop them
        if (target instanceof Poacher) {
            Poacher p = (Poacher) target;
            if (!p.isVisible()) {
                target = null;

            }
        }
    }

    public void performAction(Entity selectedEntity) {
        if (selectedEntity instanceof Poacher ) {
            target = selectedEntity;
            map.markVisiblePoachers();
        }
        if (selectedEntity instanceof Animal) {
            target = selectedEntity;
            animalTarget = (Animal) selectedEntity;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        if (selected) {
            g2d.setColor(new Color(0, 0, 255, 50)); // Semi-transparent blue
            g2d.fillOval(getCenter().x - visRadius, getCenter().y - visRadius, visRadius * 2, visRadius * 2);
        }

    }
}
