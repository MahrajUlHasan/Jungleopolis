package org.example;
import java.awt.*;
import java.lang.*;

/**
 * IGNORE THIS CLASS FOR NOW
 */

public class Sprite {
    /**
     * Attributes
     */
    protected Image img;
    protected Integer x;
    protected Integer y;
    protected Integer width;
    protected Integer height;
    protected String pathingMode;
    protected Sprite target;
    protected Boolean alive;

    /**
     * init
     * @param x , x-axis coordinent
     * @param y , y-axis coordinent
     */
    Sprite(Integer x, Integer y ,Integer width, Integer height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;


    }
    /////// GETTER SETTERS
    public Integer[] getCords()
    {
        return new Integer[]{x, y};
    }
    public String getPathingMode()
    {
        return pathingMode;
    }
    public Sprite getTarget()

    {
        return target;
    }

    public void setPathingMode(String mode)
    {
        this.pathingMode = mode;
    }

    public void setPathingMode(String mode , Sprite target){
        this.pathingMode = mode;
        this.target = target;
    }

    public Boolean isAlive()
    {
        return alive;
    }

    /// ///////Methods

    public void draw(Graphics g)
    {
//        g.drawImage(img, x, y, width, height, null);
            g.drawRect(x, y, width, height);
    }



    /**
     * checks if the sprite is overlapping with another sprite
     * @param other , Sprite to check collision with
     * @return  true if it is overlapping
     */
    public  Boolean collides(Sprite other){
        Rectangle otherRect = new Rectangle(other.x, other.y, other.width, other.height);
        if (this == other) {
            return false;
        }
        return new Rectangle(this.x , this.y , this.width, this.height).intersects(otherRect);
    };

    public void update()
    {


    }


    /// temporary method
    public void move(int x , int y)
    {
        this.x = x;
        this.y = y;
    }

}
