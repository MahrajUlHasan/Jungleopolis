package org.example;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener , MouseMotionListener {

    GameEngine game;
    public int lastX, lastY;
    public boolean mClicked = false  , mPressed = false, mReleased = false, mEntered = false  , mExited = false;

    public MouseHandler(GameEngine game) {
    this.game = game;
    game.addMouseListener(this);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            mClicked = true;
            lastX = e.getX();
            lastY = e.getY();
            System.out.println(e.getX() + " " + e.getY() + " " + e.getButton());
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            mPressed = true;
            lastX = e.getX();
            lastY = e.getY();
            System.out.println(e.getX() + " " + e.getY() + " " + e.getButton());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            mReleased = true;
            lastX = e.getX();
            lastY = e.getY();
            System.out.println(e.getX() + " " + e.getY() + " " + e.getButton());
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {


    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
