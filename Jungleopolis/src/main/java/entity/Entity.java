package entity;

import org.example.Direction;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    public int x;
    public int y;
    public int width;
    public int height;
    public BufferedImage up1 , up2 , down1 , down2 ,right1 , right2 , left1 , left2 ;
    public Direction direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public boolean selected= false;


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


    public abstract Rectangle getHitBox();

    public abstract String getType();

    public abstract void draw(Graphics2D g2d);

    public abstract void update();



}
