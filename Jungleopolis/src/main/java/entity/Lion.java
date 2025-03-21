package entity;

import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MapSpace;
import org.example.MouseHandler;

public class Lion extends Carnivore{
    public Lion(GameEngine engine, MapSpace m, KeyHandler kH, MouseHandler mH) {
        super(engine, m, kH, mH);
        this.cost = 200;
        this.health = 100;
//        this.attack = 10;
        this.velocity = 1;
        this.upPath1 = "/Animal/lion/up1.png";
        this.upPath2 = "/Animal/lion/up2.png";
        this.downPath1 = "/Animal/lion/down1.png";
        this.downPath2 = "/Animal/lion/down2.png";
        this.rightPath1 = "/Animal/lion/right1.png";
        this.rightPath2 = "/Animal/lion/right2.png";
        this.leftPath1 = "/Animal/lion/left1.png";
        this.leftPath2 = "/Animal/lion/left2.png";


    }
}
