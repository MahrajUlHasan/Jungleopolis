package entity;

import org.example.Direction;
import org.example.GameEngine;
import org.example.KeyHandler;
import org.example.MouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.*;

/**
 * IGNORE THIS CLASS FOR NOW
 */

public class DynamicEntity extends Entity {
    /**
     * Attributes
     */
    protected Image img;
    protected String pathingMode;
    protected DynamicEntity target;
    protected Boolean alive;

    protected GameEngine engine;
    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;
    protected int velocity;
    protected Direction direction;



    /// /////Change keyH with mouseH later
    public DynamicEntity(GameEngine engine, KeyHandler kH , MouseHandler mH) {
        this.engine = engine;
        keyHandler = kH;
        mouseHandler = mH;
        setDefaultValues();
        getSpriteImage();

    }

    /// //// GETTER SETTERS
    public Integer[] getCords() {
        return new Integer[]{x, y};
    }

    public Integer[] getDimentions() {
        return new Integer[]{width, height};
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        velocity = 4;
        direction = Direction.UP;
        width = engine.tileSize;
        height = engine.tileSize;
    }

    public String getPathingMode() {
        return pathingMode;
    }

    public DynamicEntity getTarget() {
        return target;
    }

    public void setPathingMode(String mode) {
        this.pathingMode = mode;
    }

    public void setPathingMode(String mode, DynamicEntity target) {
        this.pathingMode = mode;
        this.target = target;
    }

    public Boolean isAlive() {
        return alive;
    }



    /// /////// Methods

    public void draw(Graphics2D g2d) {
        g2d.fillRect(x, y, width, height);


        BufferedImage image = null;
        switch (direction) {
            case UP:
                if(spriteNum == 1) {
                    image = up1;
                }
                else if(spriteNum == 2) {
                    image = up2;
                }
                break;
            case DOWN:
                if(spriteNum == 1) {
                    image = down1;
                }
                else if(spriteNum == 2) {
                    image = down2;
                }
                break;
            case LEFT:
                if(spriteNum == 1) {
                    image = left1;
                }
                else if(spriteNum == 2) {
                    image = left2;
                }
                break;
            case RIGHT:
                if(spriteNum == 1) {
                    image = right1;
                }
                else if(spriteNum == 2) {
                    image = right2;
                }
                break;
        }

        g2d.drawImage(image, x, y, width, height, null);
        if (selected) {
            /// outine for selected sprite
            g2d.drawOval(x-width/2, y-height/2, width*2, height*2);
        }
    }

    public void update() {


        if (keyHandler.upKey) {
            direction = Direction.UP;
            y -= velocity;

        }
        if (keyHandler.downKey) {
            direction = Direction.DOWN;
            y += velocity;

        }
        if (keyHandler.rightKey) {
            direction = Direction.RIGHT;
            x += velocity;

        }
        if (keyHandler.leftKey) {
            direction = Direction.LEFT;
            x -= velocity;

        }

        spriteCounter++;
        if(spriteCounter > 10) {
            if(spriteNum == 1)
            {
                spriteNum = 2;
            }
            else if (spriteNum == 2)
            {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

    }



    public void getSpriteImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/Animal/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/Animal/up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/Animal/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/Animal/down2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/Animal/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/Animal/right2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/Animal/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/Animal/left2.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }

    public String getType(){ return "DynamicEntity"; }


    /**
     * checks if the sprite is overlapping with another sprite
     *
     * @param other , DynamicEntity to check collision with
     * @return true if it is overlapping
     */
    public Boolean collides(DynamicEntity other) {
        Rectangle otherRect = new Rectangle(other.x, other.y, other.width, other.height);
        if (this == other) {
            return false;
        }
        return new Rectangle(this.x, this.y, this.width, this.height).intersects(otherRect);
    }


}
