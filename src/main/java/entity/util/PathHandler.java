package entity.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PathHandler manages a collection of PathNode objects and provides methods to navigate between them.
 * It is used by entities like AirShip to move from one node to another in a circular path.
 */
public class PathHandler {

    protected ArrayList<PathNode> nodes;
    protected PathNode current = null;
    protected int currentIndex = 0;

    public PathHandler() {
        nodes = new ArrayList<>();
    }

    /**
     * Returns an unmodifiable view of the nodes list.
     * This allows entities to access the nodes without modifying the list.
     * @return An unmodifiable List of PathNodes
     */
    public List<PathNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * Adds a new node to the path.
     * If this is the first node, it becomes the current node.
     * @param node The PathNode to add
     */
    public void addNode(PathNode node) {
        // Check if node is already in the list to avoid duplicates
        if (!nodes.contains(node)) {
            nodes.add(node);

            // If this is the first node, set it as current
            if (nodes.size() == 1) {
                current = node;
                currentIndex = 0;
            }
        }
    }

    /**
     * Removes a node from the path.
     * @param node The PathNode to remove
     */
    public void removeNode(PathNode node) {
        int index = nodes.indexOf(node);
        if (index != -1) {
            nodes.remove(index);

            // Adjust currentIndex if necessary
            if (nodes.isEmpty()) {
                current = null;
                currentIndex = 0;
            } else if (index <= currentIndex) {
                currentIndex = currentIndex > 0 ? currentIndex - 1 : 0;
                current = nodes.get(currentIndex);
            }
        }
    }

    /**
     * Returns the next node in the path and updates the current node.
     * If there are no nodes, returns null.
     * @return The next PathNode in the sequence
     */
    public PathNode nextNode() {
        if (nodes.isEmpty()) {
            return null;
        }

        currentIndex = (currentIndex + 1) % nodes.size();
        current = nodes.get(currentIndex);
        return current;
    }

    /**
     * Returns the current node without changing position in the path.
     * @return The current PathNode
     */
    public PathNode getCurrent() {
        return current;
    }

    /**
     * Returns the number of nodes in the path.
     * @return The number of nodes
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * Checks if the path has any nodes.
     * @return true if the path has at least one node, false otherwise
     */
    public boolean hasNodes() {
        return !nodes.isEmpty();
    }
}
