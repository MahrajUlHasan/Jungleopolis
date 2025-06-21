package entity.util;

import entity.Animal;
import entity.Entity;
import org.example.GameEngine;
import org.example.MapSpace;

import java.awt.*;
import java.util.Random;

/**
 * GhostEntity is a special entity used for animal herding behavior.
 * It's invisible and non-collidable, serving as a target for animals in herding mode.
 * Each animal creates its own ghost entity near the oldest animal of its type.
 */
public class GhostEntity extends Entity {
    private Animal owner;
    private Animal oldestAnimal;
    private int lifespan = 50; // How many update cycles the ghost entity lives for
    private int age = 0;
    private MapSpace map;
    private GameEngine engine;
    
    // Getters for testing
    public Animal getOwner() { return owner; }
    public Animal getOldestAnimal() { return oldestAnimal; }
    public int getAge() { return age; }
    public MapSpace getMap() { return map; }
    public GameEngine getGameEngine() { return engine; }

    public GhostEntity(GameEngine engine, MapSpace map, Animal owner, Animal oldestAnimal, int x, int y) {
        this.engine = engine;
        this.map = map;
        this.owner = owner;
        this.oldestAnimal = oldestAnimal;
        this.x = x;
        this.y = y;
        this.width = engine.getTileSize() / 2;
        this.height = engine.getTileSize() / 2;
        this.COLLIDABLE = false; // Animals can move through ghost entities
    }

    @Override
    public void setDefaultValues() {
        // No default values needed
    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Ghost entities are invisible
        // Uncomment for debugging to see ghost entities
        // g2d.setColor(Color.YELLOW);
        // g2d.drawOval(x, y, width, height);
    }

    @Override
    public void update() {
        age++;

        // Remove the ghost entity if it's too old or if the owner or oldest animal is gone
        if (age >= lifespan || !owner.isAlive() || !oldestAnimal.isAlive() ||
            !map.animals.contains(owner) || !map.animals.contains(oldestAnimal)) {
            map.entitiesToRemove.add(this);
            return;
        }

        // If the owner gets too close to the ghost entity, remove it
        // This prevents animals from constantly moving toward the same spot
        if (owner.distance(this) < engine.getTileSize()) {
            map.entitiesToRemove.add(this);
        }
    }

    @Override
    public void interact(Entity entity) {
        // When an animal reaches its ghost entity, the ghost entity disappears
        if (entity == owner) {
            map.entitiesToRemove.add(this);
        }
    }

    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entitiesToAdd.add(this);
        mapSpace.entities.add(this);
    }

    /**
     * Creates a ghost entity at a random position near the oldest animal
     * @param engine The game engine
     * @param map The map space
     * @param owner The animal that owns this ghost entity
     * @param oldestAnimal The oldest animal of the same type
     * @return The created ghost entity
     */
    public static GhostEntity createNearOldest(GameEngine engine, MapSpace map, Animal owner, Animal oldestAnimal) {
        Random rand = new Random();

        // Calculate a random position near the oldest animal
        int distance = engine.getTileSize() * 4; // Distance from the oldest animal
        int angle = rand.nextInt(360); // Random angle

        // Convert angle to radians
        double radians = Math.toRadians(angle);

        // Calculate position using polar coordinates
        int ghostX = oldestAnimal.getX() + (int)(distance * Math.cos(radians));
        int ghostY = oldestAnimal.getY() + (int)(distance * Math.sin(radians));

        // Create and return the ghost entity
        GhostEntity ghost = new GhostEntity(engine, map, owner, oldestAnimal, ghostX, ghostY);
        return ghost;
    }
}