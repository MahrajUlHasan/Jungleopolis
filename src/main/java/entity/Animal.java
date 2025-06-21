package entity;

import entity.Item.Person;
import entity.Item.Poacher;
import entity.Item.Ranger;
import entity.util.GhostEntity;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

import java.util.Random;

public abstract class Animal extends DynamicEntity {

    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;

    protected int health = 3000;
    protected final int maxHealth = 5000;
    protected int thirst = 100; /// higher means not thirsty
    protected int thirstCap = 100;
    public int age= 0;
    public int maxAge = 100;
    protected int hungerCap = 100;
    protected int hunger  = 100; /// higher means it's belly is full
    protected int hungerThreshold = 70; /// indicates the point at which the animal will be hungry
    protected int thirstThreshold = 50;
    protected int ageThreshold = 20; /// when it reaches adulthood and reproduces, also it reproduces every ageThreshold time
    protected int lastReproduceAge = 0;

    // Ghost entity for herding behavior
    protected GhostEntity ghostTarget = null;
    protected int ghostCreationCooldown = 0; // Cooldown to prevent creating too many ghost entities

    // Movement tracking for detecting stuck animals
    protected int prevX = -1;
    protected int prevY = -1;
    protected int stuckCounter = 0;
    protected static final int STUCK_THRESHOLD = 6; // Number of updates to consider an animal stuck


    public Animal(GameEngine engine, MapSpace m) {

        super(engine, m);
        //TODO : remove this later , it's needed for testing rn
//        status = Status.THIRST;
        //
        setStats();


    }

    public void attacked(Person  person) {
        if(person instanceof Poacher)
        {
            Poacher poacher = (Poacher) person;
            health -= poacher.getAttackPower();
            if (health <= 0) {
                die();
            }
        }
        if (person instanceof Ranger)
        {
            Ranger ranger = (Ranger) person;
            health -= ranger.getAttackPower();
            if (health <= 0) {
                die();
            }
        }


    }

    /**
     * Sets the stats of the animal. This method should be overridden in subclasses
     * stats like health, hunger, thirst, and age and each of their thresholds.
     */
    protected abstract void setStats() ;


    @Override
    public void setTarget()
    {
        switch (status)
        {
            case THIRST:
                target = map.getClosestEntity("Pond", this);
                break;
            case SLEEP:
                target = null;
                break;
            case HEARD:
                // If we're the oldest animal, we don't need to follow anyone
                Animal oldest = map.getOldest(this);
                if (oldest == this) {
                    target = null;
                    return;
                }

                // If we have a ghost target and it's still valid, keep using it
                if (ghostTarget != null && map.entities.contains(ghostTarget)) {
                    target = ghostTarget;
                    return;
                }

                // Otherwise, create a new ghost target if cooldown allows
                if (ghostCreationCooldown <= 0) {
                    createGhostTarget(oldest);
                    ghostCreationCooldown = 100; // Set cooldown to prevent creating too many ghosts
                } else {
                    ghostCreationCooldown--;
                }


                    target = ghostTarget;

                break;
            default:
                break;
        }
    }

    /**
     * Creates a ghost entity near the oldest animal of the same type
     * @param oldest The oldest animal of the same type
     */
    protected void createGhostTarget(Animal oldest) {
        if (oldest == null || oldest == this) {
            return;
        }

        // Create a ghost entity near the oldest animal
        ghostTarget = GhostEntity.createNearOldest(engine, map, this, oldest);

        // Add the ghost entity to the map
        if (ghostTarget != null) {
            ghostTarget.spawn(map);
        }
    }


    @Override
    public void interact(Entity entity) {
        if(entity instanceof Building)
        {
            Building building = (Building) entity;
            building.interact(this);
        }

        // If we interact with our ghost target, create a new one
        if (entity == ghostTarget) {
//            ghostTarget = null;
            ghostCreationCooldown = 0; // Reset cooldown to allow immediate creation of a new ghost
        }
    }


