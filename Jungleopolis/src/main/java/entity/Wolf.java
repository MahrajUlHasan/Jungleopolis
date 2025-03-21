package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public class Wolf extends Carnivore{
    public Wolf(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m, kH, mH);
    }


}
