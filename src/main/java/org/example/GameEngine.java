package org.example;

import entity.*;
import Gui.*;
import entity.Item.*;
import entity.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class GameEngine extends JPanel implements Runnable {
    ///  Panel settings
    final public int tileUnit = 16; // 16x16 unit sized tiles
    final public int scale = 2; // scale the tiles are displayed
    final public int tileSize = tileUnit * scale; // actual size of tile 64x64 px

    // Getter for tileSize to make testing easier
    public int getTileSize() {
        return tileSize;
    }
    final public int maxColNUm = 48; // number of tiles in the column
    final public int maxRowNUm = 26; // number of tiles in the row
    final public int screenWidth = maxColNUm * tileSize; //  1536 px
    final public int screenHeight = maxRowNUm * tileSize;//  832 px
    // updating the cash in real time once animal is bought
    public int cash = 10000; // Initial cash

    final public int FPS = 90;

    protected int visitorsToAdd = 1; // Minimum number of visitors to add per day

    protected Thread gameThread; // PROCESS THREAD FOR THE GAME LOOP

    protected Boolean paused = false;

    protected KeyHandler keyHandler = new KeyHandler();

    protected MouseHandler mouseHandler = new MouseHandler(this);

    protected MapSpace map;

    protected boolean entitySelected = true;
    protected Entity selectedEntity = null;

    protected GUI ui;
    protected Sound sound;
    public int difficulty = 1; // Default difficulty is Easy (1)
    protected Inventory inventory;

    private InventoryPanel inventoryPanel;
    private StatsPanel statsPanel;

    protected int passedTime = 0;
    private long startTime;
    private boolean gameStarted = false;

    public int timeSpeed = 1; // 1->hour, 2->day, 3->week
    //private long startTime;            // The real start time of the game
    private long lastUpdateTime;       // Last real time when we updated the accumulated game hours
    private long accumulatedGameMillis = 0; // Game hours accumulated so far
    private Entity entityAtHand = null;
    public volatile boolean canUpdateStats = true;

    protected int visitorNum = 0;
    private int popularity = 0;

    // Variables for day-based tourist arrivals
    private long lastCheckedDay = -1; // Store the last day we checked for tourist arrivals
    // Maximum number of tourists the zoo can accommodate (must match jeepaAndVisitorHandler.MAX_VISITORS)
    private static final int MAX_TOURISTS = 100;

    // Game win/loss tracking
    private boolean gameOver = false;
    private boolean gameWon = false;
    private long lastCheckedMonth = -1; // Last month we checked win conditions
    private int consecutiveSuccessfulMonths = 0; // Number of consecutive months meeting all thresholds

    // Win condition thresholds based on difficulty
    private static final int[][] WIN_THRESHOLDS = {
        // Visitors, Herbivores, Carnivores, Cash
        {50, 20, 15, 200},  // Easy (index 0)
        {60, 30, 20, 300}, // Medium (index 1)
        {70, 40, 25, 500}  // Hard (index 2)
    };

    // Required consecutive months based on difficulty
    private static final int[] REQUIRED_MONTHS = {3, 6, 12}; // Easy, Medium, Hard


    public GameEngine(GUI ui) {
        // Set layout to BorderLayout
        setLayout(new BorderLayout());
        map = new MapSpace(this);
        this.ui = ui;
        this.sound = new Sound();

        // Set properties for the game engine panel itself (which replaces gameAreaPanel)
//        setPreferredSize(new Dimension(screenWidth, screenHeight )); // 50px top + 60px bottom
        setBackground(new Color(34, 139, 34));
        setFocusable(true);
        // We'll add the mouse listener in startGameThread()

        // UI resource loading moved to UI classes

        // Check if sound should be playing based on UI state
        if (!GUI.isSoundMuted()) {
            playMusic(0);
        }

        // Initialize inventory
        inventory = new Inventory(GUI.isSoundMuted());

        // Create and add inventory panel
        inventoryPanel = new InventoryPanel(this);
        add(inventoryPanel, BorderLayout.SOUTH);

        // Create and add stats panel
        statsPanel = new StatsPanel(this);
        add(statsPanel, BorderLayout.NORTH);

        // GameEngine itself is already the main panel, no need to add it to itself

        // Initialize time tracking
        startTime = currentTimeMillis();
        lastUpdateTime = startTime;
    }


    public void startGameThread() {
        gameThread = new Thread(this);
        map = new MapSpace(this);
        map.initRoads(); /// puts the start and end blocks of road to the map
        this.addMouseListener(mouseHandler);
        startTime = currentTimeMillis();
        lastUpdateTime = startTime;
        gameStarted = true;

        // Initialize time speed to hour (1) to ensure timer starts immediately
        timeSpeed = 1;

        // Initialize the last checked day to the current day
        lastCheckedDay = getCurrentGameDay();

        // Initialize the last checked month to the current month
        lastCheckedMonth = getMonthsPassed();

        initMap();

        gameThread.start(); /// CALLS run() method . Keep it at the end
    }

    private void initMap() {
        initRoad();
        initWater();
        initTrees();
        initAnimals();
    }
    private void initAnimals() {
        Giraffe giraffe1 = new Giraffe(this, map);
        map.addEnt(giraffe1);
        Giraffe giraffe2 = new Giraffe(this, map);
        map.addEnt(giraffe2);

        Buffalo buffalo1 = new Buffalo(this, map, keyHandler, mouseHandler);
        map.addEnt(buffalo1);
        Buffalo buffalo2 = new Buffalo(this, map, keyHandler, mouseHandler);
        map.addEnt(buffalo2);

        Lion lion1 = new Lion(this, map);
        map.addEnt(lion1);
        Lion lion2 = new Lion(this, map);
        map.addEnt(lion2);

    }


    private void initWater() {
        Point[][] grid = map.getGrid();
        Point point = grid[2][8];
        Water water = new Water(this);
        water.setX(point.x);
        water.setY(point.y);
        map.addEnt(water);

        Point point1 = grid[20][10];
        Water water1 = new Water(this);
        water1.setX(point1.x);
        water1.setY(point1.y);
        map.addEnt(water1);

        Point point2 = grid[40][20];
        Water water2 = new Water(this);
        water2.setX(point2.x);
        water2.setY(point2.y);
        map.addEnt(water2);

        Point point3 = grid[5][15];
        Pond pond = new Pond(this);
        pond.setX(point3.x);
        pond.setY(point3.y);
        map.addEnt(pond);

        Point point4 = grid[30][20];
        Pond pond1 = new Pond(this);
        pond1.setX(point4.x);
        pond1.setY(point4.y);
        map.addEnt(pond1);

    }

    private void initTrees() {
        Point[][] grid = map.getGrid();
        Point point = grid[10][10];
        Tree tree = new Tree(this);
        tree.setX(point.x);
        tree.setY(point.y);
        map.addEnt(tree);

        Point point1 = grid[10][20];
        Bush bush = new Bush(this);
        bush.setX(point1.x);
        bush.setY(point1.y);
        map.addEnt(bush);

        Point point2 = grid[40][7];
        Grass grass = new Grass(this);
        grass.setX(point2.x);
        grass.setY(point2.y);
        map.addEnt(grass);

    }

    private void initRoad(){
        // Get the grid from the map
        Point[][] grid = map.getGrid();

        // Determine the column for the straight road (middle column)
        int middleColumn = grid[0].length / 2;

        // Iterate through all rows to place roads in the middle column
        for (int row = 0; row < grid.length; row++) {
            Point point = grid[row][middleColumn];
            Road road = new Road(this);
            road.setX(point.x);
            road.setY(point.y);
            road.row = row;
            road.col = middleColumn;

            // Add the road to the map and the jeep handler
            map.addEnt(road);
            map.jeepHandler.addRoad(road);
        }
    }

    @Override
    public void run() {
        double interval = 1000.0 / (double) FPS;
        double delta = 0;
        double lastTime = currentTimeMillis();
        double currTime;
        double timer = 0;
        double drawCount = 0;

        while (gameThread != null) {
            if (!paused) {
                currTime = currentTimeMillis();
                delta += (currTime - lastTime) / interval;
                timer += (currTime - lastTime);
                lastTime = currTime;

                if (delta >= 1) {

                    repaint();

                    updateInRespectToTimeSpeed();
                    canUpdateStats = false;
                    // Update the stats panel
                    if (statsPanel != null) {
                        statsPanel.updateStats();
                    }

                    delta--;
                    drawCount++;
                }

                if (timer >= 1000) {
                    canUpdateStats = true;
                    spawnPoacher();
                    // Store fps for display
                    fps = (int) drawCount;
                    drawCount = 0;
                    timer = 0;
                    passedTime += 1; /// Adds a second to the time passed in-game
                }
            } else {
                // When paused, just update the UI occasionally and reset timing variables
                try {
                    Thread.sleep(100);  // Don't hog the CPU when paused
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Reset timing to prevent huge jumps when unpausing
                lastTime = currentTimeMillis();

                // Still update the stats panel to show paused state
                if (statsPanel != null) {
                    statsPanel.updateStats();
                }

                // Still process mouse clicks when paused (to allow unpausing via UI)
                if (mouseHandler.mPressed) {
                    handleMouseDrag();
                    mouseHandler.mReleased = false;
                    mouseHandler.mDragging = false;
                } else if (mouseHandler.mClicked) {
                    handleMouseClick();
                    mouseHandler.mClicked = false;
                }
            }

            // Process mouse clicks whether paused or not
            if (mouseHandler.mClicked) {
                handleMouseClick();
                mouseHandler.mClicked = false;
            }
        }
    }

    private void spawnPoacher() {
        if (passedTime % 60 == 0) {
            Poacher poacher = new Poacher(this, map, keyHandler, mouseHandler);
            map.addEnt(poacher);
        }
    }


    private void handleMouseClick() {
        int mouseX = mouseHandler.lastX;
        int mouseY = mouseHandler.lastY;

        // Only process entity clicks (inventory clicks are handled by Swing components)
        Entity clickedEntity = map.getEntityAt(mouseX, mouseY);

        if (clickedEntity != null ) {
            if (entitySelected && selectedEntity != null) {
                if (selectedEntity.equals(clickedEntity)) {
                    selectedEntity.deSelect();
                    selectedEntity = null;
                    entitySelected = false;
                    return;
                }
                if (selectedEntity instanceof Ranger ) {
                    // Call performAction on the newly selected entity
                    Ranger ranger = (Ranger) selectedEntity;
                    ranger.performAction(clickedEntity);
                }
                selectedEntity.deSelect();
            }
            clickedEntity.select();
            selectedEntity = clickedEntity;
            entitySelected = true;


        } else {
            // If we have a selected item from inventory, try to place it
            if (inventory != null && inventory.hasSelectedItem()) {
                placeSelectedItem(inventory.getSelectedItem());
            } else if (entitySelected && selectedEntity != null) {
                selectedEntity.deSelect();
                selectedEntity = null;
                entitySelected = false;
            }
        }
    }


    public void update() {
        // Don't update if game is over
        if (gameOver) {
            return;
        }

        // Apply speed multiplier to update frequency
        int multiplier = getMultiplier();

        // Update all entities with the appropriate speed
        if (map != null) {
            map.update();
            if (canUpdateStats) {
                updatePopularity();
            }
        }
    }

    /**
     * Checks if the player has met win or loss conditions
     */
    private void checkWinLossConditions() {
        // Check for loss conditions first (these end the game immediately)
        if (checkLossConditions()) {
            return; // Game is already over
        }

        // Check for win conditions (these require consecutive months of success)
        checkWinConditions();
    }

    /**
     * Checks if the player has lost the game
     * @return true if the game is lost, false otherwise
     */
    private boolean checkLossConditions() {
        // Check for bankruptcy
        if (cash <= 0) {
            handleGameLoss("You've gone bankrupt! Game over.");
            return true;
        }

        // Check for animal extinction
        if (map.getHerbivoreNum() == 0 && map.getCarnivoreNum() == 0) {
            handleGameLoss("All animals have become extinct! Game over.");
            return true;
        }

        return false;
    }

    /**
     * Checks if the player has met the win conditions for the current month
     */
    private void checkWinConditions() {
        // Get the current month
        long currentMonth = getMonthsPassed();

        // Only check once per month
        if (currentMonth == lastCheckedMonth) {
            return;
        }

        // Update the last checked month
        lastCheckedMonth = currentMonth;

        // Get thresholds for the current difficulty (adjust for 0-based indexing)
        int difficultyIndex = difficulty - 1;
        int requiredVisitors = WIN_THRESHOLDS[difficultyIndex][0];
        int requiredHerbivores = WIN_THRESHOLDS[difficultyIndex][1];
        int requiredCarnivores = WIN_THRESHOLDS[difficultyIndex][2];
        int requiredCash = WIN_THRESHOLDS[difficultyIndex][3];

        // Check if all thresholds are met
        int currentVisitors = getVisitors();
        int currentHerbivores = map.getHerbivoreNum();
        int currentCarnivores = map.getCarnivoreNum();

        boolean thresholdsMet =
            currentVisitors >= requiredVisitors &&
            currentHerbivores >= requiredHerbivores &&
            currentCarnivores >= requiredCarnivores &&
            cash >= requiredCash;

        if (thresholdsMet) {
            // Increment consecutive successful months
            consecutiveSuccessfulMonths++;
            System.out.println("Month " + currentMonth + ": All thresholds met! " +
                              consecutiveSuccessfulMonths + "/" + REQUIRED_MONTHS[difficultyIndex] +
                              " consecutive months.");

            // Check if player has won
            if (consecutiveSuccessfulMonths >= REQUIRED_MONTHS[difficultyIndex]) {
                handleGameWin();
            }
        } else {
            // Reset consecutive months if thresholds are not met
            if (consecutiveSuccessfulMonths > 0) {
                System.out.println("Month " + currentMonth + ": Thresholds not met. Consecutive months reset to 0.");
                System.out.println("Current stats: Visitors=" + currentVisitors + "/" + requiredVisitors +
                                 ", Herbivores=" + currentHerbivores + "/" + requiredHerbivores +
                                 ", Carnivores=" + currentCarnivores + "/" + requiredCarnivores +
                                 ", Cash=" + cash + "/" + requiredCash);
            }
            consecutiveSuccessfulMonths = 0;
        }
    }

    /**
     * Handles game win scenario
     */
    private void handleGameWin() {
        gameWon = true;
        gameOver = true;
        pauseGame();

        // Display win message
        String difficultyName = "";
        switch (difficulty) {
            case 1: difficultyName = "Easy"; break;
            case 2: difficultyName = "Medium"; break;
            case 3: difficultyName = "Hard"; break;
        }

        String winMessage = "Congratulations! You've successfully managed your zoo for " +
                          REQUIRED_MONTHS[difficulty - 1] + " consecutive months on " +
                          difficultyName + " difficulty!\n\n" +
                          "Final Stats:\n" +
                          "Visitors: " + getVisitors() + "\n" +
                          "Herbivores: " + map.getHerbivoreNum() + "\n" +
                          "Carnivores: " + map.getCarnivoreNum() + "\n" +
                          "Cash: $" + cash;

        JOptionPane.showMessageDialog(this, winMessage, "Victory!", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles game loss scenario
     * @param message The reason for the loss
     */
    private void handleGameLoss(String message) {
        gameOver = true;
        pauseGame();

        // Display loss message
        String lossMessage = message + "\n\n" +
                           "Final Stats:\n" +
                           "Months survived: " + getMonthsPassed() + "\n" +
                           "Visitors: " + getVisitors() + "\n" +
                           "Herbivores: " + map.getHerbivoreNum() + "\n" +
                           "Carnivores: " + map.getCarnivoreNum() + "\n" +
                           "Cash: $" + cash;

        JOptionPane.showMessageDialog(this, lossMessage, "Game Over", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Calculates a diversity score based on the different types of animals present
     * @return A score representing animal diversity
     */
    private int calculateAnimalDiversity() {
        // Base diversity on the number of different animal types
        int diversityScore = 0;

        // Check for presence of each animal type
        if (!map.lions.isEmpty()) diversityScore++;
        if (!map.wolves.isEmpty()) diversityScore++;
        if (!map.giraffes.isEmpty()) diversityScore++;
        if (!map.buffalos.isEmpty()) diversityScore++;

        // Bonus for having both herbivores and carnivores
        if (!map.herbivores.isEmpty() && !map.carnivores.isEmpty()) {
            diversityScore += Math.min(map.herbivores.size(), map.carnivores.size()) / 2;
        }

        return diversityScore;
    }

    /**
     * Calculates a visitor experience score based on available buildings and infrastructure
     * @return A score representing visitor experience quality
     */
    private int calculateVisitorExperience() {
        int experienceScore = 0;
        Map<String, Integer> buildings = getAvailableBuildings();

        // More roads improve visitor mobility
        experienceScore += Math.min(10, buildings.getOrDefault("Road", 0) / 5);

        // Water features enhance experience
        experienceScore += buildings.getOrDefault("Pond", 0) * 2;
        experienceScore += buildings.getOrDefault("Water", 0);

        // Natural elements improve aesthetics
        experienceScore += buildings.getOrDefault("Tree", 0);
        experienceScore += buildings.getOrDefault("Bush", 0);
        experienceScore += buildings.getOrDefault("Grass", 0) / 2;

        return experienceScore;
    }

    /**
     * Returns a list of all buildings currently in the map
     * @return List of building types and their counts
     */
    public Map<String, Integer> getAvailableBuildings() {
        Map<String, Integer> buildingCounts = new HashMap<>();

        // Count each type of building
        for (Building building : map.buildings) {
            String buildingType = building.getClass().getSimpleName();
            buildingCounts.put(buildingType, buildingCounts.getOrDefault(buildingType, 0) + 1);
        }

        return buildingCounts;
    }

    /**
     * Updates the zoo's popularity score and adds new tourists once per day
     */
    protected void updatePopularity() {
        // Calculate the current popularity score
        int basePopularity = map.jeepHandler.getPopularity();
        int diversityBonus = calculateAnimalDiversity() * 3;
        int experienceBonus = calculateVisitorExperience();

        // Update the popularity score
        popularity = basePopularity + diversityBonus + experienceBonus;

        // Check if a new day has started
        if (hasNewDayStarted()) {
            // Calculate new tourists based on popularity score, but in smaller groups
            // Use a scaling factor to reduce the number of tourists
            Random rand = new Random(currentTimeMillis());

            // Calculate a base number of tourists (smaller than before)
            int baseVisitors = Math.max(popularity / 3, 1); // Divide by 3 to make groups smaller

            // Add some randomness to the number of tourists (±20%)
            double randomFactor = 0.8 + (rand.nextDouble() * 0.4); // Between 0.8 and 1.2
            int newTourists = (int)(baseVisitors * randomFactor);

            // Ensure at least 1 tourist arrives
            newTourists = Math.max(newTourists, 1);

            // Check if adding new tourists would exceed the maximum capacity
            int currentVisitors = map.jeepHandler.getVisitors();
            if (currentVisitors >= MAX_TOURISTS) {
                // Zoo is at maximum capacity, no new tourists can enter
                newTourists = 0;

            } else {
                // Limit new tourists to available capacity
                int availableCapacity = MAX_TOURISTS - currentVisitors;
                if (newTourists > availableCapacity) {
                    newTourists = availableCapacity;
                }

                // Add the new tourists (respecting zoo capacity)
                int actualTouristsAdded = map.jeepHandler.addVisitor(newTourists);
                visitorNum += actualTouristsAdded;

                // Update newTourists to reflect the actual number added
                newTourists = actualTouristsAdded;
            }

            // Tourists pay once when they arrive (only if new tourists actually entered)
            if (newTourists > 0) {
                // Base entrance fee scales with zoo quality (diversity + experience)
                int baseEntranceFee = 10;
                int qualityBonus = (diversityBonus + experienceBonus) / 5; // Scale down the bonus
                int entranceFee = baseEntranceFee + qualityBonus;
                cash += newTourists * entranceFee;

            }

            // Random chance of some tourists leaving
            if (rand.nextInt() % 2 == 0) {
                int leavingVisitors = Math.min(visitorsToAdd, map.jeepHandler.getVisitors() / 4);
                map.jeepHandler.removeVisitor(leavingVisitors);
            }
        }

        // No continuous cash updates - tourists only pay once when they arrive
    }
//
//    protected void updateJeeps() {
//        int dv = map.getJeepNum() - visitorNum / 4;
//        for (int i = 0; i < dv; i++) {
//            map.addEnt(new Jeep(this, map));
//        }
//    }

    public void pauseGame() {
        if (!paused) {
            // Store the last update time when pausing to ensure accurate time tracking
            updateAccumulatedTime(); // Update time before pausing
            paused = true;

            // Update the stats panel to reflect paused state
            if (statsPanel != null) {
                statsPanel.updateStats();
            }


        }
    }

    public void resumeGame() {
        if (paused) {
            // Reset the last update time when resuming to prevent time jumps
            lastUpdateTime = currentTimeMillis();
            paused = false;

            // Update stats panel to reflect the resumed state
            if (statsPanel != null) {
                statsPanel.updateStats();
            }


        }
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw game map and entities
        if (map != null) {
            map.paintComponents(g2d);
        }

        // Draw grid if needed
        if (inventory != null && inventory.hasSelectedItem()) {
            map.drawGrid(g2d);
        }
    }

    // Panel drawing has been moved to InventoryPanel

    private int fps = 0;

    public int getMultiplier() {
        switch (timeSpeed) {
            case 1: // Hours
                return 3600;         // 1 real second = 1 game hour
            case 2: // Days
                return 86400;        // 1 real second = 1 game day (24 game hours)
            case 3: // Weeks
                return 604800;       // 1 real second = 1 game week (168 game hours)
            default: // Seconds (normal speed) - fallback
                timeSpeed = 1;       // Reset to hour if somehow gets to an invalid state
                return 3600;         // Default to hour speed
        }
    }

    // Update the accumulated game time with improved initialization
    private void updateAccumulatedTime() {
        // Only update time if the game is not paused
        if (!paused) {
            long currentTime = currentTimeMillis();

            // Ensure we don't have a zero or negative time difference
            if (lastUpdateTime <= 0) {
                lastUpdateTime = currentTime;
                return;
            }

            // Calculate elapsed real milliseconds since last update
            long elapsedMillis = Math.max(0, currentTime - lastUpdateTime);

            // Add game time according to the current speed
            accumulatedGameMillis += elapsedMillis * getMultiplier();

            // Update last update time
            lastUpdateTime = currentTime;
        }
    }

    // Cycle to the next speed level - using your friend's implementation
    public void cycleSpeed() {
        // Update accumulated time before changing speed
        updateAccumulatedTime();

        // Cycle to next speed (1-3)
        timeSpeed = (timeSpeed % 3) + 1;


    }

    public String getTimePassed() {
        if (!gameStarted) return "00:00:00:00";

        // Only update accumulated time when not paused
        if (!paused) {
            updateAccumulatedTime();
        }

        // Convert accumulated milliseconds to hours
        long totalHours = accumulatedGameMillis / (1000 * 3600); // Convert ms to hours

        // Calculate time units
        int hoursInDay = 24;
        int hoursInWeek = 7 * hoursInDay;
        int hoursInMonth = 30 * hoursInDay;

        long months = totalHours / hoursInMonth;
        totalHours %= hoursInMonth;

        long weeks = totalHours / hoursInWeek;
        totalHours %= hoursInWeek;

        long days = totalHours / hoursInDay;
        long hours = totalHours % hoursInDay;

        return String.format("%02d:%02d:%02d:%02d", months, weeks, days, hours);
    }

    public long getMonthsPassed() {
        if (!gameStarted) return 0;

        // Only update accumulated time when not paused
        if (!paused) {
            updateAccumulatedTime();
        }

        // Convert accumulated milliseconds to hours
        long totalHours = accumulatedGameMillis / (1000 * 3600); // Convert ms to hours

        // Calculate time units
        int hoursInDay = 24;
        int hoursInWeek = 7 * hoursInDay;
        int hoursInMonth = 30 * hoursInDay;

        long months = totalHours / hoursInMonth;

        return months;
    }

    /**
     * Gets the current game day (total days since game start)
     * @return The current game day
     */
    public long getCurrentGameDay() {
        if (!gameStarted) return 0;

        // Only update accumulated time when not paused
        if (!paused) {
            updateAccumulatedTime();
        }

        // Convert accumulated milliseconds to hours
        long totalHours = accumulatedGameMillis / (1000 * 3600); // Convert ms to hours

        // Calculate time units
        int hoursInDay = 24;
        int hoursInWeek = 7 * hoursInDay;
        int hoursInMonth = 30 * hoursInDay;

        // Calculate total days
        long months = totalHours / hoursInMonth;
        totalHours %= hoursInMonth;

        long weeks = totalHours / hoursInWeek;
        totalHours %= hoursInWeek;

        long days = totalHours / hoursInDay;

        // Convert all to days
        return (months * 30) + (weeks * 7) + days;
    }

    /**
     * Checks if a new day has started since the last check
     * @return true if a new day has started, false otherwise
     */
    public boolean hasNewDayStarted() {
        long currentDay = getCurrentGameDay();

        // If this is the first check or a new day has started
        if (lastCheckedDay == -1 || currentDay > lastCheckedDay) {
            lastCheckedDay = currentDay;
            return true;
        }

        return false;
    }

    public String getSpeedLevel() {
        switch (timeSpeed) {
            case 1:
                return "hour";
            case 2:
                return "day";
            case 3:
                return "week";
            default:
                return "second";
        }
    }

    public int getCash() {
        return cash;
    }

    public void updateCash(int amount) {
        this.cash += amount;
    }

    public int getVisitors() {
//        return map.visitors.size();
        return map.jeepHandler.getVisitors();
    }

    public int getHerbivores() {
        return map.getHerbivoreNum();
    }

    public int getCarnivores() {
        return map.getCarnivoreNum();
    }

    public MapSpace getMap() {
        return map;
    }

    public void playMusic(int i) {
        sound.setFile(i);
//        sound.play();
//        sound.loop();
    }

    public void stopMusic() {
        sound.stop();
    }

    /**
     * Delegates item placement to the Inventory class
     *
     * @param item The item to place
     */
    public void placeSelectedItem(Item item) {

        Entity newEntity = null;
        switch (item) {
            // Animals
            case LION:
                newEntity = new Lion(this, map);
                break;
            case GIRAFFE:
                newEntity = new Giraffe(this, map);
                break;
            case BUFFALO:
                newEntity = new Buffalo(this, map, keyHandler, mouseHandler);
                break;
            case WOLF:
                newEntity = new Wolf(this, map);
                break;

            // Infrastructure
            case ROAD:
                newEntity = new Road(this);
                break;
            case POND:
                newEntity = new Pond(this);
                break;
            case TREE:
                newEntity = new Tree(this);
                break;
            case GRASS:
                newEntity = new Grass(this);
                break;
            case BUSH:
                newEntity = new Bush(this);
                break;
            case WATER:
                newEntity = new Water(this);
                break;

            case CHARGING_STATION:
                newEntity = new ChargingStation(this);
                break;

            // Vehicles and Personnel
            case JEEP:
                if (!jeepAndVisitorHandler.hasRoad()) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot place drone: You need to place a roads connected to the start first!",
                            "Placement Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                newEntity = new Jeep(this, map);
                break;
            case RANGER:

                newEntity = new Ranger(this, map, keyHandler, mouseHandler);
                break;

            // Surveillance
            case DRONE:
                // Check if there's at least one charging station on the map
                if (map.chargingStations.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot place drone: You need to place a charging station first!",
                            "Placement Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                newEntity = new Drone(this, map, keyHandler, mouseHandler);
                break;
            case AIR_SHIP:
                newEntity = new AirShip(this, map, keyHandler, mouseHandler);
                break;
            case AIRSHIP_PATROL_FLAG:
                newEntity = new AirShipPatrolFlag(this);
                break;
            case CAMERA:
                newEntity = new Camera(this, map);
                break;
            case DRONE_PATROL_FLAG:
                newEntity = new DronePatrolFlag(this);
                break;

            // Threats
            case POACHER:
                newEntity = new Poacher(this, map, keyHandler, mouseHandler);
                break;
        }

        if (newEntity != null) {
            newEntity.setX(mouseHandler.lastX);
            newEntity.setY(mouseHandler.lastY);
            if (!map.isBlocked(newEntity, 0, 0)) {
                map.addEnt(newEntity);
                inventory.removeItem(item);
                refreshInventoryUI();
            }
        }
        refreshInventoryUI();
    }

    /**
     * Refreshes the inventory UI . Call after changes
     */
    public void refreshInventoryUI() {
        if (inventoryPanel != null) {
            inventoryPanel.refreshInventorySlots();
        }
        if (statsPanel != null) {
            statsPanel.updateStats();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @return The game's key handler
     */
    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    /**
     * @return The game's mouse handler
     */
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public boolean isEntityAtHand() {
        return entityAtHand != null;
    }

    public void putEntityInHand(Entity entity) {
        entityAtHand = entity;
    }

    public void updateInRespectToTimeSpeed() {
        checkWinLossConditions();
        switch (timeSpeed) {
            case 1:
                update();
                break;
            case 2:
                update();
                update();
                break;
            case 3:
                update();
                update();
                update();
                break;
            default:
                update();
                break;
        }
    }

    public void handleMouseDrag() {
        if (inventory != null && inventory.hasSelectedItem() && inventory.getSelectedItem() == Item.ROAD) {
            Entity newEntity = new Road(this);
            if (!map.isBlocked(newEntity, 0, 0)) {
                newEntity.setX(mouseHandler.lastX);
                newEntity.setY(mouseHandler.lastY);
                map.addEnt(newEntity);
            }
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Checks if the game is over (either won or lost)
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Checks if the player has won the game
     * @return true if the player has won, false otherwise
     */
    public boolean isGameWon() {
        return gameWon;
    }

    /**
     * Gets the number of consecutive months the player has met all thresholds
     * @return The number of consecutive successful months
     */
    public int getConsecutiveSuccessfulMonths() {
        return consecutiveSuccessfulMonths;
    }

    /**
     * Gets the required number of consecutive months to win based on current difficulty
     * @return The required number of consecutive months
     */
    public int getRequiredConsecutiveMonths() {
        return REQUIRED_MONTHS[difficulty - 1];
    }

    /**
     * Gets the win thresholds for the current difficulty
     * @return An array containing [requiredVisitors, requiredHerbivores, requiredCarnivores, requiredCash]
     */
    public int[] getCurrentWinThresholds() {
        return WIN_THRESHOLDS[difficulty - 1];
    }
}

