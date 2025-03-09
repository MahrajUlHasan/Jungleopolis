package org.example;

import entity.DynamicEntity;
import entity.Entity;

import javax.swing.*;
import java.awt.*;

public class GameEngine extends JPanel implements Runnable {
    ///  Screen settings
    final public int tileUnit = 16; // 16x16 unit sized tiles
    final public int scale = 3; // scale the tiles are displayed
    final public int tileSize = tileUnit * scale; // actual size of tile 64x64 px
    final public int maxColNUm = 48; // number of tiles in the column
    final public int maxRowNUm = 26; // number of tiles in the row
    final public int screenWidth = maxColNUm * tileSize; //  1536 px
    final public int screenHeight = maxRowNUm * tileSize;//  824 px

    final public int FPS = 60;

    protected Thread gameThread; // PROCESS THREAD FOR THE GAME LOOP

    protected Boolean paused = false;

    protected KeyHandler keyHandler = new KeyHandler();

    protected MouseHandler mouseHandler = new MouseHandler(this);

    protected MapSpace map;

    ///    Temporary player DynamicEntity
    DynamicEntity player = new DynamicEntity(this, keyHandler, mouseHandler);
    int px = 100;
    int py = 100;

    ///

    protected boolean entitySelected = true;
    protected Entity selectedEntity = player;


    /// ///////// INIT
    public GameEngine() {

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler); // add the keyHandler
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start(); /// CALLS run() method
        map = new MapSpace(this);
        /// Temporary
        map.addEnt(player);
        ///

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


        while (gameThread != null) {

            currTime = System.currentTimeMillis();
            delta += (currTime - lastTime) / interval;
            timer += (currTime - lastTime);
            lastTime = currTime;
            if (delta >= 1) {

                if (!paused) {
                    update();
                }
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }

            /// temporary mouse entity selection
            if (mouseHandler.mClicked) {
                selectedEntity = map.getEntityAt(mouseHandler.lastX, mouseHandler.lastY);
                if (selectedEntity != null) {
                    entitySelected = true;
                    selectedEntity.select();
                }

            }
            ///


        }
    }

    /// /// different gameLoop logic
//    @Override
//    public void run() {
//        while (gameThread != null || !paused) {
//
//            long currTime = System.currentTimeMillis(); // System time in milliseconds
//
//            double interval = (double) 1000 / (double) FPS; // interval between two frames in milliseconds
//
//            double nextDrawTime = currTime + interval; // which system time the next frame should be drawn
//
//            update(); // UPDATES GAME STATE  ex: Character position
//
//            repaint(); // Redraws the new updated components on the screen
//
//
//            try {
//                double timeRemaining = nextDrawTime - currTime; /// time remaining between current time and the next draw time
//                if (timeRemaining < 0) {
//                    timeRemaining = 0;
//                }
//
//                Thread.sleep((long) timeRemaining); // puts the thread to sleep until next thread
//                nextDrawTime += interval;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//
//        }
//    }
    public void update() {
        map.update(); ///temporary
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        /// temporary
        g2d.fillRect(px, py, tileSize, tileSize);
//        player.draw(g2d);
        map.paintComponents(g2d);

        ///
        g2d.dispose();

    }
}
