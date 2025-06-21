package entity.Item;

import entity.Animal;
import entity.Entity;
import org.example.*;

import java.awt.*;

public class Poacher extends Person{
    protected int attackPower = 20;
    protected int animalsKilled = 0;
    protected int health = 100;
    protected boolean underPersue = false;


    public Poacher(GameEngine game, MapSpace m, KeyHandler keyH, MouseHandler mH) {
        super(game, m);
        upPath1 = "/People/Poachers/up1.png";
        upPath2 = "/People/Poachers/up2.png";
        downPath1 = "/People/Poachers/down1.png";
        downPath2 = "/People/Poachers/down2.png";
        rightPath1 = "/People/Poachers/right1.png";
        rightPath2 = "/People/Poachers/right2.png";
        leftPath1 = "/People/Poachers/left1.png";
        leftPath2 = "/People/Poachers/left2.png";
        getSpriteImage();
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setUnderPersue(boolean underPersue) {
        this.underPersue = underPersue;
    }
    public boolean isUnderPersue() {
        return underPersue;
    }

    public void attack(Animal target) {
        if (target.isAlive()) {
            target.attacked(this);

        }
    }

    @Override
    public void setDefaultValues() {
        setRandPos();
        visible = false;
        velocity = 1;
        direction = Direction.UP;
        width = engine.tileSize;
        height = engine.tileSize;


    }
    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.poachers.add(this);
    }

    @Override
    protected void die() {
        super.die();
        map.poachers.remove(this);
    }

    @Override
    public void setTarget()
    {
        if (isUnderPersue()) {
            target = map.getFurthestEntity( this);
            return;
        }

     target = map.getClosestEntity("Animal", this);


    }

    @Override
    public void interact(Entity entity) {
        super.interact(entity);
        if(entity instanceof Animal)
        {
            Animal prey = (Animal) entity;
            attack(prey);

            if (!prey.isAlive()) {
                animalsKilled++;
            }
            if (animalsKilled == engine.getDifficulty()) {
                die();
            }
        }
    }

    public void attacked(Ranger ranger) {
        health -= ranger.getAttackPower();
        System.out.println(health);
        if (health <= 0) {
            die();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (!isVisible()) {
            // Draw a dark circle around the poacher
            // Gradually fade out the poacher instead of abrupt disappearance
            int alpha = 100; // Semi-transparent
            g2d.setColor(new Color(0, 0, 0, alpha));
            g2d.fillOval(x + width / 2 - engine.tileSize, y + height / 2 - engine.tileSize, engine.tileSize * 2, engine.tileSize * 2);
        } else {
            // Draw the sprite as usual
            super.draw(g2d);
        }
    }

    @Override
    public void update() {
        super.update();
        map.updatePoacherPursuitStatus(this);

    }
}
