package org.example;

import entity.*;
import entity.Item.*;
import entity.util.jeepAndVisitorHandler;
import entity.util.PathHandler;
import java.util.List; // Ensure this import is used instead of java.awt.List

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;


public class MapSpace {
    // Constants for UI panel dimensions
    private static final int STATS_PANEL_HEIGHT = 52;  // StatsPanel height
    private static final int INVENTORY_PANEL_HEIGHT = 80; // Approximate InventoryPanel height

    public ArrayList<Entity> entities;
    public final ArrayList<Entity> entitiesToAdd = new ArrayList<>(); // Temporary list for new entities
    public final ArrayList<Entity> entitiesToRemove = new ArrayList<>(); // Temporary list for removed entities

    public ArrayList<DynamicEntity> dynamicEntities = new ArrayList<>();
    public ArrayList<Animal> animals = new ArrayList<>();
    public ArrayList<Herbivore> herbivores = new ArrayList<>();
    public ArrayList<Carnivore> carnivores = new ArrayList<>();
    public ArrayList<Person> people = new ArrayList<>();
    public ArrayList<Building> buildings = new ArrayList<>();
    public ArrayList<Poacher> poachers = new ArrayList<>();
    public ArrayList<Lion> lions = new ArrayList<>();
    public ArrayList<Wolf> wolves = new ArrayList<>();
    public ArrayList<Buffalo> buffalos = new ArrayList<>();
    public ArrayList<Person> visitors = new ArrayList<>();
    public ArrayList<Giraffe> giraffes = new ArrayList<>();
    public ArrayList<Pond> ponds = new ArrayList<>();
    public ArrayList<Grass> grasses = new ArrayList<>();
    public ArrayList<Road> roads = new ArrayList<>();
    public ArrayList<Jeep>  jeeps = new ArrayList<>();
    public ArrayList<Ranger> rangers = new ArrayList<>();
    public ArrayList<Camera> cameras = new ArrayList<>();
    public ArrayList<AirShipPatrolFlag> airShipPatrolFlags = new ArrayList<>();
    public ArrayList<AirShip> airships = new ArrayList<>();
    public ArrayList<DronePatrolFlag> dronePatrolFlags = new ArrayList<>();
    public ArrayList<Drone> drones = new ArrayList<>();
    public ArrayList<ChargingStation> chargingStations = new ArrayList<>();

    public GameEngine game;

    protected Point[][] grid;

    public jeepAndVisitorHandler jeepHandler;
    public PathHandler airshipPathHandler;
    public PathHandler dronePathHandler;


    public MapSpace(GameEngine game) {
        entities = new ArrayList<>();
        dynamicEntities = new ArrayList<>();
        animals = new ArrayList<>();
        buildings = new ArrayList<>();
        airShipPatrolFlags = new ArrayList<>();
        airships = new ArrayList<>();
        dronePatrolFlags = new ArrayList<>();
        drones = new ArrayList<>();
        chargingStations = new ArrayList<>();
        this.game = game;

        // Calculate the playable area dimensions, accounting for UI panels
        int playableHeight = game.screenHeight ;
        int gridRows = playableHeight / game.tileSize;
        int gridCols = game.screenWidth / game.tileSize;

        grid = new Point[gridCols][gridRows];

        // Initialize grid with adjusted positions, starting after the stats panel
        for (int i = 0; i < gridCols; i++) {
            for (int j = 0; j < gridRows; j++) {
                // Offset y-coordinate by STATS_PANEL_HEIGHT to start grid after the stats panel
                grid[i][j] = new Point(i * game.tileSize, j * game.tileSize);
            }
        }

        jeepHandler = new jeepAndVisitorHandler(this);
        airshipPathHandler = new PathHandler();
        dronePathHandler = new PathHandler();
    }


    /// METHODS
    public void addEnt(Entity ent) {
        ent.spawn(this);
        entities.sort((a, b) -> {
            if (a instanceof Road) {
                return -1; // Road comes before Jeep
            } else if (b instanceof Road) {
                return 1; // Jeep comes after Road
            }
            return a.y - b.y; // Default sorting by y value
        });
    }


    public void initRoads()
    {
        jeepHandler.init(this);
    }

    /**
     * @param x mouse x position
     * @param y mouse y position
     * @return the first Entity that contains the point with coordinates (x,y)
     */
    public Entity getEntityAt(int x, int y) {
        return entities.stream().toList().stream().filter(e -> e.getHitBox().contains(x, y)).findFirst().orElse(null);
    }