    public void drinkWater() {
        this.thirst += 2;
        if(thirst > thirstCap)
        {
            thirst = thirstCap;
            status = Status.HEARD;
        }

    }

    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entitiesToAdd.add(this);
        mapSpace.dynamicEntities.add(this);
        mapSpace.animals.add(this);
    }


    public  void updateStats() {
        incAge();
        decHunger();
        decThirst();
        checkIfStuck();
    }

    /**
     * Checks if the animal is stuck (hasn't moved in a while) and respawns it if necessary.
     * Only applies to animals that are not the oldest and not sleeping.
     */
    protected void checkIfStuck() {
        // Initialize previous position if this is the first check
        if (prevX == -1 || prevY == -1) {
            prevX = x;
            prevY = y;
            return;
        }


        // Only check for stuck animals that are not the oldest and not sleeping
        if (status != Status.SLEEP) {
            // Check if position hasn't changed
            if (x == prevX && y == prevY) {
                stuckCounter++;

                // If stuck for too long, respawn the animal
                if (stuckCounter >= STUCK_THRESHOLD) {
                    respawnNearby();
                }
            } else {
                // Reset counter if the animal has moved
                stuckCounter = 0;
            }
        }

        // Update previous position
        prevX = x;
        prevY = y;
    }

    /**
     * Respawns the animal at a new position near its current location.
     * Makes sure the new position doesn't collide with other entities.
     */
    protected void respawnNearby() {
        // Calculate a random position near the current position
        Random rand = new Random();
        int maxDistance = engine.tileSize * 5; // Maximum distance to respawn
        int minDistance = engine.tileSize * 2; // Minimum distance to respawn

        // Try to find a valid position
        int newX, newY;
        int attempts = 0;
        int maxAttempts = 20; // Maximum number of attempts to find a valid position
        boolean isBlocked = false;

        do {
            // Calculate random angle and distance
            double angle = rand.nextDouble() * 2 * Math.PI;
            int distance = minDistance + rand.nextInt(maxDistance - minDistance);

            // Calculate new position
            newX = x + (int)(distance * Math.cos(angle));
            newY = y + (int)(distance * Math.sin(angle));

            // Make sure the new position is within the screen bounds
            newX = Math.max(0, Math.min(newX, engine.screenWidth - width));
            newY = Math.max(0, Math.min(newY, engine.screenHeight - height));

            attempts++;

            // Save current position
            int oldX = x;
            int oldY = y;

            // Temporarily move to check if the new position is valid
            x = newX;
            y = newY;
            isBlocked = map.isBlocked(this, 0, 0);

            // Restore original position
            x = oldX;
            y = oldY;

        } while (isBlocked && attempts < maxAttempts);

        // If we found a valid position, move the animal there
        if (attempts < maxAttempts) {
            x = newX;
            y = newY;

        }

        // Reset stuck counter
        stuckCounter = 0;

        // If we have a ghost target, remove it so we can create a new one
        if (ghostTarget != null && map.entities.contains(ghostTarget)) {
            map.entitiesToRemove.add(ghostTarget);
            ghostTarget = null;
            ghostCreationCooldown = 0;
        }
    }

    private void decThirst() {
        thirst--;
        if(thirst < 0)
        {
            die();
            return;
        }
        if(thirst < thirstThreshold)
        {
            status = Status.THIRST;
        }

    }

    private void decHunger() {
        hunger--;
        if(hunger < 0)
        {
            die();
            return;
        }
        if(hunger < hungerThreshold)
        {
            status = Status.HUNT;
        }
    }

    private void incAge() {
        age++;
        if(age > maxAge)
        {
            die();
        }
        if (age >= ageThreshold && (age - lastReproduceAge) >= ageThreshold) {
            status = Status.REPRODUCE;
            lastReproduceAge = age; // Update the last reproduction age
        }

    }

    public void die() {
        alive = false;
        map.animals.remove(this);
        map.entitiesToRemove.add(this);
        map.dynamicEntities.remove(this);

        // Clean up ghost target if it exists
        if (ghostTarget != null && map.entities.contains(ghostTarget)) {
            map.entitiesToRemove.add(ghostTarget);
            ghostTarget = null;
        }
    }
}
