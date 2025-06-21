package entity.Item;

import entity.DynamicEntity;
import entity.Entity;
import entity.util.PathNode;
import entity.util.Vision;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

import java.awt.*;
import java.util.List;

public class Drone extends DynamicEntity implements Vision {
    private boolean hasBattery;
    private long batteryEndDay; // The day when battery will run out
    private long rechargeEndDay; // The day when recharging will be complete
    private boolean isRecharging;
    private int currentNodeIndex = 0; // Each drone tracks its own position in the patrol route
    protected int visRadius = 7*engine.tileSize;

    public Drone(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m);
        // Set drone images
        upPath1 = "/surveilance/Drone/drone1.png";
        upPath2 = "/surveilance/Drone/drone2.png";
        downPath1 = "/surveilance/Drone/drone1.png";
        downPath2 = "/surveilance/Drone/drone2.png";
        rightPath1 = "/surveilance/Drone/drone1.png";
        rightPath2 = "/surveilance/Drone/drone2.png";
        leftPath1 = "/surveilance/Drone/drone1.png";
        leftPath2 = "/surveilance/Drone/drone2.png";
        direction = org.example.Direction.UP;
        getSpriteImage();

        // Set default values
        hasBattery = true;
        isRecharging = false;

        // Set battery to last for one day from current game time
        long currentDay = getCurrentGameDay();
        batteryEndDay = currentDay + 1;
    }

    /**
     * Helper method to get the current game day
     * @return The current game day
     */
    private long getCurrentGameDay() {
        // Parse the time string from GameEngine (format: MM:WW:DD:HH)
        String timeString = engine.getTimePassed();
        String[] timeParts = timeString.split(":");

        // Calculate total days
        long months = Long.parseLong(timeParts[0]);
        long weeks = Long.parseLong(timeParts[1]);
        long days = Long.parseLong(timeParts[2]);

        // Convert all to days (assuming 30 days per month, 7 days per week)
        return (months * 30) + (weeks * 7) + days;
    }

    @Override
    protected void updateStats() {
        long currentDay = getCurrentGameDay();

        // Check if drone is recharging
        if (isRecharging) {
            // If recharging is complete
            if (currentDay >= rechargeEndDay) {
                hasBattery = true;
                isRecharging = false;
                batteryEndDay = currentDay + 1; // Battery lasts for one more day
                velocity = 3; // Restore normal speed
            } else {
                velocity = 0; // Can't move while recharging
            }
            return;
        }

        // Check if battery has run out
        if (currentDay >= batteryEndDay) {
            hasBattery = false;
            // Still allow movement to reach a charging station, but at reduced speed
            velocity = 1; // Reduced speed when battery is depleted (emergency power)
        } else {
            velocity = 3; // Normal speed when battery is good
        }
    }

    @Override
    public void setTarget() {
        // If drone is recharging, it can't move
        if (isRecharging) {
            return;
        }

        // If battery is depleted, prioritize finding a charging station
        if (!hasBattery) {
            // If we already have a charging station as target, keep it
            if (target instanceof ChargingStation) {
                return;
            }

            // Find the nearest charging station
            ChargingStation nearestStation = findNearestChargingStation();
            if (nearestStation != null) {
                target = nearestStation;
                return;
            } else {
                // If no charging station found, drone will remain stationary
                target = null;
                return;
            }
        }

        // Normal patrol behavior when battery is good

        // If there are no drone patrol flags, don't set a target
        if (!map.dronePathHandler.hasNodes()) {
            target = null;
            return;
        }

        // If we don't have a target or we've reached our current target
        if (target == null || (target instanceof PathNode && collides(target))) {
            // Get the next patrol flag index for this specific drone
            List<PathNode> nodes = map.dronePathHandler.getNodes();
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

    private ChargingStation findNearestChargingStation() {
        ChargingStation nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (ChargingStation station : map.chargingStations) {
            int distance = distance(station);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = station;
            }
        }

        return nearest;
    }

    public void rechargeBattery() {
        // Start recharging process
        isRecharging = true;

        // Set recharge to complete after one day
        long currentDay = getCurrentGameDay();
        rechargeEndDay = currentDay + 1;
    }

    @Override
    public void interact(Entity entity) {
        // When interacting with a patrol flag, we've reached it
        if (entity instanceof DronePatrolFlag) {
            // The patrol flag will call setTarget() to get the next flag
            // Only interact with patrol flags if we have battery
            if (hasBattery && !isRecharging) {
                setTarget();
            }
        } else if (entity instanceof ChargingStation) {
            // Recharge battery at charging station if needed
            if (!hasBattery && !isRecharging) {
                rechargeBattery();
            }
        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        // Check if there's at least one charging station on the map
        if (mapSpace.chargingStations.isEmpty()) {
            return;
        }

        mapSpace.entities.add(this);
        mapSpace.dynamicEntities.add(this);
        mapSpace.drones.add(this);

        // Set initial target if patrol flags exist
        if (mapSpace.dronePathHandler.hasNodes()) {
            List<PathNode> nodes = map.dronePathHandler.getNodes();
            if (!nodes.isEmpty()) {
                // Assign a random starting position in the patrol route for each drone
                if (mapSpace.drones.size() > 1) {
                    currentNodeIndex = (mapSpace.drones.size() - 1) % nodes.size();
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

    public boolean hasBattery() {
        return hasBattery;
    }

    public boolean isRecharging() {
        return isRecharging;
    }

    public long getBatteryEndDay() {
        return batteryEndDay;
    }

    public long getRechargeEndDay() {
        return rechargeEndDay;
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
