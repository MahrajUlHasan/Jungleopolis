package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public abstract class Carnivore extends Animal {

    public int attack;

    public Carnivore(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m, kH, mH);

    }

    @Override
    public void setTarget() {
        switch (status) {
            case "survive":
                ///Temporarily set to building
                target = map.getClosestEntity("Building", this);
                break;
            default:
                break;
        }

    }

    public void attack(Herbivore herb) {
        herb.attacked(this);

    }
}
