package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public abstract class Animal extends DynamicEntity {

    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;
    protected GameEngine gameEngine;

    protected int health;
    protected int maxHealth = 500;
    protected int thirst;
    protected int age;
    protected int hunger;


    public Animal(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {

        super(engine, m, kH, mH);


    }

    @Override
    public abstract void setTarget();

    @Override
    public void act(Entity entity) {

    }

    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entities.add(this);
        mapSpace.animals.add(this);
        mapSpace.dynamicEntities.add(this);
    }


}
