package entity.Item;

import entity.Building;
import org.example.GameEngine;
import org.example.MapSpace;

public class Road extends Building {

    public int row;
    public int col;

    public Road(GameEngine engine) {
        super(engine);
        upPath1 = "/Objects/Road.png";
        upPath2 = "/Objects/Road.png";
        getSpriteImage();
        COLLIDABLE = false;
    }

    @Override
    public void setDefaultValues() {
        super.setDefaultValues();

    }

    @Override
    public Boolean IsOccupied() {
        return engine.getMap().jeepHandler.isOccupied(row, col);
    }


    @Override
    public void spawn(MapSpace mapSpace) {
            super.spawn(mapSpace);
            mapSpace.roads.add(this);
            col = (int)Math.floor((float)y/(float)engine.tileSize);
            row = (int)Math.floor((float)x/(float)engine.tileSize);
            mapSpace.jeepHandler.addRoad(this);

    }




}
