package org.example ;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
/// ////////// Initial window setup
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Jungleopolis");
        GameEngine gamePanel = new GameEngine();
        gamePanel.startGameThread();

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}