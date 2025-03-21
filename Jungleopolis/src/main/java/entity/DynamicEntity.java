package entity;

import org.example.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.*;
import java.util.Random;


public abstract class DynamicEntity extends Entity {
    protected String status = "survive";
    protected Entity target;
    protected Boolean alive;

    protected GameEngine engine;
    protected MapSpace map;
    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;
    protected int velocity;
    public Direction direction;
    public Direction obstacleDirection;
    protected String upPath1 , upPath2 , downPath1 , downPath2 , rightPath1 , rightPath2 , leftPath1 , leftPath2;

    /**
     * Constructor for DynamicEntity
     * @param engine
     * @param map
     * @param kH
     * @param mH
     */
    public DynamicEntity(GameEngine engine, MapSpace map, KeyHandler kH, MouseHandler mH) {
        this.engine = engine;
        this.map = map;
        keyHandler = kH;
        mouseHandler = mH;
        setDefaultValues();
        getSpriteImage();

    }

    /// //// GETTER SETTERS

    @Override
    public void setDefaultValues() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        x = rand.nextInt(engine.screenWidth - width);
        y = rand.nextInt(engine.screenHeight - height);
        velocity = 1;
        direction = Direction.UP;
        width = engine.tileSize;
        height = engine.tileSize;
        upPath1 = "/Animal/lion/up1.png";
        upPath2 = "/Animal/lion/up2.png";
        downPath1 = "/Animal/lion/down1.png";
        downPath2 = "/Animal/lion/down2.png";
        rightPath1 = "/Animal/lion/right1.png";
        rightPath2 = "/Animal/lion/right2.png";
        leftPath1 = "/Animal/lion/left1.png";
        leftPath2 = "/Animal/lion/left2.png";

    }

    public String getStatus() {
        return status;
    }

    public Entity getTarget() {
        return target;
    }

    public void setStatus(String mode) {
        this.status = mode;
    }

    public void setStatus(String mode, Entity target) {
        this.status = mode;
        this.target = target;
    }

    public Boolean isAlive() {
        return alive;
    }


    /// /////// Methods

    public void draw(Graphics2D g2d) {
//        g2d.fillRect(x, y, width, height);


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
        g2d.drawImage(image, x, y, width, height, null);
        if (selected) {
            /// outline for selected sprite
            g2d.drawOval(x - width / 2, y - height / 2, width * 2, height * 2);
        }
    }

    @Override
    public void update() {
        if (target == null) {
            setTarget();
            if (target == null) {
                return;
            }
        }
        move();
//        System.out.println("moving");
        updateSprite();

    }

    protected void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }


//    protected void move() {
//        if (target == null) {
//            setTarget();
//            if (target == null) {
//                return;
//            }
//        }
//
//        int dx = xDelta(target);
//        int dy = yDelta(target);
//        int newx = x + dx * velocity;
//        int newy = y + dy * velocity;
//
//        if (this.collides(target) || target.getHitbox().intersects(new Rectangle(newx, newy, width, height))) {
//            if (target != null) target = null;
//            return;
//        }
//
//        changeDirection();
//
//        if (map.isBlocked(this, dx * velocity, dy * velocity)) {
//            if (!map.isBlocked(this, dx * velocity, 0)) {
//                direction = dx > 0 ? Direction.RIGHT : Direction.LEFT;
//                x += dx * velocity;
//            } else if (!map.isBlocked(this, 0, dy * velocity)) {
//                direction = dy <0 ? Direction.UP : Direction.DOWN;
//                y += dy * velocity;
//            }
//            else {
//                while(isBlocked())
//                {
//                    rotate();
//                }
//                moveInDirection();
//            }
//        } else {
//            x += Math.abs(newx - target.getX()) > velocity ? dx * velocity : target.getX() - x;
//            y += Math.abs(newy - target.getY()) > velocity ? dy * velocity : target.getY() - y;
//        }
//    }

    public void move() {
        if (target == null ) setTarget(); if(target == null) { return;}

        int dx = xDelta(target);
        int dy = yDelta(target);
        int Dx = dx*velocity;
        int Dy = dy*velocity;
        int newX = x + Dx;
        int newY = y + Dy;

        if (this.collides(target) || target.getHitBox().intersects(new Rectangle(newX, newY, width, height))) {
            if (target != null) target = null;
            return;
        }

        changeDirection();
        if (map.isBlocked(this , Dx , Dy)) {
            if(!map.isBlocked(this , Dx , 0)) {
                direction= dx > 0 ? Direction.RIGHT : Direction.LEFT;
                x= newX;
            }
            else if(!map.isBlocked(this , 0 ,Dy)) {
                direction = dy > 0 ? Direction.DOWN : Direction.UP;
                y = newY;
            }
            else {
                while(isBlocked()) {
                    rotate();
                }
                moveInDirection();
            }

        }
        else { x = newX; y = newY;}

    }

    protected void moveInDirection() {
        int dx = 0;
        int dy = 0;
        switch (direction) {
            case UP:
                dy = -1;
                break;
            case DOWN:
                dy = 1;
                break;
            case LEFT:
                dx = -1;
                break;
            case RIGHT:
                dx = 1;
                break;
        }
        int newx = x + dx * velocity;
        int newy = y + dy * velocity;
        x += Math.abs(newx - target.getX()) > velocity ? dx * velocity : target.getX() - x;
        y += Math.abs(newy - target.getY()) > velocity ? dy * velocity : target.getY() - y;

    }

    protected boolean isBlocked() {
        switch (direction) {
            case UP:
                return map.isBlocked(this, 0, -velocity);
            case DOWN:
                return map.isBlocked(this, 0, velocity);
            case LEFT:
                return map.isBlocked(this, -velocity, 0);
            case RIGHT:
                return map.isBlocked(this, velocity, 0);
            default:
                return false;
        }
    }

    protected void rotate() {
        this.direction = Direction.values()[(direction.ordinal() + 1) % Direction.values().length];
    }


    protected void changeDirection() {
        int dx = target.getX() - x;
        int dy = target.getY() - y;
        if (dx == 0 && dy == 0) {
            return;
        }
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                direction = Direction.RIGHT;
            } else {
                direction = Direction.LEFT;
            }
        } else {
            if (dy > 0) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.UP;
            }
        }
    }


    protected int xDelta(Entity e) {
        if (e.x - x == 0) {
            return 0;
        }
        return e.x - x > 0 ? 1 : -1;
    }

    protected int yDelta(Entity e) {
        if (e.y - y == 0) {
            return 0;
        }
        return e.y - y > 0 ? 1 : -1;
    }


    public abstract void setTarget();

    @Override
    public abstract void act(Entity entity);


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
    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }


}
