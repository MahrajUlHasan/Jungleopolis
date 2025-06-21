package Gui;

import org.example.*;
import javax.swing.*;
import javax.swing.border.Border; // Add this import for Border class
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class GUI extends JFrame implements ActionListener {

    Font font = new Font("Arial", Font.PLAIN, 20);
    private Font titleFont;
    private Font buttonFont;

    GameEngine gameEngine;
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

    private JLabel soundButton;
    private static boolean isMuted = false;

    private float titleAlpha = 0.0f;
    private Timer animationTimer;
    private boolean titleFadingIn = true;
    private int titleY = 50;
    private final int TARGET_TITLE_Y = 80;

    private StyledButton newGameButton;
    private StyledButton settingsButton;
    private StyledButton exitButton;

    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JLabel label3 = new JLabel();
    JLabel label4 = new JLabel();
    JLabel label5 = new JLabel();
    JLabel label6 = new JLabel();
    JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    BackgroundPanel backgroundPanel;
    private JFrame mainFrame;
    ///////////////////////
    BackgroundPanel secondBackgroundPanel;
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

    private static final int SOUND_BUTTON_SIZE = 70;

    public GUI() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            titleFont = new Font("Arial", Font.BOLD, 60);
            buttonFont = new Font("Arial", Font.BOLD, 24);
        } catch (Exception e) {
            titleFont = new Font("Arial", Font.BOLD, 60);
            buttonFont = new Font("Arial", Font.BOLD, 24);
        }

        gameEngine = new GameEngine(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(gameEngine.screenWidth, gameEngine.screenHeight);

        // Allow resizing
        this.setResizable(true);

        // Set minimum size to prevent layout issues when shrinking
        this.setMinimumSize(new Dimension(1024, 768));

        BufferedImage enhancedBackground = createEnhancedBackground(gameEngine.screenWidth, gameEngine.screenHeight);
        backgroundPanel = new BackgroundPanel(enhancedBackground, 0);
        secondBackgroundPanel = new BackgroundPanel(enhancedBackground, 0);

        this.add(initFirstPage());
        setVisible(true);

        startTitleAnimation();

        // Add component listener to handle window resizing
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                // When the window is resized, ensure components adjust properly
                if (gameEngine != null && gameEngine.getInventory() != null) {
                    repaint(); // Force a repaint to update inventory position
                }
            }
        });
    }

    private BufferedImage createEnhancedBackground(int width, int height) {
        BufferedImage background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = background.createGraphics();

        GradientPaint gp = new GradientPaint(
            0, 0, new Color(32, 87, 13),
            0, height, new Color(85, 170, 85)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);

        try {
            Image bgImage = backgroundIcon.getImage();
            float alpha = 0.7f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2d.setComposite(ac);
            g2d.drawImage(bgImage, 0, 0, width, height, null);
        } catch (Exception e) {
            System.out.println("Couldn't overlay original background: " + e.getMessage());
        }

        g2d.dispose();
        return background;
    }

    private void startTitleAnimation() {
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (titleFadingIn) {
                    titleAlpha += 0.05f;
                    if (titleAlpha >= 1.0f) {
                        titleAlpha = 1.0f;
                        titleFadingIn = false;
                    }
                } else {
                    titleAlpha = 0.85f + (float)(Math.sin(System.currentTimeMillis() / 500.0) * 0.15f);
                }

                if (titleY < TARGET_TITLE_Y) {
                    titleY += 2;
                    if (titleY > TARGET_TITLE_Y) titleY = TARGET_TITLE_Y;
                }

                if (label4 != null) {
                    // Use a deep purple color that contrasts with green/white background
                    label4.setForeground(new Color(75, 0, 130, (int)(titleAlpha * 255))); // Deep purple (indigo)
                    label4.setBorder(BorderFactory.createEmptyBorder(titleY, 0, 0, 0));
                    label4.repaint();
                }
                backgroundPanel.repaint();
            }
        }, 0, 50);
    }

    public void draw(Graphics2D g2) {
        GUI.g2 = g2;
        
        // The game engine now handles its own stats display with enhanced styling
        // This method is kept for backward compatibility
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);

        for (int i = 3; i > 0; i--) {
            g2.setColor(new Color(0, 0, 0, 70 / i));
            g2.fillRoundRect(x + i, y + i, width, height, 35, 35);
        }

        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        GradientPaint gp = new GradientPaint(
            x, y, new Color(255, 255, 255, 40),
            x, y + height, new Color(255, 255, 255, 10)
        );
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        g2.setColor(new Color(255, 255, 255, 255));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public JPanel initFirstPage() {
        newGameButton = new StyledButton("New Game", new Color(76, 175, 80), new Color(56, 142, 60));
        settingsButton = new StyledButton("Settings", new Color(3, 155, 229), new Color(2, 119, 189));
        exitButton = new StyledButton("Exit", new Color(229, 57, 53), new Color(211, 47, 47));

        newGameButton.setFont(buttonFont);
        settingsButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        newGameButton.addActionListener(this);
        settingsButton.addActionListener(this);
        exitButton.addActionListener(this);

        Image scaledImage = newGameIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        newGameIcon = new ImageIcon(scaledImage);

        Image scaledImage2 = settingsIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        settingsIcon = new ImageIcon(scaledImage2);

        Image scaledImage3 = exitIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        exitIcon = new ImageIcon(scaledImage3);

        newGameButton.setIcon(newGameIcon);
        settingsButton.setIcon(settingsIcon);
        exitButton.setIcon(exitIcon);

        label4.setText("Jungleopolis");
        label4.setFont(titleFont);
        label4.setHorizontalAlignment(SwingConstants.CENTER);
        label4.setVerticalAlignment(SwingConstants.CENTER);
        
        // Change to a color that contrasts with green/white background
        label4.setForeground(new Color(75, 0, 130)); // Deep purple (indigo)
        
        // Add a drop shadow effect to title
        Border empty = BorderFactory.createEmptyBorder(titleY, 0, 0, 0);
        label4.setBorder(empty);

        soundButton = new JLabel();
        updateSoundButton();
        soundButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSound();
            }
        });

        JPanel soundButtonPanel = new JPanel();
        soundButtonPanel.setOpaque(false);
        soundButtonPanel.add(soundButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        buttonPanel.add(Box.createVerticalStrut(20));

        for (JButton button : new JButton[] { newGameButton, settingsButton, exitButton }) {
            JPanel row = new JPanel();
            row.setOpaque(false);
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.add(Box.createHorizontalGlue());
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            row.add(button);
            row.add(Box.createHorizontalGlue());
            buttonPanel.add(row);
            buttonPanel.add(Box.createVerticalStrut(20));
        }

        backgroundPanel.setLayout(new BorderLayout());

        backgroundPanel.add(Box.createVerticalStrut(40), BorderLayout.NORTH);
        backgroundPanel.add(Box.createHorizontalStrut(40), BorderLayout.WEST);
        backgroundPanel.add(Box.createHorizontalStrut(40), BorderLayout.EAST);

        backgroundPanel.add(label4, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);
        backgroundPanel.add(soundButtonPanel, BorderLayout.SOUTH);

        return backgroundPanel;
    }

    public JPanel initSecondPage() {
        // Clear existing components
        panel2.removeAll();
        panel3.removeAll();
        radioPanel.removeAll();

        // Create styled components for the second page
        StyledButton startGameBtn = new StyledButton("Start Game", new Color(76, 175, 80), new Color(56, 142, 60));
        StyledButton goBackBtn = new StyledButton("Go Back", new Color(229, 57, 53), new Color(211, 47, 47));
        
        startGameButton = startGameBtn;
        goBackButton = goBackBtn;
        
        startGameButton.setFont(buttonFont);
        goBackButton.setFont(buttonFont);
        
        startGameButton.addActionListener(this);
        goBackButton.addActionListener(this);

        // Set text field with enhanced styling
        textField.setPreferredSize(new Dimension(300, 50));
        textField.setFont(new Font("Consolas", Font.PLAIN, 20));
        textField.setText("Enter the City Name");
        textField.setForeground(Color.GRAY);
        textField.setBackground(new Color(240, 255, 240)); // Light green background
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60), 2, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // Add focus listener for placeholder behavior
        textField.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Don't clear the text when focus is gained
                // Just select all text to make it easier to replace
                textField.selectAll();
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText("Enter the City Name");
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        
        // Add document listener to track when user actually starts typing
        textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleTextChange();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleTextChange();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleTextChange();
            }
            
            private void handleTextChange() {
                // Only change color if text is not the placeholder
                if (!textField.getText().equals("Enter the City Name")) {
                    textField.setForeground(Color.BLACK);
                }
            }
        });
        
        // Add action listener for Enter key
        textField.addActionListener(e -> {
            // Only start game if text is not the placeholder
            if (!textField.getText().equals("Enter the City Name")) {
                startGame();
            }
        });

        // Style radio buttons with much more vibrant colors
        for (JRadioButton button : new JRadioButton[]{easyButton, mediumButton, hardButton}) {
            button.setFont(new Font("Arial", Font.BOLD, 16));
            // Use much brighter colors with better contrast against the background
            button.setForeground(new Color(255, 255, 120)); // Bright yellow
            
            // Add a dark, semi-transparent background to make text more visible
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setBackground(new Color(30, 50, 30, 180)); // Dark green with alpha
            
            // Add a border to make the buttons stand out more
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0, 100), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            
            button.setFocusPainted(false);
            button.addActionListener(this);
            
            // Add hover effect to radio buttons
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setForeground(new Color(255, 165, 0)); // Orange on hover
                    button.setBackground(new Color(40, 70, 40, 200)); // Slightly lighter background
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setForeground(new Color(255, 255, 120)); // Back to yellow
                    button.setBackground(new Color(30, 50, 30, 180)); // Back to original
                }
            });
        }
        
        // Set up radio button group - pre-select Easy difficulty
        radioGroup.clearSelection();
        radioGroup.add(easyButton);
        radioGroup.add(mediumButton);
        radioGroup.add(hardButton);
        easyButton.setSelected(true); // Pre-select Easy difficulty
        
        // Create styled radio panel with a semi-transparent background
        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font
        difficultyLabel.setForeground(new Color(255, 220, 90)); // Bright gold for label
        
        // Add a slight shadow effect for better readability
        difficultyLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        radioPanel.setOpaque(true);
        radioPanel.setBackground(new Color(0, 50, 0, 100)); // Semi-transparent dark green
        radioPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0, 80), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        radioPanel.add(difficultyLabel);
        radioPanel.add(easyButton);
        radioPanel.add(mediumButton);
        radioPanel.add(hardButton);

        // Create sound button panel
        soundButton = new JLabel();
        updateSoundButton();
        soundButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleSound();
            }
        });
        
        JPanel soundButtonPanel = new JPanel();
        soundButtonPanel.setOpaque(false);
        soundButtonPanel.add(soundButton);

        // Set up main panel with GridBagLayout for better control
        panel2 = new JPanel(new GridBagLayout());
        panel2.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel titleLabel = new JLabel("Create Your Jungle Paradise");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(75, 0, 130)); // Deep purple (indigo) - matching main title
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add glowing effect with border
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(titleLabel, gbc);

        // Add text field
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 15, 15, 15); // Extra space at top
        panel2.add(textField, gbc);

        // Add radio panel
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 15, 15, 15);
        panel2.add(radioPanel, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.add(startGameButton);
        buttonPanel.add(goBackButton);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 15, 15, 15);
        panel2.add(buttonPanel, gbc);

        // Set up background panel
        secondBackgroundPanel.removeAll();
        secondBackgroundPanel.setLayout(new BorderLayout());
        secondBackgroundPanel.add(panel2, BorderLayout.CENTER);
        secondBackgroundPanel.add(soundButtonPanel, BorderLayout.SOUTH);
        
        // Request focus on text field after panel is visible
        SwingUtilities.invokeLater(() -> {
            textField.requestFocusInWindow();
            textField.selectAll();
        });

        return secondBackgroundPanel;
    }

    private void startGame() {
        String cityName = textField.getText().trim();
        
        // Check if city name is provided
        if (cityName.isEmpty() || cityName.equals("Enter the City Name")) {
            textField.setBackground(new Color(255, 100, 100)); // Brighter red for more visibility
            JOptionPane.showMessageDialog(GUI.this, 
                    "Please enter a city name!", 
                    "Input Required", 
                    JOptionPane.WARNING_MESSAGE);
            
            // Reset the background after a short delay
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        textField.setBackground(new Color(240, 255, 240));
                        textField.requestFocusInWindow();
                    });
                }
            }, 500);
            return;
        }
        
        // No need to check if difficulty is selected since Easy is now pre-selected
        // Just use the selected radio button

        // Reset background color and continue with game start
        textField.setBackground(Color.WHITE);
        getContentPane().removeAll();
        setTitle(cityName);

        // Set the difficulty based on selection
        if (easyButton.isSelected()) {
            gameEngine.difficulty = 1;
        } else if (mediumButton.isSelected()) {
            gameEngine.difficulty = 2;
        } else if (hardButton.isSelected()) {
            gameEngine.difficulty = 3;
        }
        // Note: The game engine will behave the same regardless of difficulty for now

        add(gameEngine);
        gameEngine.startGameThread();
        
        // Stop the animation timer when game starts
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        
        revalidate();
        repaint();
    }

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

        if (gameEngine != null && gameEngine.getInventory() != null) {
            gameEngine.getInventory().syncSoundState(isMuted);
        }
    }

    public static boolean isSoundMuted() {
        return isMuted;
    }

    public static void setSoundMuted(boolean muted) {
        isMuted = muted;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
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
        }
        if (e.getSource() == easyButton) {
            gameEngine.difficulty = 1;
        }
        if (e.getSource() == mediumButton) {
            gameEngine.difficulty = 2;
        }
        if (e.getSource() == hardButton) {
            gameEngine.difficulty = 3;
        }
        if (e.getSource() == goBackButton) {
            this.getContentPane().removeAll();
            this.add(backgroundPanel);
            this.revalidate();
            this.repaint();
        }
        if (e.getSource() == startGameButton) {
            startGame();
        }
    }

    private class StyledButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public StyledButton(String text, Color baseColor, Color hoverColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(250, 60));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            GradientPaint gp = new GradientPaint(
                0, 0, isHovered ? hoverColor : baseColor,
                0, height, isHovered ? baseColor : hoverColor.darker()
            );
            g2d.setPaint(gp);

            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, 15, 15);
            g2d.fill(roundedRectangle);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height / 3);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.setColor(isHovered ? baseColor.darker() : baseColor);
            g2d.draw(roundedRectangle);

            if (isHovered) {
                for (int i = 1; i <= 3; i++) {
                    g2d.setColor(new Color(0, 0, 0, 50 / i));
                    g2d.drawRoundRect(i, i, width - (i * 2), height - (i * 2), 15, 15);
                }
            }

            g2d.dispose();

            super.paintComponent(g);
        }
    }
}




