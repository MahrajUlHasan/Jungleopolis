package entity.util;

import entity.Item.*;

import org.example.MapSpace;

import java.util.*;
import java.awt.*;

import static java.lang.System.currentTimeMillis;

public class jeepAndVisitorHandler {
    protected MapSpace map;
    protected ArrayList<Jeep> jeeps;
    protected static Road[][] roads;
    protected Point[][] grid;
    public static Road start;
    public Road end;
    protected int visitors = 0;


    public jeepAndVisitorHandler(MapSpace map) {
        this.map = map;
        jeeps = new ArrayList<>();
        grid = map.getGrid();
        roads = new Road[grid.length][grid[0].length];
    }

    public static int roadNum() {
        int count = 0;
        for (Road[] r : roads) {
            for (Road r1 : r) {
                if (r1 != null) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public static boolean hasRoad() {
        int i = start.row;
        int j = start.col;
        return (roads[i + 1][j] != null) || (roads[i][j + 1] != null) || (roads[i][j - 1] != null);
    }

    public void init(MapSpace map) {
        Point p = grid[0][grid[0].length / 2];
        start = new Road(map.game);
        start.setX(p.x);
        start.setY(p.y);
        p = grid[grid.length - 1][grid[0].length / 2];
        end = new Road(map.game);
        end.setX(p.x);
        end.setY(p.y);
        map.addEnt(start);

        map.addEnt(end);
    }

    public Road nextRoad(Jeep jeep) {
        if (jeep.currRoad == null) {
            return start; // Start at the beginning
        }
//
//        if (jeep.currRoad == end) {
//            jeep.inReverse = true; // Reverse when reaching the end
//        } else if (jeep.currRoad == start) {
//            jeep.inReverse = false; // Stop reversing when back at the start
//        }

        Road target = jeep.inReverse ? start : end;
        Road next = findPath(jeep, jeep.currRoad, target);

        if (next != null ) {
            if (jeep.collides(start) ) {
                pickupVisitors(jeep);
            }

            if (next.equals(end)) {
                jeep.inReverse = true;
            }
            return next; // Move to the next road if it's not occupied
        }
        return jeep.currRoad; // Stay in the current road if no valid move
    }

    private Road findPath(Jeep j, Road start, Road target) {
        ArrayList<Road> validroads = new ArrayList<>();
        for (Road r : getAdjacentRoads(start)) {
//           if(start == target)
//           {
//               j.inReverse = !j.inReverse;
//               return null;
//           }
            if (r == target) {
                return r;
            } else if (r != j.prevRoad) {
                validroads.add(r);
            }

        }
        if (validroads.isEmpty()) return null;
        Random rand = new Random(currentTimeMillis());
        int ind = rand.nextInt(validroads.size());
        return validroads.get(ind);

    }

    private ArrayList<Road> getAdjacentRoads(Road road) {
        ArrayList<Road> neighbors = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, // Up
                {1, 0},  // Down
                {0, -1}, // Left
                {0, 1}   // Right
        };

        for (int[] dir : directions) {
            int newRow = road.row + dir[0];
            int newCol = road.col + dir[1];

            if (isValidRoad(newRow, newCol)) {
                neighbors.add(roads[newRow][newCol]);
            }
        }

        return neighbors;
    }

    private boolean isValidRoad(int row, int col) {
        return row >= 0 && row < roads.length &&
                col >= 0 && col < roads[0].length &&
                roads[row][col] != null;
    }


    public void addRoad(Road road) {
        roads[road.row][road.col] = road;
    }

    public Boolean isOccupied(int row, int col) {
        for (Jeep j : jeeps) {
            if (j.getHitBox().intersects(row, col, 1, 1)) {
                return true;
            }
        }
        return false;
    }

    public void addJeep(Jeep jeep) {
        jeeps.add(jeep);
        jeep.currRoad = start;
        jeep.inReverse = false;
        jeep.x = start.getX();
        jeep.y = start.getY();
    }

    public int getPopularity() {
        int total = 0;
        for (Jeep j : jeeps) {
            total += j.getPopularity();
        }
        return total;
//                / ((jeeps.size()-3) == 0 ? 1 : (jeeps.size()-3));


    }

    // Maximum number of visitors the zoo can accommodate
    private static final int MAX_VISITORS = 100;

    /**
     * Adds visitors to the zoo, respecting the maximum capacity
     * @param v Number of visitors to add
     * @return Actual number of visitors added (may be less than requested if at capacity)
     */
    public int addVisitor(int v) {
        // Check if adding would exceed maximum capacity
        if (visitors >= MAX_VISITORS) {
            return 0; // No more visitors can be added
        }

        // Calculate how many visitors can actually be added
        int availableCapacity = MAX_VISITORS - visitors;
        int actualVisitorsAdded = Math.min(v, availableCapacity);

        // Add the visitors
        visitors += actualVisitorsAdded;

        return actualVisitorsAdded;
    }


    public void drawVisitorCount(Graphics g) {
        if (start != null) {
            Graphics2D g2d = (Graphics2D) g;


            // Draw semi-transparent circle
            int circleX = start.getX() + start.getDimension().width / 4; // Adjust position as needed
            int circleY = start.getY() + start.getDimension().height; // Adjust position as needed
            int circleDiameter = 20;
            g2d.setColor(new Color(0, 0, 255, 80)); // Semi-transparent blue
            g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);

            // Draw visitor number
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(visitors), circleX + 5, circleY + 13); // Adjust text position as needed
        }
    }


    public void removeVisitor(int v) {
        if (visitors - v >= 0) {
            visitors -= v;
        }
        ;
    }

    public void pickupVisitors(Jeep jeep) {
        if(jeep.pickUpBuffer == 0) {
            jeep.passengers = Math.min(visitors, jeep.maxPassengerCount);
            visitors -= jeep.passengers;
            jeep.pickUpBuffer = 1000;
        }

    }

    public int getVisitors() {
        return visitors;
    }
}
