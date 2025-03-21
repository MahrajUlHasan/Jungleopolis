package Gui;

import entity.Entity;
import entity.Item;
import entity.Lion;
import org.example.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private static final int SLOT_SIZE = 40;
    private static final int SLOTS_PER_ROW = 10;
    private static final int PADDING = 10;
    private static final int SOUND_BUTTON_SIZE = 70; // Increased from 40


    private Map<Item, Integer> items;
    private Rectangle bounds;
    private Rectangle buyButton;
    private Rectangle soundButton;
    private boolean isMuted = false;
    private Item selectedItem = null;
    private Map<Integer, Item> slotItems;

    public Inventory() {
        items = new HashMap<>();
        slotItems = new HashMap<>();
        // i am setting the inventory to empty, and will add things as we buy them.
        for (Item item : Item.values()) {
            items.put(item, 0);
        }
    }

    public void draw(Graphics2D g2d, GameEngine engine) {
        // Calculate dimensions and positions
        int totalWidth = SLOTS_PER_ROW * SLOT_SIZE;
        int startX = (engine.screenWidth - totalWidth) /2;
        int startY = engine.screenHeight - (SLOT_SIZE*2 + 3 * PADDING);

        // Set button dimensions
        buyButton = new Rectangle(startX + totalWidth + PADDING, startY, 100, SLOT_SIZE);
        soundButton = new Rectangle(startX - 100 - PADDING, startY, 40, SLOT_SIZE);
        bounds = new Rectangle(startX, startY, totalWidth, SLOT_SIZE);

        // Draw sound button
        drawSoundButton(g2d, engine);

        // Draw inventory background
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2d.setColor(Color.WHITE);
        g2d.draw(bounds);

        // Draw slots and items

        int x = startX;
        int y = startY;
        int slot = 0;

        for (int i = 0; i < SLOTS_PER_ROW; i++) {
            // Draw slot
            g2d.setColor(Color.GRAY);
            g2d.drawRect(x, y, SLOT_SIZE, SLOT_SIZE);

            // Draw item if slot is occupied
            Item slotItem = slotItems.get(i);
            if (slotItem != null && items.get(slotItem) > 0) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString(slotItem.toString(), x + 2, y + 15);
                g2d.drawString("x" + items.get(slotItem), x + 2, y + 30);
            }

            x += SLOT_SIZE;
        }


        drawBuyButton(g2d);
    }

    private int getSlotAtPosition(int mouseX, int mouseY) {
        if (!bounds.contains(mouseX, mouseY)) return -1;

        int relativeX = mouseX - bounds.x;
        return relativeX / SLOT_SIZE;
    }


    public void handleClick(int mouseX, int mouseY, GameEngine engine) {
        System.out.println("Click at: " + mouseX + "," + mouseY); // Debug click location

        if (bounds.contains(mouseX, mouseY)) {
            System.out.println("Click in inventory bounds"); // Debug inventory click
            selectItem(mouseX, mouseY);
        } else if (buyButton.contains(mouseX, mouseY)) {
            System.out.println("Click on buy button"); // Debug buy button click
            showBuySellDialog(engine);
        } else if (selectedItem != null) {
            System.out.println("Attempting to place: " + selectedItem); // Debug placement attempt
            // Attempt to place the selected item
            engine.placeSelectedItem(mouseX, mouseY, selectedItem);
            selectedItem = null; // Clear selection after placement attempt
        }
    }

//    private boolean tryPlaceItem(int mouseX, int mouseY, GameEngine engine) {
//        if (selectedItem == null) return false;
//
//        // Check if placement location is valid
//        if (engine.getMap().isBlocked(null, mouseX, mouseY)) {
//            return false;
//        }
//
//        // Create and place the entity based on selected item
//        Entity entity = createEntityFromItem(selectedItem, engine);
//        if (entity != null) {
//            entity.x = mouseX;
//            entity.y = mouseY;
//            engine.getMap().addEnt(entity);
//            return true;
//        }
//
//        return false;
//    }

//    private Entity createEntityFromItem(Item item, GameEngine engine) {
//        switch (item) {
//            case LION:
//                return new Lion(engine, engine.getMap(), engine.keyHandler, engine.mouseHandler);
//            // Add other cases for different items
//            default:
//                return null;
//        }
//    }

//    public Item getSelectedItem() {
//        return selectedItem;
//    }


    public void selectItem(int mouseX, int mouseY) {
        int slot = getSlotAtPosition(mouseX, mouseY);
        System.out.println("Slot clicked: " + slot); // Debug slot calculation

        if (slot >= 0 && slot < Item.values().length) {
            Item clickedItem = Item.values()[slot];
            int itemCount = items.getOrDefault(clickedItem, 0);
            System.out.println("Clicked item: " + clickedItem + ", Count: " + itemCount); // Debug item info

            if (itemCount > 0) {
                selectedItem = clickedItem;
                System.out.println("Item selected: " + selectedItem); // Debug selection
            } else {
                System.out.println("Item not available in inventory"); // Debug inventory check
            }
        }
    }


    public void addItem(Item item) {
        // Update item count
        items.put(item, items.getOrDefault(item, 0) + 1);
        slotItems.put(item.ordinal(), item);
    }

    public void removeItem(Item item) {
        int count = items.getOrDefault(item, 0);
        if (count > 0) {
            items.put(item, count - 1);
            // Remove from slot if count reaches 0
            if (count == 1) {
                int slotToRemove = -1;
                for (Map.Entry<Integer, Item> entry : slotItems.entrySet()) {
                    if (entry.getValue() == item) {
                        slotToRemove = entry.getKey();
                        break;
                    }
                }
                if (slotToRemove != -1) {
                    slotItems.remove(slotToRemove);
                }
            }
        }
    }

