package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MouseHandler;

public class Person extends DynamicEntity {

    public Person(GameEngine game , KeyHandler keyH , MouseHandler mH) {
        super(game, keyH, mH);
    }

}
