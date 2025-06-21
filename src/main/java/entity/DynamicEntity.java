package entity;

import org.example.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import entity.util.GhostEntity;



public abstract class DynamicEntity extends Entity {
    protected Entity target;
    protected Boolean alive = true;
    protected int hp;
    protected int age;
    protected int maxHp;
    protected int maxAge;
    protected int cost;

    protected GameEngine engine;
    protected MapSpace map;
    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;
    protected int velocity;
    public Direction direction;
    protected Status status = Status.HEARD;
    public Direction obstacleDirection;
    protected  String upPath1 , upPath2 , downPath1 , downPath2 , rightPath1 , rightPath2 , leftPath1 , leftPath2;
    int maxRotations = 4; // Limit to 4 directions
    int rotations = 0;
    int maxAttempts = 10; // Limit the number of attempts to resolve blockage
    int attempts = 0;


    public DynamicEntity(GameEngine engine, MapSpace map) {
        this.engine = engine;
        this.map = map;
        setDefaultValues();
        upPath1 = "/Animal/up1.png";
        upPath2 = "/Animal/up2.png";
        downPath1 = "/Animal/down1.png";
        downPath2 = "/Animal/down2.png";
        rightPath1 = "/Animal/right1.png";
        rightPath2 = "/Animal/right2.png";
        leftPath1 = "/Animal/left1.png";
        leftPath2 = "/Animal/left2.png";
        getSpriteImage();

    }



    public void setDefaultValues() {
        setRandPos();
        velocity = 1;
        direction = Direction.UP;
        width = engine.tileSize;
        height = engine.tileSize;

    }

    protected void setRandPos() {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        x = rand.nextInt(engine.screenWidth - width);
        y = rand.nextInt(engine.screenHeight - height);
        while (map.isBlocked(this, 0, 0)) {
            x = rand.nextInt(engine.screenWidth - width);
            y = rand.nextInt(engine.screenHeight - height);
        }
    }


    public Enum getStatus() {
        return status;
    }

    public Entity getTarget() {
        return target;
    }

    public void setStatus(Status mode) {
        this.status = mode;
    }

    public void setStatus(Status mode, Entity target) {
        this.status = mode;
        this.target = target;
    }

    public Boolean isAlive() {
        return alive;
    }


    public void draw(Graphics2D g2d) {
//        g2d.setColor(Color.WHITE);
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
        // Check if target is still valid (might have been removed from the game)
        if (target != null && !map.entities.contains(target)) {
            target = null;
        }

        // Set a new target if needed
        if (target == null) {
            setTarget();
            // Reset attempts and rotations when setting a new target
            attempts = 0;
            rotations = 0;
        }

        move();
        if(engine.canUpdateStats) {
            updateStats();
        }
        updateSprite();
    }


    protected abstract void updateStats();

