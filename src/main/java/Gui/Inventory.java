package Gui;

import entity.util.Item;
import org.example.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private static final int SLOTS_PER_ROW = 10;

    private final Map<Item, Integer> items;
    private final Map<Integer, Item> slotItems;
    private boolean isMuted;
    private Item selectedItem;

    public Inventory(boolean initialMuteState) {
        this.items = initializeItems();
        this.slotItems = initializeSlotItems();
        this.isMuted = initialMuteState;
    }

    private Map<Item, Integer> initializeItems() {
        Map<Item, Integer> items = new HashMap<>();
        for (Item item : Item.values()) {
            items.put(item, 0);
        }
        return items;
    }

    private Map<Integer, Item> initializeSlotItems() {
        return new HashMap<>();
    }

    public void syncSoundState(boolean newMuteState) {
        this.isMuted = newMuteState;
    }

    public void addItem(Item item) {
        items.put(item, items.getOrDefault(item, 0) + 1);
        slotItems.put(item.ordinal(), item);
    }

    public void removeItem(Item item) {
        int count = items.getOrDefault(item, 0);
        if (count > 0) {
            items.put(item, count - 1);
            if (count == 1) {
                clearSlot(item);
            }
        }
    }

    private void clearSlot(Item item) {
        if (selectedItem == item) {
            selectedItem = null;
        }
        slotItems.entrySet().removeIf(entry -> entry.getValue() == item);
    }

    public int getItemCount(Item item) {
        return items.getOrDefault(item, 0);
    }

    public Item getItemAtSlot(int slotIndex) {
        return slotItems.get(slotIndex);
    }

    public boolean isItemSelected(Item item) {
        return item == selectedItem;
    }

    public void toggleItemSelection(Item item) {
        selectedItem = (selectedItem == item) ? null : item;
    }

    public boolean hasSelectedItem() {
        return selectedItem != null;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void showBuySellDialog(GameEngine engine, Component parent) {
        showBuySellDialog(engine, parent, false);
    }

    public void showBuySellDialog(GameEngine engine, Component parent, boolean wasPaused) {
        if (!wasPaused) {
            engine.pauseGame();
        }

        Window gameWindow = SwingUtilities.getWindowAncestor(engine);
        if (gameWindow == null) return;

        JPanel glassPane = getGlassPane(gameWindow);
        if (glassPane == null) {
            showFallbackDialog(engine, parent, wasPaused);
            return;
        }

        JPanel slideOutPanel = createSlideOutPanel(gameWindow);
        setupGlassPane(glassPane, slideOutPanel);

        createMarketContent(slideOutPanel, engine, glassPane, wasPaused);
        animateSlideIn(slideOutPanel, gameWindow.getHeight());
    }

    private JPanel getGlassPane(Window gameWindow) {
        if (gameWindow instanceof JFrame) {
            return (JPanel) ((JFrame) gameWindow).getGlassPane();
        } else if (gameWindow instanceof JDialog) {
            return (JPanel) ((JDialog) gameWindow).getGlassPane();
        }
        return null;
    }

    private JPanel createSlideOutPanel(Window gameWindow) {
        JPanel slideOutPanel = new JPanel(new BorderLayout());
        slideOutPanel.setPreferredSize(new Dimension(300, gameWindow.getHeight()));
        slideOutPanel.setBackground(new Color(40, 70, 40, 240));
        slideOutPanel.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(0, 100, 0)));
        return slideOutPanel;
    }

    private void setupGlassPane(JPanel glassPane, JPanel slideOutPanel) {
        glassPane.setLayout(new BorderLayout());
        glassPane.removeAll();
        glassPane.add(slideOutPanel, BorderLayout.EAST);
        glassPane.setVisible(true);
    }

    private void animateSlideIn(JPanel slideOutPanel, int height) {
        Timer slideTimer = new Timer(10, null);
        final int[] pos = {0};
        slideTimer.addActionListener(e -> {
            pos[0] += 20;
            if (pos[0] >= 300) {
                slideOutPanel.setSize(300, height);
                slideTimer.stop();
            } else {
                slideOutPanel.setSize(pos[0], height);
            }
            slideOutPanel.revalidate();
            slideOutPanel.repaint();
        });
        slideTimer.start();
    }

    private void showFallbackDialog(GameEngine engine, Component parent, boolean wasPaused) {
        JDialog dialog = new JDialog((Frame) null, "Market", true);
        dialog.setSize(300, 600);
        dialog.setLocationRelativeTo(engine);

        JPanel marketPanel = new JPanel(new BorderLayout());
        createMarketContent(marketPanel, engine, null, wasPaused);
        dialog.add(marketPanel);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (!wasPaused) {
                    engine.resumeGame();
                }
            }
        });

        dialog.setVisible(true);
    }

    private void createMarketContent(JPanel container, GameEngine engine, JPanel glassPane, boolean wasPaused) {
        container.setLayout(new BorderLayout());
        container.add(createHeader(glassPane, wasPaused, engine), BorderLayout.NORTH);
        container.add(createMarketPanel(engine), BorderLayout.CENTER);
        container.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader(JPanel glassPane, boolean wasPaused, GameEngine engine) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 60, 30));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("MARKET", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 255, 220));

        JButton closeButton = createCloseButton(glassPane, wasPaused, engine);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(closeButton, BorderLayout.EAST);
        return headerPanel;
    }

    private JButton createCloseButton(JPanel glassPane, boolean wasPaused, GameEngine engine) {
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(180, 0, 0));
        closeButton.setBorder(BorderFactory.createLineBorder(new Color(255, 100, 100), 2));
        closeButton.setFocusPainted(false);

        closeButton.addActionListener(e -> closeMarket(glassPane, wasPaused, engine));
        return closeButton;
    }

    private void closeMarket(JPanel glassPane, boolean wasPaused, GameEngine engine) {
        if (glassPane != null) {
            glassPane.setVisible(false);
            glassPane.removeAll();
            if (!wasPaused) {
                engine.resumeGame();
            }
        }
    }

    private JScrollPane createMarketPanel(GameEngine engine) {
        JPanel marketPanel = new JPanel();
        marketPanel.setLayout(new BoxLayout(marketPanel, BoxLayout.Y_AXIS));
        marketPanel.setBackground(new Color(40, 70, 40));
        marketPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addAnimalCards(marketPanel, engine);

        JScrollPane scrollPane = new JScrollPane(marketPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void addAnimalCards(JPanel marketPanel, GameEngine engine) {
        for(Item item : Item.values()) {
            int buyPrice = 100;
            int sellPrice = 50;

            // Set different prices for different items
            switch(item) {
                // Animals
                case LION:
                    buyPrice = 200;
                    sellPrice = 100;
                    break;
                case WOLF:
                    buyPrice = 150;
                    sellPrice = 75;
                    break;
                case BUFFALO:
                    buyPrice = 100;
                    sellPrice = 50;
                    break;
                case GIRAFFE:
                    buyPrice = 120;
                    sellPrice = 60;
                    break;

                // Infrastructure
                case ROAD:
                    buyPrice = 20;
                    sellPrice = 10;
                    break;
                case POND:
                    buyPrice = 80;
                    sellPrice = 40;
                    break;
                case TREE:
                    buyPrice = 30;
                    sellPrice = 15;
                    break;
                case GRASS:
                    buyPrice = 10;
                    sellPrice = 5;
                    break;
                case CHARGING_STATION:
                    buyPrice = 300;
                    sellPrice = 150;
                    break;

                // Vehicles and Personnel
                case JEEP:
                    buyPrice = 150;
                    sellPrice = 75;
                    break;
                case RANGER:
                    buyPrice = 200;
                    sellPrice = 100;
                    break;

                // Surveillance
                case DRONE:
                    buyPrice = 250;
                    sellPrice = 125;
                    break;
                case AIR_SHIP:
                    buyPrice = 350;
                    sellPrice = 175;
                    break;
                case AIRSHIP_PATROL_FLAG:
                    buyPrice = 50;
                    sellPrice = 25;
                    break;
                case DRONE_PATROL_FLAG:
                    buyPrice = 50;
                    sellPrice = 25;
                    break;
                case CAMERA:
                    buyPrice = 100;
                    sellPrice = 50;
                    break;

                // Threats
                case POACHER:
                    buyPrice = 500;
                    sellPrice = 250;
                    break;
            }

            addAnimalCard(marketPanel, engine, item, item.name(), buyPrice, sellPrice, "/images/" + item.toString().toLowerCase() + ".png");
        }
    }

    private JLabel createFooter() {
        JLabel infoLabel = new JLabel("Buy animals for your zoo!", JLabel.CENTER);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return infoLabel;
    }

    private void addAnimalCard(JPanel panel, GameEngine engine, Item item, String name, int buyPrice, int sellPrice, String iconPath) {
        JPanel card = createAnimalCardPanel();
        JPanel infoPanel = createAnimalInfoPanel(item, name, iconPath);
        JPanel buttonPanel = createAnimalButtonPanel(engine, item, buyPrice, sellPrice);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        panel.add(card);
        panel.add(Box.createVerticalStrut(10));
    }

    private JPanel createAnimalCardPanel() {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(50, 80, 50));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 30), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(280, 100));
        return card;
    }

    private JPanel createAnimalInfoPanel(Item item, String name, String iconPath) {
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setOpaque(false);

        JLabel iconLabel = createAnimalIconLabel(item, iconPath);
        JLabel nameLabel = createAnimalNameLabel(name);
        JLabel quantityLabel = createAnimalQuantityLabel(item);

        infoPanel.add(iconLabel, BorderLayout.WEST);
        infoPanel.add(nameLabel, BorderLayout.CENTER);
        infoPanel.add(quantityLabel, BorderLayout.SOUTH);
        return infoPanel;
    }

    private JLabel createAnimalIconLabel(Item item, String iconPath) {
        try {
            ImageIcon animalIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaled = animalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            return new JLabel(getAnimalEmoji(item));
        }
    }

    private String getAnimalEmoji(Item item) {
        switch (item) {
            // Animals
            case LION: return "🦁";
            case GIRAFFE: return "🦒";
            case BUFFALO: return "🐃";
            case WOLF: return "🐺";

            // Infrastructure
            case ROAD: return "🛣️";
            case POND: return "🌊";
            case TREE: return "🌳";
            case GRASS: return "🌿";
            case CHARGING_STATION: return "🔌";

            // Vehicles and Personnel
            case JEEP: return "🚙";
            case RANGER: return "👮";

            // Surveillance
            case DRONE: return "🛸";
            case AIR_SHIP: return "🛩️";
            case AIRSHIP_PATROL_FLAG: return "🚩";
            case DRONE_PATROL_FLAG: return "📍";
            case CAMERA: return "📷";

            // Threats
            case POACHER: return "🔫";

            // Default
            default: return "❓";
        }
    }

    private JLabel createAnimalNameLabel(String name) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        return nameLabel;
    }

    private JLabel createAnimalQuantityLabel(Item item) {
        JLabel quantityLabel = new JLabel("Owned: " + items.getOrDefault(item, 0));
        quantityLabel.setForeground(new Color(200, 255, 200));
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        return quantityLabel;
    }

    private JPanel createAnimalButtonPanel(GameEngine engine, Item item, int buyPrice, int sellPrice) {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(120, 80));

        JButton buyButton = createPriceButton("Buy: $" + buyPrice, new Color(76, 175, 80));
        JButton sellButton = createPriceButton("Sell: $" + sellPrice, new Color(229, 57, 53));

        setupBuyButton(buyButton, engine, item, buyPrice);
        setupSellButton(sellButton, engine, item, sellPrice);

        buttonPanel.add(buyButton);
        buttonPanel.add(sellButton);
        return buttonPanel;
    }

    private JButton createPriceButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void setupBuyButton(JButton buyButton, GameEngine engine, Item item, int buyPrice) {
        buyButton.addActionListener(e -> {
            if (engine.getCash() >= buyPrice) {
                engine.updateCash(-buyPrice);
                addItem(item);
                engine.refreshInventoryUI();
                refreshButton(buyButton, new Color(100, 220, 100), new Color(76, 175, 80));
            } else {
                showTooltipError(buyButton, "Not enough cash! Need $" + buyPrice);
            }
        });
    }

    private void setupSellButton(JButton sellButton, GameEngine engine, Item item, int sellPrice) {
        sellButton.addActionListener(e -> {
            if (items.getOrDefault(item, 0) > 0) {
                engine.updateCash(sellPrice);
                removeItem(item);

                refreshButton(sellButton, new Color(255, 100, 100), new Color(229, 57, 53));
            } else {
                showTooltipError(sellButton, "Nothing to sell!");
            }
        });
    }



    private void refreshButton(JButton button, Color successColor, Color defaultColor) {
        button.setBackground(successColor);
        Timer resetTimer = new Timer(500, evt -> button.setBackground(defaultColor));
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void showTooltipError(Component component, String message) {
        JWindow tooltip = new JWindow();
        JPanel content = createTooltipContent(message);

        tooltip.setContentPane(content);
        tooltip.pack();
        positionTooltip(component, tooltip);

        tooltip.setVisible(true);
        autoHideTooltip(tooltip);
    }

    private JPanel createTooltipContent(String message) {
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        content.setBackground(new Color(255, 220, 220));

        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 12));
        content.add(errorLabel);
        return content;
    }

    private void positionTooltip(Component component, JWindow tooltip) {
        Point p = component.getLocationOnScreen();
        tooltip.setLocation(p.x - (tooltip.getWidth() - component.getWidth()) / 2,
                p.y + component.getHeight() + 5);
    }

    private void autoHideTooltip(JWindow tooltip) {
        Timer hideTimer = new Timer(2000, e -> tooltip.dispose());
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
}