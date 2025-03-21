package org.example;

import entity.*;

import java.awt.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class  MapSpace {
    public ArrayList<Entity> entities;


    public ArrayList<DynamicEntity> dynamicEntities;
    /// temporary
    public ArrayList<Animal> animals;
    public ArrayList<Herbivore> herbivores;
    public ArrayList<Carnivore> carnivores;
    public ArrayList<Person> people;
    public ArrayList<Building> buildings;

    protected GameEngine game;

    protected int[][] grid ;


    public MapSpace(GameEngine game) {
        entities = new ArrayList<>();
        dynamicEntities = new ArrayList<>();
        animals = new ArrayList<>();
        buildings = new ArrayList<>();
        this.game = game;
        grid = new int[game.screenWidth][game.screenHeight];
    }


    /// METHODS
    public void addEnt(Entity ent) {

        entities.add(ent);
        ent.spawn(this);
        entities.sort((a, b) -> a.height - b.height);
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
                list =new ArrayList<>(animals) ;
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
            default:
                return null;

        }

        return list.stream().toList().stream().filter(ent -> ent != e).min((a, b) -> {
            int dx = a.x - e.x;
            int dy = a.y - e.y;
            int d1 = dx * dx + dy * dy;
            dx = b.x - e.x;
            dy = b.y - e.y;
            int d2 = dx * dx + dy * dy;
            return d1 - d2;
        }).orElse(null);

    }

    public int getAnimalNum()
    {
        return animals.size();
    }
    public int getHerbivoreNum()
    {
        return herbivores.size();
    }
    public int getCarnivoreNum()
    {
        return carnivores.size();
    }
    public int getPersonNum()
    {
        return people.size();
    }
    public int getBuildingNum()
    {
        return buildings.size();
    }


    public boolean isBlocked(DynamicEntity ent, int x, int y) {
        int newx = ent.getX()+x;
        int newy = ent.getY()+y;
        if(offScreen(ent.getX()+x , ent.getY()+y , ent.width , ent.height))
        {
            return true;
        }
        Rectangle newPosition = new Rectangle(newx, newy, ent.width, ent.height);
        for(Entity e : entities) {

            if(e != ent && e.getHitBox().intersects(newPosition)) {
                    return true;
            }
        }
        return false;
    }


    protected  boolean offScreen(int x , int y , int width , int height)
    {
        return x < 0 || y < 0 || x + width > game.screenWidth || y + height > game.screenHeight;
    }


    public void paintComponents(Graphics2D g2d) {
        entities.forEach(e -> e.draw(g2d));
    }

    public void update() {
        entities.forEach(e -> e.update());
    }


}
