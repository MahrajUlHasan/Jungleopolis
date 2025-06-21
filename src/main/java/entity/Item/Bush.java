package entity.Item;

import entity.Building;
import entity.Entity;
import org.example.GameEngine;

public class Bush extends Building {

    public Bush(GameEngine engine) {
        super(engine);
        upPath1 = "/Greenery/bushes.png";
        upPath2 = "/Greenery/bushes.png";
//        downPath1 = "Greenery/tree1.png";
//        downPath2 = "Greenery/tree2.png";
//        rightPath1 = "Greenery/tree1.png";
//        rightPath2 = "Greenery/tree2.png";
//        leftPath1 = "Greenery/tree1.png";
//        leftPath2 = "Greenery/tree2.png";
        getSpriteImage();
    }

    @Override
    protected void updateStats() {

    }


    @Override
    public void interact(Entity entity) {
        super.interact(entity);

    }
}