    protected void updateSprite() {
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    public void move() {
        if (target == null) setTarget();
        if (target == null) {
            return;
        }

        if (collidesOrIntersects(target) && !(target instanceof GhostEntity) ) {
            if(engine.canUpdateStats) {
                this.interact(target);
            }
            target = null;
            return;
        }


        int dx = xDelta(target);
        int dy = yDelta(target);
        int Dx = dx * velocity;
        int Dy = dy * velocity;
        int newX = x + Dx;
        int newY = y + Dy;

        if (map.isBlocked(this, Dx, Dy)) {
            handleBlockedMovement(Dx, Dy, newX, newY);
        } else {
            // Reset attempts and rotations when successfully moving
            attempts = 0;
            rotations = 0;
            changeDirection();
            x = newX;
            y = newY;
        }
    }

    protected boolean collidesOrIntersects(Entity target) {
        int dx = xDelta(target);
        int dy = yDelta(target);
        int newX = x + dx * velocity;
        int newY = y + dy * velocity;
        return this.collides(target) || target.getHitBox().intersects(new Rectangle(newX, newY, width, height));
    }

    private void handleBlockedMovement(int Dx, int Dy, int newX, int newY) {
        // Reset attempts and rotations at the beginning of handling blocked movement
        attempts = 0;
        rotations = 0;

        if (!map.isBlocked(this, 0, Dy) && !map.isBlocked(this, Dx, 0)) {
            handleDiagonalObstruction(Dx, Dy);
        } else if (!map.isBlocked(this, Dx, 0)) {
            handleHorizontalMovement(Dx, Dy);
        } else if (!map.isBlocked(this, 0, Dy)) {
            handleVerticalMovement(Dx, Dy);
        } else {
            while (isBlocked() && attempts < maxAttempts) {
                rotate();
                attempts++;
            }
            if (attempts >= maxAttempts) {
                // Stop trying to move and reset the target if all directions are blocked
                target = null;
                // Reset counters for next time
                attempts = 0;
                rotations = 0;
            } else {
                moveInDirection();
            }
        }
    }

    private void handleDiagonalObstruction(int Dx, int Dy) {
        int xDis = Math.abs(target.getCenter().x - this.getCenter().x);
        int yDis = Math.abs(target.getCenter().y - this.getCenter().y);
        if (xDis <= yDis) {
            if (Dy > 0) {
                handleHorizontalMovement(Dx, Dy);
            } else {
                handleVerticalMovement(Dx, Dy);
            }
        } else {
            if (Dx > 0) {
                handleVerticalMovement(Dx, Dy);
            } else {
                handleHorizontalMovement(Dx, Dy);
            }

        }
    }

    private void handleHorizontalMovement(int Dx, int Dy) {
        if (inDiag()) {

            if (Dy < 0) {
                this.direction = Direction.RIGHT;
            } else {
                this.direction = Direction.LEFT;
            }

        } else if (inCol()) {
            this.direction = Dy < 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (inRow()) {
            this.direction = Dx >= 0 ? Direction.RIGHT : Direction.LEFT;
        }
        moveInDirection();
    }


    private void handleVerticalMovement(int Dx, int Dy) {
        if (inDiag()) {

            if (Dx < 0) {

                this.direction = Direction.UP;
            } else {
                this.direction = Direction.DOWN;
            }

        } else if (inCol()) {
            this.direction = Dy >= 0 ? Direction.DOWN : Direction.UP;
        } else if (inRow()) {
            this.direction = Dx <= 0 ? Direction.UP : Direction.DOWN;
        }

        moveInDirection();
    }

    protected int tDisX() {
        return Math.abs(target.getCenter().x - getCenter().x);
    }

    protected int tDisY() {
        return Math.abs(target.getCenter().y - getCenter().y);
    }


    protected Boolean inCol() {
        return Math.abs(getCenter().x - target.getCenter().x) < (target.getDimension().width + width) / 2;
    }

    protected Boolean inRow() {
        return Math.abs(getCenter().y - target.getCenter().y) < (target.getDimension().getHeight() + height) / 2;
    }

    protected Boolean inDiag() {
        return !inCol() && !inRow();
    }

    protected void moveInDirection() {
        if (target == null) {
            return; // Exit if target is null
        }

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

        // Ensure target is not null before accessing its properties
        if (target != null) {
            x += Math.abs(newx - target.getX()) > velocity ? dx * velocity : (target.getX() - x == 0 ? dx : target.getX() - x);
            y += Math.abs(newy - target.getY()) > velocity ? dy * velocity : (target.getY() - y == 0 ? dy : target.getY() - y);
        }

    }

    /**
     * @return Boolean true if the entity is blocked in the current direction
     */
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
        while (isBlocked() && rotations < maxRotations) {
            this.direction = Direction.values()[(direction.ordinal() + 1) % Direction.values().length];
            rotations++;
        }

        if (rotations >= maxRotations) {
            // If all directions are blocked, stop trying to move
            target = null;
            // Reset rotations for next time
            rotations = 0;
        }    }


    /**
     * Changes the direction to face the target
     */
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
        int centerX = e.x + (e.getDimension().width / 2);

        if (centerX - x == 0) {
            return 0;
        }
        return centerX - x > 0 ? 1 : -1;
    }

    protected int yDelta(Entity e) {
        int centerY = e.y + e.getDimension().height / 2;
        if (centerY - y == 0) {
            return 0;
        }
        return centerY - y > 0 ? 1 : -1;
    }


    public abstract void setTarget();

    @Override
    public abstract void interact(Entity entity);


    protected void getSpriteImage() {
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
