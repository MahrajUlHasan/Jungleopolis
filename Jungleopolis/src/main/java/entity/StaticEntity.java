package entity;

import org.example.GameEngine;
import org.example.MouseHandler;

import java.awt.*;

public class StaticEntity extends Entity {

    protected GameEngine engine;
    protected MouseHandler mouseH;

    StaticEntity(GameEngine gameEngine, MouseHandler mouseHandler) {
        engine = gameEngine;
        mouseH = mouseHandler;

    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String getType() {
        return "StaticEntity";
    }

    @Override
    public void draw(Graphics2D g2d) {

    }

    @Override
    public void update() {

    }
}
