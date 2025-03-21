package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public class Herbivore extends Animal{
    private float defense;

    public Herbivore(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m, kH, mH);
    }

    @Override
    public void setTarget() {

    }

    public void attacked(Carnivore carnivore) {
        health -= (int) (carnivore.attack * defense);
        if (health <= 0) {
            alive = false;
        }
    }
}
