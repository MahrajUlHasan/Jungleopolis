package org.example;

import entity.*;

import java.awt.*;
import java.util.ArrayList;

public class MapSpace {
    public ArrayList<Entity> entities;
    public ArrayList<DynamicEntity> dyentities; ///temporay
    public ArrayList<Animal> animals;

    public ArrayList<Building> buildings;

    protected GameEngine game;


    public MapSpace(GameEngine game) {
        entities = new ArrayList<>();
        dyentities = new ArrayList<>();
        animals = new ArrayList<>();
        game = game;
    }


    /// METHODS
    public void addEnt(DynamicEntity ent) {

        entities.add(ent);
        switch (ent.getType()) {
            case "Animal":
                animals.add((Animal) ent);
                break;
            case "DynamicEntity":
                dyentities.add((DynamicEntity) ent);
        }
    }

    /**
     * @param x mouse x position
     * @param y mouse y position
     * @return the first Entity that contains the point with cordinats (x,y)
     */
    public Entity getEntityAt(int x, int y) {
        return entities.stream().toList().stream().filter(e -> e.getHitBox().contains(x, y)).findFirst().orElse(null);
    }


    public void paintComponents(Graphics2D g2d)
    {
        entities.forEach(e -> e.draw(g2d));
    }

    public void update() {
        entities.forEach(e -> e.update());
    }



}
