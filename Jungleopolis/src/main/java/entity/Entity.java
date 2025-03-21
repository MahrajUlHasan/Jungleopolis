package entity;

import org.example.Direction;
import org.example.MapSpace;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    public int x;
    public int y;
    public int width;
    public int height;
    public BufferedImage up1 , up2 , down1 , down2 ,right1 , right2 , left1 , left2 ;
    public Direction direction = Direction.UP;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public boolean selected= false;
    public int cost;
    public boolean COLLIDABLE = true;



    /// getter setters
    public Boolean isSelected() {
        return selected;
    }
    ///

    public void select()
    {
        selected = true;
    }
    public void deSelect()
    {
        selected = false;
    }



    public abstract void setDefaultValues();
    public abstract Rectangle getHitBox();

    public abstract void draw(Graphics2D g2d);

    public abstract void update();

    public abstract void act(Entity entity);

    public Boolean collides(Entity other) {
        Rectangle otherBox = other.getHitBox();
        if (this == other) {
            return false;
        }
        if(otherBox != null) {
            return this.getHitBox().intersects(otherBox);
        }
        return false;
    }


    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Point getCords() {
        return new Point(x , y);
    }

    public Dimension getDimension() {
        return new Dimension(x,y);
    }

    /**
     * returns the hit-box of the entity ususally used for collision detection and is rectangle
     * @return a rectangle representing the hit-box of the entity
     */
    protected Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }


    /**
     * adds it to the correct list / lists in the mapspace (ex: Lion will add itself to the carnivores ,entities and animals list)
     * @param mapSpace the map in which the entity will be added
     */
    public abstract void spawn(MapSpace mapSpace) ;



}