//    public Item getItemInSlot(int slot) {
//        return slotItems.get(slot);
//    }

    //add sound button
//    private void drawSoundButton(Graphics2D g2d, GameEngine engine) {
//        try {
//            Image soundIcon = new ImageIcon(getClass().getResource("/imagesGui/" +
//                    (isMuted ? "mute.png" : "unmute.png"))).getImage();
//            g2d.drawImage(soundIcon, soundButton.x, soundButton.y,
//                    soundButton.width, soundButton.height, null);
//        } catch (Exception e) {
//            g2d.setColor(Color.GRAY);
//            g2d.fill(soundButton);
//            g2d.setColor(Color.WHITE);
//            g2d.drawString(isMuted ? "Off" : "On",
//                    soundButton.x + 5, soundButton.y + SLOT_SIZE/2);
//        }
//    }

    // the sound button visibility issue: The solution to do so is Ai generated, SOUND BUTTON SIZE


    private void drawSoundButton(Graphics2D g2d, GameEngine engine) {
        try {
            Image soundIcon = new ImageIcon(getClass().getResource("/imagesGui/" +
                    (isMuted ? "mute.png" : "unmute.png"))).getImage();
            soundButton = new Rectangle(soundButton.x, soundButton.y,
                    SOUND_BUTTON_SIZE, SOUND_BUTTON_SIZE);
            g2d.drawImage(soundIcon, soundButton.x, soundButton.y,
                    SOUND_BUTTON_SIZE, SOUND_BUTTON_SIZE, null);
        } catch (Exception e) {
            g2d.setColor(Color.GRAY);
            g2d.fill(soundButton);
            g2d.setColor(Color.WHITE);
            g2d.drawString(isMuted ? "Off" : "On",
                    soundButton.x + 5, soundButton.y + SLOT_SIZE/2);
        }
    }

    private void drawBuyButton(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(buyButton);
        g2d.setColor(Color.BLACK);
        g2d.draw(buyButton);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String buttonText = "Buy/Sell";
        int textX = buyButton.x + (buyButton.width - fm.stringWidth(buttonText)) / 2;
        int textY = buyButton.y + (buyButton.height + fm.getAscent()) / 2;
        g2d.drawString(buttonText, textX, textY);
    }

    //we implement a method responsible for shwoing a dailog for buying and selling items, and update the inventory and the cash accordingly.
    // added the current quantitie and success or failure messages too. that are still under consideration, we will work on that later.
    // added selling too, at lower price than buying.
    private void showBuySellDialog(GameEngine engine) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(engine), "Buy/Sell Items", true);
        dialog.setSize(engine.screenWidth/3, engine.screenHeight/3);
        dialog.setLocationRelativeTo(engine);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Headers
        gbc.gridy = 0;
        gbc.gridx = 0; panel.add(new JLabel("Item"), gbc);
        gbc.gridx = 1; panel.add(new JLabel("Name"), gbc);
        gbc.gridx = 2; panel.add(new JLabel("Price"), gbc);
        gbc.gridx = 3; panel.add(new JLabel("Actions"), gbc);
        gbc.gridx = 4; panel.add(new JLabel("Owned"), gbc); // this column is not in the UI Plan, but as a debuggin and testing purpose, i have added it.

        // Lion row
        gbc.gridy = 1;

        // Lion image
        ImageIcon lionIcon = null;
        try {
            lionIcon = new ImageIcon(getClass().getResource("/images/lion.png"));
            Image scaled = lionIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            lionIcon = new ImageIcon(scaled);
        } catch (Exception e) {
            gbc.gridx = 0; panel.add(new JLabel("🦁"), gbc);
        }
        if (lionIcon != null) {
            gbc.gridx = 0; panel.add(new JLabel(lionIcon), gbc);
        }

        // Name and price
        gbc.gridx = 1; panel.add(new JLabel("Lion"), gbc);
        gbc.gridx = 2; panel.add(new JLabel("200"), gbc);

        // Buy/Sell buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buyButton = new JButton("Buy");
        JButton sellButton = new JButton("Sell");

        // Show current quantity
        JLabel quantityLabel = new JLabel("x" + items.getOrDefault(Item.LION, 0));
        gbc.gridx = 4; panel.add(quantityLabel, gbc);

        // Buy button action
        buyButton.addActionListener(e -> {
            if (engine.getCash() >= 200) {
                engine.updateCash(-200);
                addItem(Item.LION);
                quantityLabel.setText("x" + items.get(Item.LION));
                JOptionPane.showMessageDialog(dialog,
                        "Lion purchased! Current cash: " + engine.getCash(),
                        "Purchase Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Not enough cash! Required: 200, Available: " + engine.getCash(),
                        "Purchase Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
            dialog.repaint();
        });

        // Sell button action
        sellButton.addActionListener(e -> {
            if (items.getOrDefault(Item.LION, 0) > 0) {
                engine.updateCash(150); // Sell for less than purchase price
                removeItem(Item.LION);
                quantityLabel.setText("x" + items.get(Item.LION));
                JOptionPane.showMessageDialog(dialog,
                        "Lion sold! Current cash: " + engine.getCash(),
                        "Sale Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "No lions to sell!",
                        "Sale Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
            dialog.repaint();
        });

        buttonsPanel.add(buyButton);
        buttonsPanel.add(sellButton);
        gbc.gridx = 3; panel.add(buttonsPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public Rectangle getBuyButton() {
        return buyButton;
    }
}