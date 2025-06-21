package entity.Item;

import entity.DynamicEntity;
import entity.Entity;
import entity.util.jeepAndVisitorHandler;
import entity.util.Vision;
import org.example.GameEngine;
import org.example.MapSpace;

import javax.swing.*;
import java.awt.*;

public class Jeep extends DynamicEntity implements Vision {

    public int pickUpBuffer = 0;
    protected jeepAndVisitorHandler handler;
    public Road currRoad = null;
    public Road prevRoad = null;
    public Boolean inReverse = false;
    protected int visRadius = 7 * engine.tileSize;
    public int passengers = 0;
    public int maxPassengerCount = 4;

    public Jeep(GameEngine engine, MapSpace map) {
        super(engine, map);
        upPath1 = "/Jeep/jeepup.png";
        upPath2 = "/Jeep/jeepup.png";
        rightPath1 = "/Jeep/jeepRight.png";
        rightPath2 = "/Jeep/jeepRight.png";
        leftPath1 = "/Jeep/jeepLeft.png";
        leftPath2 = "/Jeep/jeepLeft.png";
        downPath1 = "/Jeep/jeepdown.png";
        downPath2 = "/Jeep/jeepdown.png";
        getSpriteImage();
        handler = map.jeepHandler;
        velocity = 1;
    }

    @Override
    protected void updateStats() {

    }

    @Override
    public void spawn(MapSpace mapSpace) {
        if (!jeepAndVisitorHandler.hasRoad()) {
            JOptionPane.showMessageDialog(engine,
                    "Cannot place drone: You need to place a roads first!",
                    "Placement Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mapSpace.entitiesToAdd.add(this);
        mapSpace.dynamicEntities.add(this);
        mapSpace.jeeps.add(this);
        handler.addJeep(this);
//        this.y = (int)Math.floor((float)y/(float)engine.tileSize) * engine.tileSize;
//        this.x = (int)Math.floor((float)x/(float)engine.tileSize) * engine.tileSize;

    }


    @Override
    public void move() {
        target = map.jeepHandler.nextRoad(this);
        changeDirection();
        redBuffer();
        int dx = xDelta(target);
        int dy = yDelta(target);
        int Dx = dx * velocity;
        int Dy = dy * velocity;
        int newX = x + Dx;
        int newY = y + Dy;
        if (newX == target.x && newY == target.y) {
            if (target.equals(map.jeepHandler.start)) {
                map.jeepHandler.pickupVisitors(this);
            }
            else if (target.equals(map.jeepHandler.end)) {
                passengers = 0;
            }
            prevRoad = currRoad;
            currRoad = (Road) target;
        }
        if (currRoad == jeepAndVisitorHandler.start && x == currRoad.x && y == currRoad.y) {
            if(passengers<maxPassengerCount) {
            map.jeepHandler.pickupVisitors(this);}
        }
        if (currRoad == map.jeepHandler.end && x == currRoad.x && y == currRoad.y) {
            passengers = 0;
        }
        if (this.collides(map.jeepHandler.end)) {
            passengers = 0;
        }
        x = newX;
        y = newY;


    }

    private void redBuffer() {
        if (pickUpBuffer > 0) {
            pickUpBuffer--;
        }
    }

    @Override
    protected int xDelta(Entity e) {

        if (e.x - x == 0) {
            return 0;
        }
        return e.x - x > 0 ? 1 : -1;
    }

    @Override
    protected int yDelta(Entity e) {
        if (e.y - y == 0) {
            return 0;
        }
        return e.y - y > 0 ? 1 : -1;
    }

    @Override
    public void setTarget() {

    }

    @Override
    public void interact(Entity entity) {

    }

    @Override
    public int getVisRadius() {
        return visRadius;
    }

    @Override
    public Entity[] inVision() {
        return map.animals.stream()
                .filter(poacher -> {
                    int dx = poacher.getX() - this.getX();
                    int dy = poacher.getY() - this.getY();
                    return dx * dx + dy * dy <= getVisRadius() * getVisRadius();
                })
                .toArray(Entity[]::new);
    }

    public int getPopularity() {
        return (passengers * inVision().length) / 4;
    }

    @Override
    public void markVisible() {

    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);


        // Draw semi-transparent circle
        int circleX = getX() + width/2 -5; // Adjust position as needed
        int circleY = getY() + height/2 -5; // Adjust position as needed
        int circleDiameter = 20;
        g2d.setColor(new Color(0, 0, 255, 80)); // Semi-transparent blue
        g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);

        // Draw visitor number
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.valueOf(passengers), circleX + 5, circleY + 13);
    }
}
