package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

import java.awt.*;

public class Person extends DynamicEntity {

    public Person(GameEngine game , MapSpace m, KeyHandler keyH , MouseHandler mH) {
        super(game, m ,keyH, mH);
    }

    @Override
    public void setTarget() {

    }

    @Override
    public void act(Entity entity) {

    }

    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entities.add(this);
        mapSpace.people.add(this);
    }

}