    public Entity getClosestEntity(String type, Entity e) {
        ArrayList<Entity> list;
        switch (type) {

            case "Animal":
                list = new ArrayList<>(animals);
                break;
            case "Building":
                list = new ArrayList<>(buildings);
                break;
            case "Herbivore":
                list = new ArrayList<>(herbivores);
                break;
            case "Carnivore":
                list = new ArrayList<>(carnivores);
                break;
            case "Person":
                list = new ArrayList<>(people);
                break;
            case "Poacher":
                list = new ArrayList<>(poachers);
                break;
            case "Lion":
                list = new ArrayList<>(lions);
                break;
            case "Wolf":
                list = new ArrayList<>(wolves);
                break;
            case "Buffalo":
                list = new ArrayList<>(buffalos);
                break;
            case "Giraffe":
                list = new ArrayList<>(giraffes);
                break;
            case "Pond":
                list = new ArrayList<>(ponds);
                list.removeIf(ent -> ((Pond)ent).IsOccupied());
                break;
            case  "Grass":
                list = new ArrayList<>(grasses);
                list.removeIf(ent -> ((Grass)ent).IsOccupied());
                break;
            case "PatrolFlag":
                list = new ArrayList<>(airShipPatrolFlags);
                break;
            case "AirShip":
                list = new ArrayList<>(airships);
                break;
            //TODO: Add remaining cases

            default:
                return null;

        }

        return list.stream().toList().stream().filter(ent -> ent != e ).min((a, b) -> {
            int dx = a.x - e.x;
            int dy = a.y - e.y;
            int d1 = dx * dx + dy * dy;
            dx = b.x - e.x;
            dy = b.y - e.y;
            int d2 = dx * dx + dy * dy;
            return d1 - d2;
        }).orElse(null);

    }


    public Entity getFurthestEntity( Entity e) {
        ArrayList<Entity> list = entities;


        return list.stream().toList().stream().filter(ent -> ent != e ).max((a, b) -> {
            int dx = a.x - e.x;
            int dy = a.y - e.y;
            int d1 = dx * dx + dy * dy;
            dx = b.x - e.x;
            dy = b.y - e.y;
            int d2 = dx * dx + dy * dy;
            return d1 - d2;
        }).orElse(null);

    }


    public Entity getClosestSelectedAnimalAndPoacher( Entity e) {
        ArrayList<Entity> list;
        ArrayList<Entity> list1;

        list = new ArrayList<>(animals);
        list1 = new ArrayList<>(poachers);

        list.addAll(list1);


        return list.stream().toList().stream().filter(ent -> ent != e ).filter(Entity::isSelected).filter(Entity::isVisible).min((a, b) -> {
            int dx = a.x - e.x;
            int dy = a.y - e.y;
            int d1 = dx * dx + dy * dy;
            dx = b.x - e.x;
            dy = b.y - e.y;
            int d2 = dx * dx + dy * dy;
            return d1 - d2;
        }).orElse(null);
        }

        public void markVisiblePoachers() {
            for(Entity entity : poachers) {
                entity.setInvisible();
            }
            for (Ranger ranger : rangers) {
                ranger.markVisible();
            }
            for (Camera camera : cameras) {
                camera.markVisible();
            }
            for (AirShip airship : airships) {
                airship.markVisible();
            }
            for (Drone drone : drones) {
                drone.markVisible();
            }
        }

    public void updatePoacherPursuitStatus(Poacher poacher) {
        boolean isUnderPursue = rangers.stream().anyMatch(ranger -> ranger.getTarget() == poacher);
        poacher.setUnderPersue(isUnderPursue);
    }

    public int getAnimalNum() {
        return animals.size();
    }

    public int getHerbivoreNum() {
        return herbivores.size();
    }

    public int getCarnivoreNum() {
        return carnivores.size();
    }

    public int getPersonNum() {
        return people.size();
    }

    public int getBuildingNum() {
        return buildings.size();
    }


    public boolean isBlocked(Entity ent, int x, int y) {
        int newx = ent.getX() + x;
        int newy = ent.getY() + y;

        // First check if the new position would be off-screen or in UI areas
        if (offScreen(newx, newy, ent.width, ent.height)) {
            return true;
        }

        // Then check for collisions with other entities
        Rectangle newPosition = new Rectangle(newx, newy, ent.width, ent.height);
        for (Entity e : entities) {
            if (e != ent && e.getHitBox().intersects(newPosition) && e.COLLIDABLE && ent.COLLIDABLE) {
                return true;
            }
        }

        // Check entitiesToAdd
        for (Entity e : entitiesToAdd) {
            if (e != ent && e.getHitBox().intersects(newPosition)) {
                return true;
            }
        }
        return false;
    }


