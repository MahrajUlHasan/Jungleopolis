package Gui;

import org.example.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatsPanel extends JPanel {
    private GameEngine gameEngine;
    private JLabel cashLabel;
    private JLabel timeLabel;
    private JLabel difficultyLabel; // New label for displaying difficulty
    private JButton speedButton;
    private JButton pauseButton;  // New pause button
    private JLabel visitorsLabel;
    private JLabel herbivoresLabel;
    private JLabel carnivoresLabel;
    private JLabel winProgressLabel; // New label for displaying win progress

    // UI colors
    private final Color PANEL_BG = new Color(0, 60, 0, 180);
    private final Color PANEL_BORDER = new Color(0, 100, 0);
    private final Color HEADER_COLOR = new Color(255, 255, 150);
    private final Color VALUE_COLOR = new Color(220, 255, 220);
    private final Color HIGHLIGHT_COLOR = new Color(255, 215, 0);
    private final Color SPEED_BUTTON_BG = new Color(60, 100, 60);
    private final Color SPEED_BUTTON_HOVER = new Color(80, 120, 80);
    private final Color PAUSE_BUTTON_BG = new Color(60, 60, 100);
    private final Color PAUSE_BUTTON_HOVER = new Color(80, 80, 120);

    // Button icons
    private ImageIcon speedIcon;
    private ImageIcon pauseIcon;
    private ImageIcon playIcon;

    public StatsPanel(GameEngine engine) {
        this.gameEngine = engine;

        // Load icons
        try {
            speedIcon = new ImageIcon(getClass().getResource("/imagesGui/speed.png"));
            Image scaledSpeed = speedIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            speedIcon = new ImageIcon(scaledSpeed);

            pauseIcon = new ImageIcon(getClass().getResource("/imagesGui/pause.png"));
            Image scaledPause = pauseIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            pauseIcon = new ImageIcon(scaledPause);

            playIcon = new ImageIcon(getClass().getResource("/imagesGui/start.png"));
            Image scaledPlay = playIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            playIcon = new ImageIcon(scaledPlay);
        } catch (Exception e) {
            System.out.println("Error loading button icons: " + e.getMessage());
            // Will fall back to text-only buttons if icons can't be loaded
        }

        // Change from GridLayout to BorderLayout for more flexibility
        setLayout(new BorderLayout());
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PANEL_BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Create stat labels
        cashLabel = createStatLabel("Cash: $10,000");
        timeLabel = createStatLabel("Time: 00:00:00");
        difficultyLabel = createStatLabel("Difficulty: Easy"); // Initialize with default difficulty
        visitorsLabel = createStatLabel("Visitors: 0");
        herbivoresLabel = createStatLabel("Herbivores: 0");
        carnivoresLabel = createStatLabel("Carnivores: 0");
        winProgressLabel = createStatLabel("Progress: 0/3 months");

        // Make difficulty label and win progress label stand out with a different color
        difficultyLabel.setForeground(HIGHLIGHT_COLOR);
        winProgressLabel.setForeground(HIGHLIGHT_COLOR);

        // Create button panel for right side (EAST)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        // Create speed button with icon
        speedButton = createControlButton("Speed", SPEED_BUTTON_BG, speedIcon);
        speedButton.addActionListener(e -> {
            gameEngine.cycleSpeed();
            updateStats();
        });
        speedButton.setToolTipText("Click to cycle through game speeds (second → minute → hour → day)");

        // Create pause button with icon
        pauseButton = createControlButton("Pause", PAUSE_BUTTON_BG, pauseIcon);
        pauseButton.addActionListener(e -> {
            togglePauseGame();
            updatePauseButton();
        });
        pauseButton.setToolTipText("Click to pause/resume the game");

        // Make both buttons the same size
        Dimension buttonSize = new Dimension(120, 40);
        speedButton.setPreferredSize(buttonSize);
        pauseButton.setPreferredSize(buttonSize);

        // Add small vertical padding to button panel to ensure buttons display fully
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

        buttonPanel.add(speedButton);
        buttonPanel.add(pauseButton);

        // Create stats panel for center - now with 7 elements including difficulty and win progress
        JPanel statsPanel = new JPanel(new GridLayout(1, 7, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(cashLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(difficultyLabel); // Add difficulty label to the panel
        statsPanel.add(winProgressLabel); // Add win progress label to the panel
        statsPanel.add(visitorsLabel);
        statsPanel.add(herbivoresLabel);
        statsPanel.add(carnivoresLabel);

        // Add panels to main layout
        add(statsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(VALUE_COLOR);
        return label;
    }

    private JButton createControlButton(String text, Color bgColor, ImageIcon icon) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(true);

        // Apply a more visible border with proper margins to ensure it's fully displayed
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // Set icon if available
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(8);
        }

        // Use a clearer visual difference for hover with full border visibility
        final Color hoverColor = bgColor.brighter();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(HIGHLIGHT_COLOR, 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
        });

        return button;
    }

    private void togglePauseGame() {
        if (gameEngine.isPaused()) {
            gameEngine.resumeGame();
        } else {
            gameEngine.pauseGame();
        }
    }

    private void updatePauseButton() {
        boolean isPaused = gameEngine.isPaused();
        pauseButton.setText(isPaused ? "Resume" : "Pause");
        pauseButton.setIcon(isPaused ? playIcon : pauseIcon);
        pauseButton.setBackground(isPaused ? new Color(76, 175, 80) : PAUSE_BUTTON_BG);

        // Ensure size remains consistent
        pauseButton.setPreferredSize(new Dimension(120, 40));
    }

    public void updateStats() {
        // Update cash display with formatting
        cashLabel.setText(String.format("Cash: $%,d", gameEngine.getCash()));

        // Update time display
        timeLabel.setText("Time: " + gameEngine.getTimePassed());

        // Update difficulty based on gameEngine's difficulty level
        String difficultyText = "Easy";
        switch (gameEngine.difficulty) {
            case 1: difficultyText = "Easy"; break;
            case 2: difficultyText = "Medium"; break;
            case 3: difficultyText = "Hard"; break;
        }
        difficultyLabel.setText("Difficulty: " + difficultyText);

        // Update win progress
        try {
            int consecutiveMonths = gameEngine.getConsecutiveSuccessfulMonths();
            int requiredMonths = gameEngine.getRequiredConsecutiveMonths();

            // Change color based on progress
            if (consecutiveMonths > 0) {
                // Use a gradient from yellow to green based on progress
                float progress = (float)consecutiveMonths / requiredMonths;
                Color progressColor = new Color(
                    (int)(255 * (1 - progress)), // Red component decreases
                    (int)(215 + (40 * progress)), // Green component increases
                    0  // Blue component stays at 0
                );
                winProgressLabel.setForeground(progressColor);
            } else {
                winProgressLabel.setForeground(HIGHLIGHT_COLOR);
            }

            winProgressLabel.setText(String.format("Progress: %d/%d months", consecutiveMonths, requiredMonths));

            // If game is over, update the label accordingly
            if (gameEngine.isGameOver()) {
                if (gameEngine.isGameWon()) {
                    winProgressLabel.setText("Victory!");
                    winProgressLabel.setForeground(new Color(0, 255, 0)); // Bright green
                } else {
                    winProgressLabel.setText("Game Over");
                    winProgressLabel.setForeground(new Color(255, 0, 0)); // Bright red
                }
            }
        } catch (Exception e) {
            // If methods aren't available yet, just show default
            winProgressLabel.setText("Progress: 0/3 months");
        }

        // Update speed button text with current speed level
        speedButton.setText(gameEngine.getSpeedLevel());

        // Update pause button state
        updatePauseButton();

        // Update visitors and animals count
        visitorsLabel.setText(String.format("Visitors: %d", gameEngine.getVisitors()));
        herbivoresLabel.setText(String.format("Herbivores: %d", gameEngine.getHerbivores()));
        carnivoresLabel.setText(String.format("Carnivores: %d", gameEngine.getCarnivores()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw more distinct gradient background to separate from map
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(30, 70, 30, 240),
            0, getHeight(), new Color(20, 50, 20, 240)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw stronger bottom border, but leave some space to not overlap with buttons
        g2d.setColor(new Color(0, 100, 0, 200));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);

        super.paintComponent(g);
    }
}
