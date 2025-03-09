package org.example;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upKey = false;
    public boolean leftKey = false;
    public boolean rightKey = false;
    public boolean downKey = false;


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP) {
            upKey = true;
        }
        if (code == KeyEvent.VK_DOWN) {
            downKey = true;
            System.out.println("pressed down key");///temporay
        }
        if (code == KeyEvent.VK_LEFT) {
            leftKey = true;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightKey = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP) {
            upKey = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            downKey = false;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftKey = false;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightKey = false;
        }


    }
}
