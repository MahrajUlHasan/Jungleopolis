package Gui;

import entity.util.*;
import org.example.GameEngine;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.stream.IntStream;

public class InventoryPanel extends JPanel {
    private static final int SLOT_SIZE = 70; // Increased slot size
    private static final int PADDING = 10; // Increased padding

    private final GameEngine gameEngine;
    private final Inventory inventory;

    // UI Components
    private final JPanel slotsPanel;
    private final JButton soundButton;
    private final JButton marketButton;
    private InventorySlot[] slots;

    public InventoryPanel(GameEngine engine) {
        this.gameEngine = engine;
        this.inventory = engine.getInventory();

        configurePanel();

        this.soundButton = createSoundButton();
        this.marketButton = createMarketButton();
        this.slotsPanel = createSlotsPanel();

        addComponentsToPanel();
    }

    private void configurePanel() {
        // Increase the panel height to accommodate the scrollbar below the slots
        setBackground(new Color(60, 35, 15, 220));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout(10, 0));
    }

    private JButton createSoundButton() {
        JButton button = createStyledButton(GUI.isSoundMuted() ? "Sound Off" : "Sound On", new Color(60, 60, 60));
        button.setPreferredSize(new Dimension(120, 50));
        setButtonIcon(button, GUI.isSoundMuted() ? "mute.png" : "unmute.png");
        button.addActionListener(e -> toggleSound(button));
        return button;
    }

    private JButton createMarketButton() {
        JButton button = createStyledButton("Market", new Color(128, 0, 128));
        button.setPreferredSize(new Dimension(120, 50));
        button.addActionListener(e -> showMarketDialog());
        return button;
    }

    private JPanel createSlotsPanel() {
        // Create a panel with FlowLayout for horizontal arrangement
        JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING, 0));
        itemsPanel.setOpaque(false);

        // Get the number of items in the Item enum
        Item[] itemValues = Item.values();
        int itemCount = itemValues.length;

        // Create slots for each possible item
        this.slots = new InventorySlot[itemCount];
        for (int i = 0; i < itemCount; i++) {
            slots[i] = new InventorySlot(i);
            itemsPanel.add(slots[i]);
        }

        // Create a container panel with vertical BoxLayout to separate slots from scrollbar
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        // Add the items panel to the container
        containerPanel.add(itemsPanel);

        // Add some vertical space before the scrollbar
        containerPanel.add(Box.createVerticalStrut(5));

        // Create a scroll pane to hold the container panel
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Customize the scroll bar appearance
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUnitIncrement(SLOT_SIZE);
        horizontalScrollBar.setBlockIncrement(SLOT_SIZE * 3);

        // Style the scrollbar to match the game's theme
        horizontalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 120, 40);
                this.trackColor = new Color(60, 35, 15, 150);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createScrollButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createScrollButton();
            }

            private JButton createScrollButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                    return;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint a rounded thumb with gradient
                g2.setPaint(new GradientPaint(
                    thumbBounds.x, thumbBounds.y, new Color(200, 140, 60),
                    thumbBounds.x, thumbBounds.y + thumbBounds.height, new Color(160, 100, 40)
                ));

                g2.fillRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width, thumbBounds.height, 10, 10);

                // Add a subtle highlight
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawRoundRect(thumbBounds.x, thumbBounds.y,
                    thumbBounds.width - 1, thumbBounds.height - 1, 10, 10);

                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint a subtle track
                g2.setPaint(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y,
                    trackBounds.width, trackBounds.height, 5, 5);

                g2.dispose();
            }
        });

        // Create a wrapper panel with BorderLayout
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        return wrapperPanel;
    }


    private void addComponentsToPanel() {
        JPanel leftPanel = createSidePanel(FlowLayout.LEFT, soundButton);
        JPanel rightPanel = createSidePanel(FlowLayout.RIGHT, marketButton);

        add(leftPanel, BorderLayout.WEST);
        add(slotsPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createSidePanel(int alignment, JButton button) {
        JPanel panel = new JPanel(new FlowLayout(alignment, 5, 0));
        panel.setOpaque(false);
        panel.add(button);
        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void setButtonIcon(JButton button, String iconName) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imagesGui/" + iconName));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Use text if icon loading fails
        }
    }

    private void toggleSound(JButton button) {
        boolean isMuted = !GUI.isSoundMuted();
        GUI.setSoundMuted(isMuted);

        if (isMuted) {
            gameEngine.stopMusic();
        } else {
            gameEngine.playMusic(0);
        }

        button.setText(isMuted ? "Sound Off" : "Sound On");
        setButtonIcon(button, isMuted ? "mute.png" : "unmute.png");
        inventory.syncSoundState(isMuted);
    }

    private void showMarketDialog() {
        boolean wasPaused = gameEngine.isPaused();
        if (!wasPaused) {
            gameEngine.pauseGame();
        }
        inventory.showBuySellDialog(gameEngine, this, wasPaused);
    }

    public void refreshInventorySlots() {
        for (InventorySlot slot : slots) {
            slot.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackgroundGradient(g2d);
        drawBorder(g2d);

        super.paintComponent(g);
    }

    private void drawBackgroundGradient(Graphics2D g2d) {
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(80, 50, 30, 220),
                0, getHeight(), new Color(60, 35, 20, 220)
        );
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawBorder(Graphics2D g2d) {
        g2d.setColor(new Color(255, 215, 0, 100)); // Gold-tinted border
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawLine(0, 0, getWidth(), 0);
    }

    /**
     * Helper method to draw a panel background with 3D effect
     * @param g2d Graphics context
     * @param x X position
     * @param y Y position
     * @param width Panel width
     * @param height Panel height
     */
    public void drawPanel(Graphics2D g2d, int x, int y, int width, int height) {
        // Draw shadow with reduced opacity
        for (int i = 3; i > 0; i--) {
            g2d.setColor(new Color(0, 0, 0, 40 / i));
            g2d.fillRoundRect(x + i, y + i, width, height, 15, 15);
        }

        // Draw panel background with transparency
        Color panelColor = new Color(0, 0, 0, 120);
        g2d.setColor(panelColor);
        g2d.fillRoundRect(x, y, width, height, 15, 15);

        // Draw panel border with reduced opacity
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x, y, width, height, 15, 15);

        // Draw highlight at top for 3D effect with reduced opacity
        GradientPaint gp = new GradientPaint(
            x, y, new Color(255, 255, 255, 40),
            x, y + 30, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(gp);
        g2d.fillRoundRect(x, y, width, 30, 15, 15);
    }

    // Inner class for inventory slots
    private class InventorySlot extends JButton {
        private final int slotIndex;
//        private final Entity item;

        public InventorySlot(int index) {
            this.slotIndex = index;
            configureSlot();
        }

        private void configureSlot() {
            setPreferredSize(new Dimension(SLOT_SIZE, SLOT_SIZE));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); // Add hand cursor for better UX

            addActionListener(e -> handleSlotClick());
        }

        private void handleSlotClick() {
            Item item = getItemInSlot();
            if (item != null && inventory.getItemCount(item) > 0) {
                inventory.toggleItemSelection(item);
                refreshInventorySlots();
            }
        }

        private Item getItemInSlot() {
            return inventory.getItemAtSlot(slotIndex);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawSlotBackground(g2d);
            drawSlotBorder(g2d);
            drawSlotContent(g2d);
        }

        private void drawSlotBackground(Graphics2D g2d) {
            Item item = getItemInSlot();
            Color startColor, endColor;

            if (item != null && inventory.getItemCount(item) > 0) {
                // Slot with item
                startColor = new Color(90, 55, 25, 200);
                endColor = new Color(60, 35, 15, 200);
            } else {
                // Empty slot - more subtle
                startColor = new Color(90, 55, 25, 100);
                endColor = new Color(60, 35, 15, 100);
            }

            GradientPaint slotGradient = new GradientPaint(
                    0, 0, startColor,
                    0, getHeight(), endColor
            );
            g2d.setPaint(slotGradient);

            // Draw rounded rectangle for better appearance
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }

        private void drawSlotBorder(Graphics2D g2d) {
            Item item = getItemInSlot();
            boolean isSelected = (item != null && inventory.isItemSelected(item));
            boolean hasItem = (item != null && inventory.getItemCount(item) > 0);

            if (hasItem) {
                g2d.setColor(isSelected ? new Color(255, 215, 0) : new Color(255, 255, 255, 180));
                g2d.setStroke(new BasicStroke(isSelected ? 3f : 1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                // Add a subtle glow effect for selected items
                if (isSelected) {
                    g2d.setColor(new Color(255, 215, 0, 50));
                    g2d.setStroke(new BasicStroke(5f));
                    g2d.drawRoundRect(-2, -2, getWidth() + 3, getHeight() + 3, 12, 12);
                }
            } else {
                // Subtle border for empty slots
                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        }

        private void drawSlotContent(Graphics2D g2d) {
            Item item = getItemInSlot();
            if (item != null && inventory.getItemCount(item) > 0) {
                drawItemName(g2d, item);
                drawItemCount(g2d, item);
                drawSelectionHighlight(g2d, item);
            }
        }

        private void drawItemName(Graphics2D g2d, Item item) {
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(Color.WHITE);

            String itemName = formatItemName(item.toString());
            int textX = (getWidth() - g2d.getFontMetrics().stringWidth(itemName)) / 2;
            g2d.drawString(itemName, textX, 20);
        }

        private String formatItemName(String name) {
            // Convert names like AIR_SHIP to "Air Ship"
            if (name.contains("_")) {
                StringBuilder formatted = new StringBuilder();
                String[] parts = name.split("_");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i].toLowerCase();
                    formatted.append(part.substring(0, 1).toUpperCase())
                             .append(part.substring(1));
                    if (i < parts.length - 1) {
                        formatted.append(" ");
                    }
                }
                return formatted.toString();
            } else {
                // Just capitalize first letter for single words
                return name.substring(0, 1) + name.substring(1).toLowerCase();
            }
        }

        private void drawItemCount(Graphics2D g2d, Item item) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.setColor(new Color(255, 255, 150));

            String countText = "x" + inventory.getItemCount(item);
            int countX = (getWidth() - g2d.getFontMetrics().stringWidth(countText)) / 2;
            g2d.drawString(countText, countX, 40);
        }

        private void drawSelectionHighlight(Graphics2D g2d, Item item) {
            if (inventory.isItemSelected(item)) {
                g2d.setColor(new Color(255, 255, 100, 50));
                g2d.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
            }
        }
    }
}