    protected boolean offScreen(int x, int y, int width, int height) {
        // Check if entity is outside the screen boundaries
        boolean outsideHorizontalBounds = x < 0 || x + width > game.screenWidth;

        // Check if entity is in the stats panel area (top of screen)
        boolean inStatsPanelArea = y < STATS_PANEL_HEIGHT;

        // Check if entity is in the inventory panel area (bottom of screen)
        boolean inInventoryPanelArea = y  > game.screenHeight - INVENTORY_PANEL_HEIGHT - STATS_PANEL_HEIGHT;

        // Return true if entity is outside screen or in any UI panel area
        return outsideHorizontalBounds || inStatsPanelArea || inInventoryPanelArea;
    }


    public void paintComponents(Graphics2D g2d) {
        List<Entity> entitiesCopy;
        synchronized (entities) {
            entitiesCopy = new ArrayList<>(entities); // Create a copy
        }
        for (Entity entity : entitiesCopy) {
            entity.draw(g2d);
        }

        if (game.inventory.hasSelectedItem()) {
            drawGrid(g2d);
        }

        jeepHandler.drawVisitorCount(g2d);
    }

    public void drawGrid(Graphics2D g2) {
        g2.setColor(Color.lightGray);

        // Draw grid only in the playable area
        for (Point[] row : grid) {
            for (Point point : row) {
                // Only draw grid cells that are within the playable area
                if (point.y >= STATS_PANEL_HEIGHT &&
                    point.y + game.tileSize <= game.screenHeight - INVENTORY_PANEL_HEIGHT) {
                    g2.drawRect(point.x, point.y, game.tileSize, game.tileSize);
                }
            }
        }
    }

    public void update() {
        // Create a copy of the entities list to avoid ConcurrentModificationException
        List<Entity> entitiesCopy;
        synchronized (entities) {
            entitiesCopy = new ArrayList<>(entities);
        }

        // Update all entities using the copy
        for (Entity entity : entitiesCopy) {
            entity.update();
        }

        // Process pending additions and removals
        if (!entitiesToAdd.isEmpty()) {
            synchronized (entities) {
                entities.addAll(entitiesToAdd);
                entities.sort((a, b) -> {
                    if (a instanceof Road) {
                        return -1; // Road comes before Jeep
                    } else if (b instanceof Road) {
                        return 1; // Jeep comes after Road
                    }
                    return a.y - b.y; // Default sorting by y value
                });
                entitiesToAdd.clear(); // Clear the temporary list
            }
        }
        if (!entitiesToRemove.isEmpty()) {
            synchronized (entities) {
                entities.removeAll(entitiesToRemove);
                entitiesToRemove.clear(); // Clear the temporary list
            }
        }
        markVisiblePoachers();
    }


    public Animal getOldest(Animal animal) {
        return animals.stream()
                .filter(e -> e.getClass().equals(animal.getClass()))
                .max(Comparator.comparingInt(e -> e.age))
                .orElse(null);
    }

    public int getJeepNum() {
        return jeeps.size();
    }

    /**
     * Returns the grid of points representing the playable area
     * @return The grid of points
     */
    public Point[][] getGrid() {
        return grid;
    }

    /**
     * Returns the height of the stats panel
     * @return The height of the stats panel in pixels
     */
    public int getStatsPanelHeight() {
        return STATS_PANEL_HEIGHT;
    }

    /**
     * Returns the height of the inventory panel
     * @return The height of the inventory panel in pixels
     */
    public int getInventoryPanelHeight() {
        return INVENTORY_PANEL_HEIGHT;
    }

    /**
     * Returns the total height of both UI panels
     * @return The combined height of the stats and inventory panels in pixels
     */
    public int getTotalPanelsHeight() {
        return STATS_PANEL_HEIGHT + INVENTORY_PANEL_HEIGHT;
    }

    /**
     * Returns the height of the playable area
     * @return The height of the playable area in pixels
     */
    public int getPlayableHeight() {
        return game.screenHeight - STATS_PANEL_HEIGHT - INVENTORY_PANEL_HEIGHT;
    }
}
