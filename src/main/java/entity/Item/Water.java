package entity.Item;

import entity.Building;
import org.example.GameEngine;


public class Water extends Building{
    public Water(GameEngine engine) {
        super(engine);

        upPath1 = "/Water/water.png";
        upPath2 = "/Water/water.png";
//        downPath1 = "Greenery/tree1.png";
//        downPath2 = "Greenery/tree2.png";
//        rightPath1 = "Greenery/tree1.png";
//        rightPath2 = "Greenery/tree2.png";
//        leftPath1 = "Greenery/tree1.png";
//        leftPath2 = "Greenery/tree2.png";
        getSpriteImage();
    }
}
