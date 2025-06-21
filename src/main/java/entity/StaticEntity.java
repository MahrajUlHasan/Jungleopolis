package entity;

import org.example.Direction;
import org.example.GameEngine;
import org.example.MapSpace;
import org.example.MouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public abstract class StaticEntity extends Entity {

    protected GameEngine engine;
    protected MouseHandler mouseH;
    protected String upPath1, upPath2, downPath1, downPath2, rightPath1, rightPath2, leftPath1, leftPath2;
    protected int capacity;
    protected int visitors = 0;
    protected boolean atCapacity = false;


    public StaticEntity(GameEngine engine) {
        this.engine = engine;
        setDefaultValues();
        upPath1 = "/Water/pond.png";
        upPath2 = "/Water/pond.png";
        downPath1 = "/Water/pond.png";
        downPath2 = "/Water/pond.png";
        rightPath1 = "/Water/pond.png";
        rightPath2 = "/Water/pond.png";
        leftPath1 = "/Water/pond.png";
        leftPath2 = "/Water/pond.png";
        getSpriteImage();
        setSprite(0, 0, engine.tileSize, engine.tileSize);

    }

    @Override
    public void setDefaultValues() {
        setRandPos();
        direction = Direction.UP;
        width = engine.tileSize;
        height = engine.tileSize;
        determineCapacity();
    }

    /**
     * sets the x and y coordinates of the entity to a random position on the map while not colliding with existing entities on the map
     */
    protected void setRandPos() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        do {
            x = rand.nextInt(engine.screenWidth - width);
            y = rand.nextInt(engine.screenHeight - height);
        } while (engine.getMap().isBlocked(this, 0, 0));
    }

    protected void determineCapacity() {
        int verticalCapacity = height/engine.tileSize;
        int horizontalCapacity = width/engine.tileSize;


        capacity = 2*(verticalCapacity + horizontalCapacity);
        //System.out.println("capacity: " + capacity);
    }

    protected boolean isAtCapacity() {
        if (visitors == capacity) {
            atCapacity = true;
        } else {
            atCapacity = false;
        }
        return atCapacity;
    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }

    public void getSpriteImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream(upPath1));
            up2 = ImageIO.read(getClass().getResourceAsStream(upPath2));
            down1 = ImageIO.read(getClass().getResourceAsStream(downPath1));
            down2 = ImageIO.read(getClass().getResourceAsStream(downPath2));
            right1 = ImageIO.read(getClass().getResourceAsStream(rightPath1));
            right2 = ImageIO.read(getClass().getResourceAsStream(rightPath2));
            left1 = ImageIO.read(getClass().getResourceAsStream(leftPath1));
            left2 = ImageIO.read(getClass().getResourceAsStream(leftPath2));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {

        BufferedImage image = null;
        switch (direction) {
            case UP:
                if (spriteNum == 1) {
                    image = up1;
                } else if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case DOWN:
                if (spriteNum == 1) {
                    image = down1;
                } else if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case LEFT:
                if (spriteNum == 1) {
                    image = left1;
                } else if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case RIGHT:
                if (spriteNum == 1) {
                    image = right1;
                } else if (spriteNum == 2) {
                    image = right2;
                }
                break;
        }

//        g2d.drawImage(image, x, y, width, height, null);
        g2d.drawImage(image, sprite.x, sprite.y, sprite.width, sprite.height, null);
//        g2d.drawRect(sprite.x, sprite.y, sprite.width, sprite.height);
        if (selected) {
            /// outline for selected sprite
            g2d.drawOval(x - width / 2, y - height / 2, width * 2, height * 2);
        }
    }


    @Override
    public void update() {

        spriteCounter++;
        if (spriteCounter > 30) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else if (spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        if (engine.canUpdateStats) {
            updateStats();
        }
        sprite.update();


    }

    protected abstract void updateStats();


    @Override
    public void spawn(MapSpace mapSpace) {
        mapSpace.entities.add(this);
    }

    public void setHeight(int w) {
        sprite.width = w;
        super.width = w;
        determineCapacity();

    }

    public void setWidth(int h) {
        sprite.height = h;
        super.height = h;
        determineCapacity();
    }
}
