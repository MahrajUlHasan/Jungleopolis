package Gui;

import org.example.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI extends JFrame implements ActionListener {

    Font font = new Font("Arial", Font.PLAIN, 20);
    GameEngine gameEngine ;
    static Graphics2D g2;
    public int slotCol = 0;
    public int slotRow = 0;
    //////////////////////////////
    ImageIcon newGameIcon = new ImageIcon(getClass().getResource("/imagesGui/newGame.png"));
    ImageIcon settingsIcon = new ImageIcon(getClass().getResource("/imagesGui/setting.png"));
    ImageIcon exitIcon = new ImageIcon(getClass().getResource("/imagesGui/exit.png"));
    ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/imagesGui/background.png"));
    ImageIcon muteIcon = new ImageIcon(getClass().getResource("/imagesGui/mute.png"));
    ImageIcon unmuteIcon = new ImageIcon(getClass().getResource("/imagesGui/unmute.png"));

    // adding the toggle sound on/off functionality
    private JLabel soundButton;
    private boolean isMuted = false;
    //////////////////////////////

    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JLabel label3 = new JLabel();
    JLabel label4 = new JLabel();
    JLabel label5 = new JLabel();
    JLabel label6 = new JLabel();
    JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton newGameButton = new JButton("");
    JButton settingsButton = new JButton("");
    JButton exitButton = new JButton("");
    BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundIcon.getImage(), 0);
    private  JFrame mainFrame;
    ///////////////////////
    BackgroundPanel secondBackgroundPanel = new BackgroundPanel(backgroundIcon.getImage(), 0);
    JButton startGameButton = new JButton("");
    JButton goBackButton = new JButton("");
    JRadioButton easyButton = new JRadioButton("Easy");
    JRadioButton mediumButton = new JRadioButton("Medium");
    JRadioButton hardButton = new JRadioButton("Hard");
    ButtonGroup radioGroup = new ButtonGroup();
    JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label7 = new JLabel();
    JLabel label8 = new JLabel();
    JPanel panel2 = new JPanel(new GridBagLayout());
    JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JFrame frame = new JFrame();
    JTextField textField = new JTextField();


    private static final int SOUND_BUTTON_SIZE = 70; // Add this constant


    public GUI() {
//        addMouseListener(mouseHandler);
        gameEngine = new GameEngine(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(gameEngine.screenWidth , gameEngine.screenHeight); // Adjust frame size
        this.setResizable(false);
        this.add(initFirstPage());
        setVisible(true);

    }

    public  void draw(Graphics2D g2) {
        GUI.g2 = g2;  // Now the instance variable is set
//        drawInventory();
//        drawBuyandSellButton();

        g2.setFont(font);
        g2.setColor(Color.BLACK);
        g2.drawString("Time: " + gameEngine.getTimePassed(), 10, 20);
        g2.drawString("Speed Level: "+gameEngine.getSpeedLevel(), 10, 40);
        g2.drawString("Cash: "+gameEngine.getCash(), 10, 60);
        g2.drawString("Pause the Game", 10, 80);
        g2.drawString("Visitors: "+gameEngine.getVisitors(), 10, 100);
        g2.drawString("Herbivors: "+gameEngine.getHerbivores(), 10, 120);
        g2.drawString("Carnivors: "+gameEngine.getCarnivores(), 10, 140);


    }

//    public void drawInventory() {
//
//        //FRAME
//        int frameX = gameEngine.tileSize * 3;
//        int frameY = gameEngine.tileSize * 13;
//        int frameWidth = gameEngine.tileSize * 26;
//        int frameHeight = gameEngine.tileSize * 2;
//        drawSubWindow(frameX, frameY, frameWidth, frameHeight);
//
//        //SLOT
//        final int slotXstart = frameX+20;
//        final int slotYstart = frameY+20;
//        int slotX = slotXstart;
//        int slotY = slotYstart;
//
//
//
//        //CURSOR
//        int cursorX = slotXstart +(slotCol*gameEngine.tileSize);
//        int cursorY = slotYstart +(slotRow*gameEngine.tileSize);
//        int cursorWidth = gameEngine.tileSize;
//        int cursorHeight = gameEngine.tileSize;
//
//
//        //DRAW CURSOR
//        g2.setColor(Color.WHITE);
//        g2.setStroke(new BasicStroke(3));
//        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);
//    }

    public void drawBuyandSellButton (){
        int x = gameEngine.tileSize*29 +20 ;
        int y = gameEngine.tileSize * 13 ;
        int width = gameEngine.tileSize * 2;
        int height = gameEngine.tileSize * 2;

        drawSubWindow(x, y, width, height);

        // Draw text in the middle of the window
        String text = "Buy/Sell";
        FontMetrics metrics = g2.getFontMetrics(font);
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.setFont(font);
        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);

    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public JPanel initFirstPage(){
        // Setup buttons
        newGameButton.setText("New Game");
        newGameButton.setFocusable(false);
        newGameButton.setFont(new Font(null, Font.PLAIN, 25));
        newGameButton.addActionListener(this);

        settingsButton.setText("Settings");
        settingsButton.setFocusable(false);
        settingsButton.setFont(new Font(null, Font.PLAIN, 25));
        settingsButton.addActionListener(this);

        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.setFont(new Font(null, Font.PLAIN, 25));
        exitButton.addActionListener(this);

        // Setup images
        Image scaledImage = newGameIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        newGameIcon = new ImageIcon(scaledImage);

        Image scaledImage2 = settingsIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        settingsIcon = new ImageIcon(scaledImage2);

        Image scaledImage3 = exitIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        exitIcon = new ImageIcon(scaledImage3);



        label1.setIcon(newGameIcon);
        label2.setIcon(settingsIcon);
        label3.setIcon(exitIcon);
        label4.setText("Jungleopolis");
        label4.setFont(new Font(null, Font.PLAIN, 50));
        label4.setHorizontalAlignment(SwingConstants.CENTER);
        label4.setVerticalAlignment(SwingConstants.CENTER);
        label4.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); //move the label down
        label5.setIcon(unmuteIcon);
        label6.setIcon(muteIcon);



        //  these are not needed anymore, because i would like to create one button.
        // panel1.add(label5);
        // panel1.add(label6);

        soundButton = new JLabel();
        updateSoundButton();
        soundButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSound();
            }
        });
        panel1.add(soundButton);


        // Set GridBagLayout for the panel
        JPanel panel1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        // Configure the layout (padding, gaps, etc.)
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for each component

        // Add the first button (New Game)
        gbc.gridx = 0; // Column
        gbc.gridy = 0; // Row
        gbc.gridwidth = 1; // Span 1 column
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(newGameButton, gbc);

        // Add label1 (New Game Image)
        gbc.gridx = 1; // Column 1
        gbc.gridy = 0; // Row 0 (same as button1)
        gbc.gridwidth = 1; // Span 1 column
        panel1.add(label1, gbc);

        // Add the second button (Settings)
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1 (below button1)
        gbc.gridwidth = 1; // Span 1 column
        panel1.add(settingsButton, gbc);

        // Add label2 (Settings Image)
        gbc.gridx = 1; // Column 1
        gbc.gridy = 1; // Row 1 (same as button2)
        gbc.gridwidth = 1; // Span 1 column
        panel1.add(label2, gbc);

        // Add the third button (Exit)
        gbc.gridx = 0; // Column 0
        gbc.gridy = 2; // Row 2 (below button2)
        gbc.gridwidth = 1; // Span 1 column
        panel1.add(exitButton, gbc);

        // Add label3 (Exit Image)
        gbc.gridx = 1; // Column 1
        gbc.gridy = 2; // Row 2 (same as button3)
        gbc.gridwidth = 1; // Span 1 column
        panel1.add(label3, gbc);



        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setImage(backgroundIcon.getImage());
        backgroundPanel.add(panel1, BorderLayout.CENTER);
        backgroundPanel.add(this.panel1, BorderLayout.SOUTH);
        backgroundPanel.add(label4, BorderLayout.NORTH);

        return backgroundPanel;


    }

    public JPanel initSecondPage() {
        // Clear existing components
        panel2.removeAll();
        panel3.removeAll();
        radioPanel.removeAll();

        // Set text field
        // updated the below text to show the Enter the City name only as place holder instead of name itself, and validation too.
        textField.setPreferredSize(new Dimension(200, 40));
        textField.setFont(new Font("Consolas", Font.PLAIN, 15));
        textField.setText("Enter the City Name");
        textField.setForeground(Color.GRAY);

        // below will focus on the listener for the placeholder behaviour
        textField.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals("Enter the City Name")) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText("Enter the City Name");
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cityName = textField.getText().trim();
                if (cityName.isEmpty() || cityName.equals("Enter the City Name")) {
                    textField.setBackground(new Color(255, 200, 200)); // Light red background
                    return;
                }

                // Reset background color and continue with game start
                textField.setBackground(Color.WHITE);
                getContentPane().removeAll();
                setTitle(cityName);
                add(gameEngine);
                gameEngine.startGameThread();
                revalidate();
                repaint();
            }
        });

        // Set buttons
        startGameButton.setText("Start Game");
        startGameButton.setFocusable(false);
        startGameButton.setFont(new Font(null, Font.PLAIN, 25));
        startGameButton.addActionListener(this);

        goBackButton.setText("Go Back");
        goBackButton.setFocusable(false);
        goBackButton.setFont(new Font(null, Font.PLAIN, 25));
        goBackButton.addActionListener(this);

        // Set radio buttons
        easyButton.setFocusable(false);
        easyButton.addActionListener(this);
        easyButton.setSelected(true);
        mediumButton.setFocusable(false);
        mediumButton.addActionListener(this);
        hardButton.setFocusable(false);
        hardButton.addActionListener(this);

        // Create radio panel
        radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(new JLabel("Select Difficulty: "));
        radioPanel.add(easyButton);
        radioPanel.add(mediumButton);
        radioPanel.add(hardButton);

        // Reset radio group
        radioGroup.clearSelection();
        radioGroup.add(easyButton);
        radioGroup.add(mediumButton);
        radioGroup.add(hardButton);
        easyButton.setSelected(true);

        // Layout components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        panel2.add(textField, gbc);

        gbc.gridy = 1;
        panel2.add(radioPanel, gbc);

        gbc.gridy = 2;
        panel2.add(startGameButton, gbc);

        gbc.gridy = 3;
        panel2.add(new JLabel(" "), gbc);

        gbc.gridy = 4;
        panel2.add(goBackButton, gbc);

        // Set sound icons
        label7.setIcon(unmuteIcon);
        label8.setIcon(muteIcon);

        // removing these and add one instead
        // panel3.add(label7);
        // panel3.add(label8);

        // add this instead
        panel3.add(soundButton);


        // Set up background panel
        secondBackgroundPanel.removeAll();
        secondBackgroundPanel.setLayout(new BorderLayout());
        secondBackgroundPanel.setImage(backgroundIcon.getImage());
        secondBackgroundPanel.setPreferredSize(new Dimension(1536, 824));
        secondBackgroundPanel.add(panel2, BorderLayout.CENTER);
        secondBackgroundPanel.add(panel3, BorderLayout.SOUTH);

        return secondBackgroundPanel;
    }

