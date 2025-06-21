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
    public Sprite sprite;
    protected boolean visible = false;
    
    // Getters for testing
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isCOLLIDABLE() { return COLLIDABLE; }






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

    public void setVisible() {
        visible = true;
    }
    public void setInvisible() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }


    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }

    public  void setSprite(int dx , int dy , int width , int height)
    {
        this.sprite = new Sprite(dx,dy,width,height);
    }

    public void setHitbox(int x , int y , int width , int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void setDefaultValues();

    public abstract Rectangle getHitBox();

    public abstract void draw(Graphics2D g2d);

    public abstract void update();

    /**
     * method to interact with other entities
     * * * USE OOP DESIGN PATTERNS (ex: Visitor Pattern) * *
     * @param entity target entity to interact with
     */
    public abstract void interact(Entity entity);

    /**
     * Override this method to add custom behavior when the entity has multiple hit-boxes
     * @param other other Entity to check collision with
     * @return boolean true if the two entities are colliding
     */
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
        return new Dimension(width,height);
    }

    /**
     * returns the hit-box of the entity ususally used for collision detection and is rectangle
     * @return a rectangle representing the hit-box of the entity
     */
    protected Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }

    public int distance(Entity e) {
        return (int) Math.sqrt(Math.pow(e.getX() - x, 2) + Math.pow(e.getY() - y, 2));
    }

    /**
     * adds it to the correct list / lists in the mapspace (ex: Lion will add itself to the carnivores ,entities and animals list)
     * @param mapSpace the map in which the entity will be added
     */
    public abstract void spawn(MapSpace mapSpace) ;

    public void setX(int lastX) {
        this.x = lastX;
    }

    public void setY(int lastY) {
        this.y = lastY;
    }

    protected class Sprite
    {
        public int x , y ,dx , dy, width , height;


        /**
         *
         * @param dx the x offset from hit-box (the x attribute of Entity)
         * @param dy the y offset from hit-box (the y attribute of Entity)
         * @param width the Width of the sprite (The Image to be specific)
         * @param height the Height of the sprite (The Image to be specific)
         */
        Sprite(int dx , int dy , int width , int height)
        {
            this.dx = dx;
            this.dy = dy;
            this.x = dx + Entity.this.x;
            this.y = dy + Entity.this.y;
            this.width = width;
            this.height = height;
        }

        public void update()
        {
            this.x = dx + Entity.this.x;
            this.y = dy + Entity.this.y;
            width = width;
            height = height;
        }
    }





}
