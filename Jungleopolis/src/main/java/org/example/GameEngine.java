package org.example;

import entity.Animal;
import entity.*;
import Gui.*;

import javax.swing.*;
import java.awt.*;

public class GameEngine extends JPanel implements Runnable {
    ///  Panel settings
    final public int tileUnit = 16; // 16x16 unit sized tiles
    final public int scale = 2; // scale the tiles are displayed
    final public int tileSize = tileUnit * scale; // actual size of tile 64x64 px
    final public int maxColNUm = 48; // number of tiles in the column
    final public int maxRowNUm = 26; // number of tiles in the row
    final public int screenWidth = maxColNUm * tileSize; //  1536 px
    final public int screenHeight = maxRowNUm * tileSize;//  832 px
    // updating the cash in real time once animal is bought
    private int cash = 10000; // Initial cash

    final public int FPS = 90;

    protected Thread gameThread; // PROCESS THREAD FOR THE GAME LOOP

    protected Boolean paused = false;

    protected KeyHandler keyHandler = new KeyHandler();

    protected MouseHandler mouseHandler = new MouseHandler(this);

    protected MapSpace map;

    protected boolean entitySelected = true ;
    protected Entity selectedEntity = null;

/// ////////////////
    protected GUI ui ;
    protected Sound sound;
    public int difficulty = 1;
    // these lines are being added for the inventory
    private Inventory inventory;



    //start time
    protected int passedTime = 0;
    private long startTime;
    private boolean gameStarted = false;

    /// ///////// INIT
    public GameEngine(GUI ui) {

        this.ui = ui;
        this.sound = new Sound();
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setFocusable(true);
        this.setBackground(Color.green);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler); // add the keyHandler
        this.setFocusable(true);
        playMusic(0);

        // inventory
        inventory = new Inventory();

    }

    // adding the functionality for the buy/sell button



    public void startGameThread() {
        gameThread = new Thread(this);
        map = new MapSpace(this);
        this.addMouseListener(mouseHandler);
        startTime = System.currentTimeMillis();
        gameStarted = true;



        /// Temporary
        for(int i = 0; i < 4; i++) {
            map.addEnt(new StaticEntity(this) {
            });
        }
        Animal player = new Lion(this ,map , keyHandler , mouseHandler);
        Animal player1 = new Lion(this ,map , keyHandler , mouseHandler);
        Building building = new Building(this);
        Building building1 = new Building(this);
//        building1.width= tileSize*5;
//        building1.height = tileSize*5;
        map.addEnt(building);
        map.addEnt(building1);
        map.addEnt(player);
        map.addEnt(player1);
        player.select();
        player1.select();
        building.select();
        building1.select();
        ///


        gameThread.start(); /// CALLS run() method . Keep it at the end
    }


    ///  Delta / Accumulator method (Google it)
    @Override
    public void run() {
        double interval = 1000.0 / (double) FPS;
        double delta = 0;
        double lastTime = System.currentTimeMillis();
        double currTime;
        double timer = 0;
        double drawCount = 0;

        while (gameThread != null || !paused) {
            currTime = System.currentTimeMillis();
            delta += (currTime - lastTime) / interval;
            timer += (currTime - lastTime);
            lastTime = currTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
                passedTime += 1; /// Adds a second to the time passed in-game
            }

            if (mouseHandler.mClicked) {
                handleMouseClick();
                mouseHandler.mClicked = false;
            }
        }
    }

    private void handleMouseClick() {
        int mouseX = mouseHandler.lastX;
        int mouseY = mouseHandler.lastY;

        // including buy/sell button too
        // please visit the Inventory.java file for the buy/sell button
        // i have shifted the button there, so that it can work with the inventory

        Entity clickedEntity = map.getEntityAt(mouseX, mouseY);
        inventory.handleClick(mouseX, mouseY, this);


        if (clickedEntity != null) {
            if (entitySelected && selectedEntity != null) {
                if (selectedEntity.equals(clickedEntity)) {
                    selectedEntity.deSelect();
                    selectedEntity = null;
                    entitySelected = false;
                    return;
                }
                selectedEntity.act(clickedEntity);
                selectedEntity.deSelect();
            }
            clickedEntity.select();
            selectedEntity = clickedEntity;
            entitySelected = true;
        } else {
            if (entitySelected && selectedEntity != null) {
                selectedEntity.deSelect();
                selectedEntity = null;
            }
            entitySelected = false;
        }
    }


    public void update() {
    map.update(); ///temporary
    }

    public void pause()
    {
        paused = true;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        /// temporary
//        player.draw(g2d);
        ui.draw(g2d);
        map.paintComponents(g2d);




        // draw inventory
        inventory.draw(g2d, this);

        // Draw buy/sell button


///
        g2d.dispose();
    }

    // add method to click on the buy/sell button
    //applied in the inventory,



    public String getTimePassed() {
        //Done, format: HH:MM:SS
        if (!gameStarted) return "00:00:00";

        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000; // this will convert to seconds.

        long hours = elapsedTime / 3600;
        long minutes = (elapsedTime % 3600) / 60;
        long seconds = elapsedTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public String getSpeedLevel() {
        //TODO
        return "hour";
    }
    public int getCash() {
        //Done
        return cash;
    }

    public void updateCash(int amount) {
        this.cash += amount;
    }

    public int getVisitors() {
        //TODO
        return 100;
    }
    public int getHerbivores() {
        //TODO
        return 100;
    }
    public int getCarnivores() {
        //TODO
        return 100;
    }

    public MapSpace getMap() {
        return map;
    }

    public void playMusic(int i){
        sound.setFile(i);
        sound.play();
        sound.loop();

    }
    public void stopMusic(){
        sound.stop();
    }


    //we create a placeselecteditems. we return true when successfull, and false the otherwise,


    public void placeSelectedItem(int mouseX, int mouseY, Item item) {
        if (item == null) {
            return;
        }

        DynamicEntity newEntity = null;
        switch (item) {
            case LION:
                newEntity = new Lion(this, map, keyHandler, mouseHandler);
                    newEntity.x = mouseX;
                    newEntity.y = mouseY;
                    if (!map.isBlocked(newEntity, 0, 0)) {
                        map.addEnt(newEntity);
                        inventory.removeItem(item); // when we place some item on the map, it should be removed from the inventory.
                    }
                break;
            // We will add more as we get the new images
        }


    }


}

