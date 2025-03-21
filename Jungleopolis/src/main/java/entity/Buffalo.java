package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public class Buffalo extends Herbivore{
    public Buffalo(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m, kH, mH);
    }
}