//    private void updateSoundButton() {
//        try {
//            ImageIcon icon = new ImageIcon(getClass().getResource("/imagesGui/" +
//                    (isMuted ? "mute.png" : "unmute.png")));
//            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
//            soundButton.setIcon(new ImageIcon(img));
//        } catch (Exception e) {
//            soundButton.setText(isMuted ? "Off" : "On");
//        }
//    }


    // the sound button visibilty solution: by Ai

    private void updateSoundButton() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imagesGui/" +
                    (isMuted ? "mute.png" : "unmute.png")));
            Image img = icon.getImage().getScaledInstance(SOUND_BUTTON_SIZE, SOUND_BUTTON_SIZE, Image.SCALE_SMOOTH);
            soundButton.setIcon(new ImageIcon(img));
            soundButton.setPreferredSize(new Dimension(SOUND_BUTTON_SIZE, SOUND_BUTTON_SIZE));
        } catch (Exception e) {
            soundButton.setText(isMuted ? "Off" : "On");
        }
    }

    ////////////
    private void toggleSound() {
        isMuted = !isMuted;
        if (isMuted) {
            if (gameEngine != null) {
                gameEngine.stopMusic();
            }
        } else {
            if (gameEngine != null) {
                gameEngine.playMusic(0);
            }
        }
        updateSoundButton();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            //secondPage.fillFrame(mainFrame);
            this.getContentPane().removeAll();
            this.add(initSecondPage());
            this.revalidate();
            this.repaint();
        }
        if (e.getSource() == exitButton) {
            System.exit(0);
        }
        if (e.getSource() == settingsButton) {
            // Add settings page

        //////////////////////////////////////////
        }
        // we had a problem, i.e. we could start the game with out entering the city name
        // then in that case the city name was: Enter City Name, which was not valid.
        // so we added the validation for the city name, and also the placeholder for the text field.
        // I am removing the below lines as they are no longer needed, it's now handled in the button's actionlistener
//        if (e.getSource() == startGameButton) {
//            System.out.printf("Welcome " + textField.getText());
//            this.getContentPane().removeAll();
//            this.setTitle(textField.getText());
//            this.add(gameEngine);
//            gameEngine.startGameThread();
//            this.revalidate();
//            this.repaint();
//
//        }
        if (e.getSource() == easyButton) {
            System.out.println("Easy");
            gameEngine.difficulty = 1;
        }
        if (e.getSource() == mediumButton) {
            System.out.println("Medium");
            gameEngine.difficulty = 2;
        }
        if (e.getSource() == hardButton) {
            System.out.println("Hard");
            gameEngine.difficulty = 3;
        }
        if (e.getSource() == goBackButton) {
            this.getContentPane().removeAll();
            this.add(backgroundPanel);
            this.revalidate();
            this.repaint();
    }
}
}




