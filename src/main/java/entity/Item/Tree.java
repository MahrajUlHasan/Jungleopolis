package entity.Item;

import entity.Building;
import entity.Entity;
import org.example.GameEngine;

public class Tree extends Building {
    public Tree(GameEngine engine) {
        super(engine);
        upPath1 = "/Greenery/tree1.png";
        upPath2 = "/Greenery/tree2.png";
//        downPath1 = "Greenery/tree1.png";
//        downPath2 = "Greenery/tree2.png";
//        rightPath1 = "Greenery/tree1.png";
//        rightPath2 = "Greenery/tree2.png";
//        leftPath1 = "Greenery/tree1.png";
//        leftPath2 = "Greenery/tree2.png";
        getSpriteImage();
        setSprite(0,-10 ,engine.tileSize,engine.tileSize+10);

    }

    @Override
    protected void updateStats() {

    }


    @Override
    public void interact(Entity entity) {
        super.interact(entity);

    }
}
