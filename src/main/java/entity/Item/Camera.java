package entity.Item;

import entity.Building;
import entity.Entity;
import entity.util.Vision;
import org.example.GameEngine;
import org.example.MapSpace;

import java.awt.*;

public class Camera extends Building implements Vision {
    protected int visRadius = 7*engine.tileSize;
    protected MapSpace map;



    public Camera(GameEngine engine, MapSpace map) {
        super(engine);
        this.map = map;
        upPath1 = "/surveilance/camera/camera1.png";
        upPath2 = "/surveilance/camera/camera2.png";
        getSpriteImage();
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        super.spawn(mapSpace);
        mapSpace.cameras.add(this);
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
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        if (selected)
        {
            g2d.setColor(new Color(0, 0, 255, 50)); // Semi-transparent blue
            g2d.fillOval(getCenter().x - visRadius, getCenter().y - visRadius, visRadius * 2, visRadius * 2);
        }

    }
}
