package entity;

import org.example.GameEngine;
import org.example.MapSpace;

import java.util.ArrayList;

public abstract class Building extends StaticEntity {

    protected ArrayList<DynamicEntity> occupants = new ArrayList<>();
    protected int  max_occupants = 4;
    protected Boolean isOccupied = false;


    public Building(GameEngine engine) {
        super(engine);
        setSprite(0,0,engine.tileSize,engine.tileSize);

    }
    public void  interact(Entity entity)
    {
        addOccupant((DynamicEntity) entity);
        updateOccupants();
    }

    @Override
    protected void updateStats() {
//        updateOccupants();
    }

    protected void updateOccupants() {
        if(occupants.isEmpty())return;
        occupants.removeIf(ent -> !isNearby(ent));
    }

    private boolean isNearby(DynamicEntity ent) {
       if(distance(ent) < engine.tileSize*3)
       {
           return true;
       }
       return false;
    }

    public void  addOccupant(DynamicEntity ent)
    {
        if (occupants.contains(ent) || isOccupied)return;
        else occupants.add(ent);
        if(occupants.size() >= max_occupants)
        {
            isOccupied = true;
        }
    }


    public Boolean IsOccupied()
    {
        return occupants.size()== max_occupants;
    }

    /**
     * adds it to the correct list / lists in the mapspace (ex: Lion will add itself to the carnivores ,entities and animals list)
     * snaps to the closest grid position using tileSize
     * @param mapSpace the map in which the entity will be added
     */
    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entitiesToAdd.add(this);
        mapSpace.buildings.add(this);
    }

    @Override
    public void  setX(int x){
        this.x = (int)Math.floor((float)x/(float)engine.tileSize) * engine.tileSize;
    }
    @Override
    public void  setY(int y){
        this.y = (int)Math.floor((float)y/(float)engine.tileSize) * engine.tileSize;
    }

}
