package entity.Item;

import entity.DynamicEntity;
import entity.Entity;
import entity.util.PathNode;
import entity.util.Vision;
import org.example.*;

import java.awt.*;
import java.util.List;

public class AirShip extends DynamicEntity implements Vision {
    private boolean patrolling = false;
    private int currentNodeIndex = 0; // Each airship tracks its own position in the patrol route
    protected int visRadius = 7*engine.tileSize;


    public AirShip(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m);
        // Set airship images
        upPath1 = "/surveilance/airship/up.png";
        upPath2 = "/surveilance/airship/up.png";
        downPath1 = "/surveilance/airship/down.png";
        downPath2 = "/surveilance/airship/down.png";
        rightPath1 = "/surveilance/airship/right.png";
        rightPath2 = "/surveilance/airship/right.png";
        leftPath1 = "/surveilance/airship/left.png";
        leftPath2 = "/surveilance/airship/left.png";
        direction = Direction.UP;
        getSpriteImage();

        // Set default values
        velocity = 3; // Airships move faster than ground units
        patrolling = true; // Airships are always patrolling
    }

    @Override
    protected void updateStats() {
        // No stats to update for airships currently
    }

    @Override
    public void setTarget() {
        // If there are no patrol flags, don't set a target
        if (!map.airshipPathHandler.hasNodes()) {
            target = null;
            return;
        }

        // If we don't have a target or we've reached our current target
        if (target == null || (target instanceof PathNode && collides(target))) {
            // Get the next patrol flag index for this specific airship
            List<PathNode> nodes = map.airshipPathHandler.getNodes();
            if (nodes.isEmpty()) {
                target = null;
                return;
            }

            // Move to the next node in the sequence
            currentNodeIndex = (currentNodeIndex + 1) % nodes.size();
            PathNode nextFlag = nodes.get(currentNodeIndex);

            if (nextFlag != null) {
                target = (Entity) nextFlag;
            }
        }
    }

    @Override
    public void interact(Entity entity) {
        // When interacting with a patrol flag, we've reached it
        if (entity instanceof AirShipPatrolFlag) {
            // The patrol flag will call setTarget() to get the next flag
        } else if (entity instanceof AirShip) {
            // If we collide with another airship, slightly adjust our position to avoid getting stuck
            // This simulates airships avoiding each other in the air
            x += (int)(Math.random() * 20) - 10;
            y += (int)(Math.random() * 20) - 10;
        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entities.add(this);
        mapSpace.dynamicEntities.add(this);
        mapSpace.airships.add(this);

        // Set initial target if patrol flags exist
        if (mapSpace.airshipPathHandler.hasNodes()) {
            List<PathNode> nodes = map.airshipPathHandler.getNodes();
            if (!nodes.isEmpty()) {
                // Assign a random starting position in the patrol route for each airship
                // This helps distribute multiple airships around the patrol route
                if (mapSpace.airships.size() > 1) {
                    currentNodeIndex = (mapSpace.airships.size() - 1) % nodes.size();
                } else {
                    currentNodeIndex = 0;
                }

                PathNode firstFlag = nodes.get(currentNodeIndex);
                if (firstFlag != null) {
                    target = (Entity) firstFlag;
                }
            }
        }
    }

    @Override
    public void setDefaultValues() {
        setRandPos();
        velocity = 3;
        width = engine.tileSize;
        height = engine.tileSize;
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
    public void move() {
        if (target == null) setTarget();
        if (target == null) {
            return;
        }

        if (collidesOrIntersects(target)) {
            if(engine.canUpdateStats) {
                this.interact(target);
            }
            target = null;
            return;
        }

        int dx = xDelta(target);
        int dy = yDelta(target);
        int Dx = dx * velocity;
        int Dy = dy * velocity;
        int newX = x + Dx;
        int newY = y + Dy;

        changeDirection();
        x = newX;
        y = newY;
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
